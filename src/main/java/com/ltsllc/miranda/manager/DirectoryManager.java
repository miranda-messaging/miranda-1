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

package com.ltsllc.miranda.manager;

import com.ltsllc.miranda.Consumer;
import com.ltsllc.miranda.directory.MirandaDirectory;
import com.ltsllc.miranda.event.EventDirectory;
import com.ltsllc.miranda.reader.Reader;
import com.ltsllc.miranda.writer.Writer;

/**
 * Created by Clark on 5/3/2017.
 */
public class DirectoryManager extends Consumer {
    private Reader reader;
    private Writer writer;
    private MirandaDirectory directory;

    public MirandaDirectory getDirectory() {
        return directory;
    }

    public void setDirectory(MirandaDirectory directory) {
        this.directory = directory;
    }

    public Writer getWriter() {
        return writer;
    }

    public void setWriter(Writer writer) {
        this.writer = writer;
    }

    public Reader getReader() {

        return reader;
    }

    public void setReader(Reader reader) {
        this.reader = reader;
    }

    public DirectoryManager (String name, String directory, Reader reader, Writer writer) {
        super (name);

        this.directory = new EventDirectory(directory);
        this.reader = reader;
        this.writer = writer;
    }

}
