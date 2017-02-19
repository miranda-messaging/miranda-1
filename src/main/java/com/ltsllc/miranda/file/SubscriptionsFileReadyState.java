package com.ltsllc.miranda.file;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.*;
import com.ltsllc.miranda.cluster.RemoteVersionMessage;
import com.ltsllc.miranda.node.*;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by Clark on 2/11/2017.
 */
public class SubscriptionsFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(SubscriptionsFileReadyState.class);

    private SubscriptionsFile subscriptionsFile;

    public SubscriptionsFileReadyState (SubscriptionsFile subscriptionsFile) {
        super(subscriptionsFile);

        this.subscriptionsFile = subscriptionsFile;
    }

    public SubscriptionsFile getSubscriptionsFile() {
        return subscriptionsFile;
    }

    @Override
    public State processMessage(Message message) {
        State nextState = this;

        switch (message.getSubject()) {
            case GetVersion: {
                GetVersionMessage getVersionMessage = (GetVersionMessage) message;
                nextState = processGetVersionMessage(getVersionMessage);
                break;
            }

            case GetFile: {
                GetFileMessage getFileMessage = (GetFileMessage) message;
                nextState = processGetFileMessage (getFileMessage);
                break;
            }
            default:
                nextState = super.processMessage(message);
                break;

        }

        return nextState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("subscriptions", getSubscriptionsFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getSubscriptionsFile().getQueue(), getSubscriptionsFile(), nameVersion);
        send (getVersionMessage.getSender(), versionMessage);

        return this;
    }


    public State getSyncingState () {
        return new SubscriptionsFileSyncingState(getSubscriptionsFile());
    }

    @Override
    public Version getVersion() {
        return getSubscriptionsFile().getVersion();
    }

    private State processGetFileMessage (GetFileMessage getFileMessage) {
        GetFileResponseMessage getFileResponseMessage = new GetFileResponseMessage(getSubscriptionsFile().getQueue(), this, "subscriptions", getSubscriptionsFile().getBytes());
        send(getFileMessage.getSender(), getFileResponseMessage);

        return this;
    }

    @Override
    public void write() {
        WriteMessage writeMessage = new WriteMessage(getSubscriptionsFile().getFilename(), getSubscriptionsFile().getBytes(),
                getSubscriptionsFile().getQueue(), this);

        send(getSubscriptionsFile().getQueue(), writeMessage);
    }


    @Override
    public void add(Object o) {
        Subscription subscription = (Subscription) o;
        getSubscriptionsFile().getData().add(subscription);
    }


    @Override
    public boolean contains(Object o) {
        Subscription subscription = (Subscription) o;
        for (Subscription sub : getSubscriptionsFile().getData()) {
            if (sub.equals(subscription))
                return true;
        }

        return false;
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<User>>() {}.getType();
    }

    @Override
    public SingleFile getFile() {
        return getSubscriptionsFile();
    }


    @Override
    public String getName() {
        return "subscriptions";
    }


    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getSubscriptionsFile().getData());
    }


    @Override
    public void notifyContainer(Set<Perishable> expired) {
        logger.warn("Ignoring notifyContainer");
    }
}