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

package com.ltsllc.miranda.topics.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.SessionMessage;
import com.ltsllc.miranda.session.Session;
import com.ltsllc.miranda.topics.Topic;

import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 4/9/2017.
 */
public class UpdateTopicMessage extends SessionMessage {
    private Topic topic;

    public Topic getTopic() {
        return topic;
    }

    public UpdateTopicMessage (BlockingQueue<Message> senderQueue, Object sender, Session session, Topic topic) {
        super(Subjects.UpdateTopic, senderQueue, sender, session);

        this.topic = topic;
    }
}
