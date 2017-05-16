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

package com.ltsllc.miranda.cluster.messages;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.node.NodeElement;

import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 3/11/2017.
 */
public class NodesUpdatedMessage extends Message {
    private List<NodeElement> nodeList;

    public List<NodeElement> getNodeList() {
        return nodeList;
    }

    public NodesUpdatedMessage (BlockingQueue<Message> senderQueue, Object sender, List<NodeElement> nodeList) {
        super(Subjects.NodesUpdated, senderQueue, sender);

        this.nodeList = nodeList;
    }
}
