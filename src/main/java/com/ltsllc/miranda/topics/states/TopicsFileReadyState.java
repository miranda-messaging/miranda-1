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

package com.ltsllc.miranda.topics.states;

import com.google.gson.reflect.TypeToken;
import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.State;
import com.ltsllc.miranda.Version;
import com.ltsllc.miranda.file.Perishable;
import com.ltsllc.miranda.file.SingleFile;
import com.ltsllc.miranda.file.states.SingleFileReadyState;
import com.ltsllc.miranda.node.NameVersion;
import com.ltsllc.miranda.node.messages.GetVersionMessage;
import com.ltsllc.miranda.node.messages.VersionMessage;
import com.ltsllc.miranda.topics.Topic;
import com.ltsllc.miranda.topics.TopicsFile;
import com.ltsllc.miranda.writer.WriteMessage;
import org.apache.log4j.Logger;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Clark on 2/11/2017.
 */
public class TopicsFileReadyState extends SingleFileReadyState {
    private static Logger logger = Logger.getLogger(TopicsFileReadyState.class);

    private TopicsFile topicsFile;


    public TopicsFileReadyState(TopicsFile topicsFile) {
        super(topicsFile);

        this.topicsFile = topicsFile;
    }

    public TopicsFile getTopicsFile() {
        return topicsFile;
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

            default:
                nextState = super.processMessage(message);
                break;
        }

        return nextState;
    }


    private State processGetVersionMessage (GetVersionMessage getVersionMessage) {
        NameVersion nameVersion = new NameVersion("topics", getTopicsFile().getVersion());
        VersionMessage versionMessage = new VersionMessage(getTopicsFile().getQueue(), this, nameVersion);
        send(getVersionMessage.getRequester(), versionMessage);

        return this;
    }


    @Override
    public void add(Object o) {
        Topic topic = (Topic) o;
        getTopicsFile().getData().add(topic);
    }


    @Override
    public SingleFile getFile() {
        return getTopicsFile();
    }


    @Override
    public boolean contains(Object o) {
        Topic topic = (Topic) o;
        for (Topic t : getTopicsFile().getData()) {
            if (t.equals(topic))
                return true;
        }

        return false;
    }


    public void write() {
        WriteMessage writeMessage = new WriteMessage(getTopicsFile().getFilename(), getTopicsFile().getBytes(), getTopicsFile().getQueue(), this);
        send(getTopicsFile().getWriterQueue(), writeMessage);
    }


    @Override
    public Type getListType() {
        return new TypeToken<List<Topic>>(){}.getType();
    }

    @Override
    public String getName() {
        return "topics";
    }

    @Override
    public Version getVersion() {
        return getTopicsFile().getVersion();
    }

    @Override
    public List<Perishable> getPerishables() {
        return new ArrayList<Perishable>(getTopicsFile().getData());
    }
}
