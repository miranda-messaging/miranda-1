package com.ltsllc.miranda;

import org.apache.log4j.Logger;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/1/2017.
 */
public class Consumer extends Subsystem {

    private static Logger logger = Logger.getLogger(Consumer.class);

    private State currentState;

    public State getCurrentState () {
        return currentState;
    }

    public void setCurrentState (State s) {
        currentState = s;
    }

    public Consumer (String name)
    {
        super(name);
    }



    public void start (BlockingQueue<Message> queue) {
        setQueue(queue);
        register(getName(), getQueue());
        start();
    }


    /**
     * run a Consumer.
     *
     * <P>
     * This method implents {@link Runnable#run()}.
     * </P>
     *
     * This method simply takes the next message off the object's queue
     * and processes it.  By defualt, the method sotps when the next
     * Stat is an instance of {@link StopState}.
     */
    public void run () {
        State nextState = getCurrentState().start();
        State stop = StopState.getInstance();

        try {
            logger.info (this + " starting");
            while (nextState != stop) {
                State currentState = getCurrentState();
                setCurrentState(nextState);
                if (currentState != nextState) {
                    setCurrentState(nextState.start());
                }

                Message m = getQueue().take();
                logger.info (this + " received " + m);
                nextState = processMessage(m);
            }
            logger.info (this + " terminating");
        } catch (InterruptedException e) {
            logger.warn("InterruptedException while trying to get next message" + e);
        }
    }

    /**
     * Process the next message.
     *
     * The defualt implementation ignores the message and returns {@link #getCurrentState()}.
     *
     * @param m The message to prcess
     * @return The next state.
     */
    public State processMessage (Message m)
    {
        return getCurrentState().processMessage(m);
    }


    public static void send (Message m, BlockingQueue<Message> queue)
    {
        logger.info("Sending " + m);
        try {
            queue.put(m);
        } catch (InterruptedException e) {
            logger.info("Exception trying to send message", e);
        }
    }
}
