package com.ltsllc.miranda.node;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.cluster.messages.ClusterFileMessage;
import com.ltsllc.miranda.miranda.Miranda;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 1/29/2017.
 */
public class JoiningState extends NodeState {
    private Logger logger = Logger.getLogger(JoiningState.class);

    private Node node;

    public Node getNode() {
        return node;
    }

    public JoiningState(Node  n) {
        super(n);

        this.node = n;
    }

    public State processMessage (Message m) {
        State nextState = this;

        switch (m.getSubject()) {
            case NetworkMessage: {
                NetworkMessage networkMessage = (NetworkMessage) m;
                nextState = processNetworkMessage(networkMessage);
                break;
            }

            case GetClusterFile: {
                GetClusterFileMessage getClusterFileMessage = (GetClusterFileMessage) m;
                nextState = processGetClusterFileMessage (getClusterFileMessage);
                break;
            }


            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) m;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case Version: {
                VersionMessage versionMessage = (VersionMessage) m;
                nextState = processVersionMessage(versionMessage);
                break;
            }

            case ClusterFile: {
                ClusterFileMessage clusterFileMessage = (ClusterFileMessage) m;
                nextState = processClusterFileMessage (clusterFileMessage);
                break;
            }

            default: {
                nextState = super.processMessage(m);
                break;
            }

        }

        return nextState;
    }

/*
    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        State nextState = this;

        GetClusterFileWireMessage getClusterFileWireMessage = new GetClusterFileWireMessage();
        sendOnWire();

        return nextState;
    }
*/


    public State processNetworkMessage (NetworkMessage networkMessage) {
        State nextState = this;

        switch (networkMessage.getWireMessage().getWireSubject()) {
            case JoinSuccess:{
                logger.info ("got JoinSucess");
                nextState = new NodeReadyState(getNode());
                break;
            }

            case GetVersions: {
                GetVersionsWireMessage getVersionsWireMessage = (GetVersionsWireMessage) networkMessage.getWireMessage();
                nextState = processGetVersionsWireMessage(getVersionsWireMessage);
                break;
            }

            default:
                logger.fatal(this + " does not understand network message " + networkMessage.getWireMessage().getWireSubject());
                System.exit(1);
        }

        return nextState;
    }


    private State processJoinSuccess (JoinSuccessMessage joinSucessMessage) {
        return new NodeReadyState(getNode());
    }


    private State processGetClusterFileMessage (GetClusterFileMessage getClusterFileMessage) {
        GetFileWireMessage getFileWireMessage = new GetFileWireMessage("cluster");
        sendOnWire(getFileWireMessage);

        return this;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        GetVersionsWireMessage getVersionsWireMessage = new GetVersionsWireMessage();
        sendOnWire(getVersionsWireMessage);

        return this;
    }


    private State processGetVersionsWireMessage (GetVersionsWireMessage getVersionsWireMessage) {
        GetVersionMessage getVersionMessage = new GetVersionMessage(getNode().getQueue(), this, getNode().getQueue());
        send (Miranda.getInstance().getQueue(), getVersionMessage);

        return this;
    }


    private State processVersionMessage (VersionMessage versionMessage) {
        List<NameVersion> versions = new ArrayList<NameVersion>();

        versions.add(versionMessage.getNameVersion());

        VersionsWireMessage versionsWireMessage = new VersionsWireMessage(versions);
        sendOnWire(versionsWireMessage);

        return this;
    }

    private State processClusterFileMessage (ClusterFileMessage clusterFileMessage) {
        ClusterFileWireMessage clusterFileWireMessage = new ClusterFileWireMessage(clusterFileMessage.getFile(), clusterFileMessage.getVersion());
        sendOnWire(clusterFileWireMessage);

        return this;
    }
}
