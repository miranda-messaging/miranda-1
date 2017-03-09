package com.ltsllc.miranda.socket;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.CloseMessage;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.SendMessageMessage;
import com.ltsllc.miranda.util.Utils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;

/**
 * Information about a handle.
 *
 * <p>
 *     Due to the difficulty of using TLS with nio, this class uses a
 *     separate tread for each socket.
 * </p>
 */
public class SocketHandle extends Handle {
    private static Logger logger = Logger.getLogger(SocketHandle.class);

    private Socket socket;
    private OutputStream outputStream;
    private InputStream inputStream;
    private SocketListener socketListener;

    public SocketHandle (BlockingQueue<Message> notify, Socket socket) throws NetworkException {
        super(notify);

        this.socket = socket;

        try {
            this.inputStream = socket.getInputStream();
            this.outputStream = socket.getOutputStream();
            this.socketListener = new SocketListener(socket, notify);
        } catch (IOException e) {
            throw new NetworkException (e, NetworkException.Errors.ErrorGettingStreams);
        }
    }


    public InputStream getInputStream() {
        return inputStream;
    }

    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Socket getSocket() {
        return socket;
    }


    public void send (SendMessageMessage sendMessageMessage) throws NetworkException {
        try {
            outputStream.write(sendMessageMessage.getContent());
            outputStream.flush();
        } catch (IOException e) {
            throw new NetworkException ("Exception trying to send or flush message", e, NetworkException.Errors.ExceptionSending);
        }
    }

    public void close (CloseMessage disconnectMessage) {
        Utils.closeLogExceptions(getInputStream(), logger);
        Utils.closeLogExceptions(getOutputStream(), logger);
        Utils.closeLogExceptions(getSocket(), logger);

        socketListener.terminate();
    }

    public void panic () {
        Utils.closeIgnoreExceptions(getInputStream());
        Utils.closeIgnoreExceptions(getOutputStream());
        Utils.closeIgnoreExceptions(getSocket());

        socketListener.terminate();
    }

}
