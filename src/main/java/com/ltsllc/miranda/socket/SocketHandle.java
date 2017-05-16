/*
 * Copyright 2017 Long Term Software LLC
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ltsllc.miranda.socket;

import com.google.gson.Gson;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.network.Handle;
import com.ltsllc.miranda.network.NetworkException;
import com.ltsllc.miranda.network.messages.SendNetworkMessage;
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
    private static Gson ourGson = new Gson();

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


    public void send (SendNetworkMessage sendNetworkMessage) throws NetworkException {
        try {
            String json = ourGson.toJson(sendNetworkMessage.getWireMessage());
            outputStream.write(json.getBytes());
            outputStream.flush();
        } catch (IOException e) {
            throw new NetworkException ("Exception trying to sendToMe or flush message", e, NetworkException.Errors.ExceptionSending);
        }
    }

    public void close () {
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
