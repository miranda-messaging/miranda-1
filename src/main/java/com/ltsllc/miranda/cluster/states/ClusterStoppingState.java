package com.ltsllc.miranda.cluster.states;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.StopState;
import com.ltsllc.miranda.cluster.Cluster;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.messages.NodeStoppedMessage;
import org.apache.log4j.Logger;

import java.util.List;

/**
 * Wait for all nodes to stop.
 *
 * <p>
 *     This state assumes that the nodes have already been told to stop.
 * </p>
 *
 * <p>
 *     WHEN IN THIS STATE THE CLUSTER WILL DISCARD MESSAGES OTHER THAN
 *     NodeStopped!
 * </p>
 */
public class ClusterStoppingState extends State {
    private static Logger logger = Logger.getLogger(ClusterStoppingState.class);

    public Cluster getCluster () {
        return (Cluster) getContainer();
    }

    public ClusterStoppingState (Cluster cluster) {
        super(cluster);
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case NodeStopped: {
                NodeStoppedMessage nodeStoppedMessage = (NodeStoppedMessage) message;
                nextState = processNodeStoppedMessage(nodeStoppedMessage);
                break;
            }

            default: {
                nextState = discardMessage(message);
                break;
            }
        }

        return nextState;
    }

    public State processNodeStoppedMessage (NodeStoppedMessage nodeStoppedMessage) {
        logger.info ("Node stopped " + nodeStoppedMessage.getNode());

        List<Node> list = getCluster().getNodes();
        if (!list.remove(nodeStoppedMessage.getNode())) {
            Panic panic = new Panic("Unrecognized node " + nodeStoppedMessage.getNode(), Panic.Reasons.UnrecognizedNode);
            Miranda.getInstance().panic(panic);
        }

        if (list.size() <= 0) {
            logger.info ("All nodes stopped, cluster stopping.");
            return StopState.getInstance();
        }

        return this;
    }

    public State discardMessage (Message message) {
        logger.warn (getCluster() + " is discarding " + message);

        return this;
    }
}