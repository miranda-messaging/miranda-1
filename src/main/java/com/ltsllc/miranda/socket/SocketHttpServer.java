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

import com.ltsllc.miranda.http.HttpServer;
import com.ltsllc.miranda.servlet.objects.ServletMapping;
import org.eclipse.jetty.server.Server;

import java.util.List;

/**
 * Created by Clark on 3/4/2017.
 */
public class SocketHttpServer extends HttpServer {
    @Override
    public void addServlets(List<ServletMapping> servlets) {
        throw new IllegalStateException("not implemented");
    }

    private Server jetty;

    public Server getJetty() {
        return jetty;
    }

    public SocketHttpServer (Server jetty) {
        this.jetty = jetty;
    }

    @Override
    public void startServer() {

    }
}
