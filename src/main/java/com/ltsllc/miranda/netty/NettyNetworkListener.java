package com.ltsllc.miranda.netty;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.Panic;
import com.ltsllc.miranda.network.*;
import com.ltsllc.miranda.util.Utils;
import com.ltsllc.miranda.miranda.Miranda;
import com.ltsllc.miranda.property.MirandaProperties;
import com.ltsllc.miranda.node.NetworkMessage;
import com.ltsllc.miranda.node.Node;
import com.ltsllc.miranda.node.WireMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslHandler;
import org.apache.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;


/**
 * Created by Clark on 1/31/2017.
 */
public class NettyNetworkListener {
    private static class LocalHandler extends ChannelInboundHandlerAdapter {
        private Logger logger = Logger.getLogger(LocalHandler.class);
        private BlockingQueue<Message> node;

        public LocalHandler (BlockingQueue<Message> node) {
            this.node = node;
        }

        public void channelRead (ChannelHandlerContext channelHandlerContext, Object message) {
            ByteBuf byteBuf = (ByteBuf) message;
            byte[] buffer = new byte[byteBuf.readableBytes()];
            byteBuf.getBytes(0, buffer);
            String s = new String(buffer);
            logger.info("Got " + s);

            JsonParser jsonParser = new JsonParser(s);

            for (WireMessage wireMessage : jsonParser.getMessages()) {
                NetworkMessage networkMessage = new NetworkMessage(null, this, wireMessage);
                Consumer.staticSend(networkMessage, node);
            }
        }

        @Override
        public void channelInactive(ChannelHandlerContext ctx) throws Exception {
            ctx.close();

            ConnectionClosedMessage connectionClosedMessage = new ConnectionClosedMessage(null, this);
            Consumer.staticSend(connectionClosedMessage, node);
        }
    }


    private static class LocalInitializer extends ChannelInitializer<SocketChannel> {
        private static Logger logger = Logger.getLogger(LocalInitializer.class);

        private SslContext sslContext;

        public LocalInitializer (SslContext sslContext) {
            this.sslContext = sslContext;
        }

        public void initChannel (SocketChannel socketChannel) {
            InetSocketAddress inetSocketAddress = (InetSocketAddress) socketChannel.remoteAddress();
            logger.info("Got connection from " + inetSocketAddress);

            if (null != sslContext) {
                SslHandler sslHandler = sslContext.newHandler(socketChannel.alloc());
                socketChannel.pipeline().addLast(sslHandler);
            }

            NettyHandle nettyHandle = new NettyHandle(-1, Network.getInstance().getQueue(), socketChannel);
            int handle = Network.getInstance().newConnection(nettyHandle);

            Node node = new Node(inetSocketAddress, handle);
            node.start();

            NewConnectionMessage newConnectionMessage = new NewConnectionMessage(null, this, node);
            Consumer.staticSend(newConnectionMessage, Miranda.getInstance().getQueue());

            LocalHandler localHandler = new LocalHandler(node.getQueue());
            socketChannel.pipeline().addLast(localHandler);
       }


        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            logger.error("Exception caught, closing channel", cause);
            ctx.close();
        }
    }

    private static class LocalChannelListener implements ChannelFutureListener {
        private static Logger logger = Logger.getLogger(LocalChannelListener.class);

        private BlockingQueue<Channel> channelQueue;

        public BlockingQueue<Channel> getChannelQueue () {
            return channelQueue;
        }

        public LocalChannelListener (BlockingQueue<Channel> channelQueue) {
            this.channelQueue = channelQueue;
        }

        public void operationComplete (ChannelFuture channelFuture) {
            if (channelFuture.isSuccess())
            {
                InetSocketAddress inetSocketAddress = (InetSocketAddress) channelFuture.channel().remoteAddress();

                //
                // for some reason, we occasionally get empty connections
                //
                if (null == inetSocketAddress) {
                    logger.error("spurious connection");
                    return;
                }

                try {
                    getChannelQueue().put(channelFuture.channel());

                    LocalHandler localHandler = new LocalHandler(Network.getInstance().getQueue());
                    channelFuture.channel().pipeline().addLast(localHandler);
                } catch (InterruptedException e) {
                    Panic panic = new Panic("Interrupted while trying to put new connection on queue", e, Panic.Reasons.ExceptionDuringNewConnection);
                    boolean continuePanic = Network.getInstance().panic(panic);
                    if (continuePanic) {
                        channelFuture.channel().close();
                    }
                }
            }
        }
    }

    private Logger logger = Logger.getLogger(NettyNetworkListener.class);

    private int port;
    private BlockingQueue<Message> cluster;
    private BlockingQueue<Channel> channelQueue = new LinkedBlockingQueue<Channel>();
    private ServerBootstrap serverBootstrap;

    public NettyNetworkListener(int port, BlockingQueue<Message> cluster) {
        this.port = port;
        this.cluster = cluster;
    }

    public int getPort() {
        return port;
    }

    public BlockingQueue<Message> getCluster() {
        return cluster;
    }

    public BlockingQueue<Channel> getChannelQueue() {
        return channelQueue;
    }

    public ServerBootstrap getServerBootstrap() {
        return serverBootstrap;
    }

    public void setServerBootstrap(ServerBootstrap serverBootstrap) {
        this.serverBootstrap = serverBootstrap;
    }

    public void startup () {
        MirandaProperties properties = Miranda.properties;
        MirandaProperties.EncryptionModes mode = properties.getEncrptionModeProperty(MirandaProperties.PROPERTY_ENCRYPTION_MODE);
        SslContext sslContext = null;

        if (mode == MirandaProperties.EncryptionModes.LocalCA || mode == MirandaProperties.EncryptionModes.RemoteCA)
        {
            String keyStoreFilename = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE);
            String keyStorePassword = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_PASSWORD);
            String keyStoreAlias = properties.getProperty(MirandaProperties.PROPERTY_KEYSTORE_ALIAS);

            String certificateFilename = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE);
            String certificatePassword = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_PASSWORD);
            String certificateAlias = properties.getProperty(MirandaProperties.PROPERTY_TRUST_STORE_ALIAS);

            sslContext = Utils.createServerSslContext(keyStoreFilename, keyStorePassword, keyStoreAlias, certificateFilename, certificatePassword, certificateAlias);
        }

        LocalInitializer localInitializer = new LocalInitializer(sslContext);

        ServerBootstrap serverBootstrap = Utils.createServerBootstrap(localInitializer);
        setServerBootstrap(serverBootstrap);

        logger.info ("listening at " + port);
        serverBootstrap.bind(port);
        ChannelFuture channelFuture = null;

        channelFuture = serverBootstrap.bind(port);

        LocalChannelListener localChannelListener = new LocalChannelListener(getChannelQueue());
        channelFuture.addListener(localChannelListener);
    }


    public Handle nextConnection () {
        try {
            Channel channel = getChannelQueue().take();
            NettyHandle nettyHandle = new NettyHandle(-1, Network.getInstance().getQueue(), channel);
            return nettyHandle;
        } catch (InterruptedException e) {
            Panic panic = new Panic ("Interrupted while waiting for next connection", e, Panic.Reasons.ExceptionWhileWaitingForNextConnection);
            boolean continuePanic = Network.getInstance().panic(panic);
            if (continuePanic) {
                shutdown();
            }
        }

        return null;
    }


    public void shutdown () {
        //
        // we should release the port, but I don't know how to do that with
        // netty
        //
    }
}