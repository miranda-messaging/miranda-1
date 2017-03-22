package com.ltsllc.miranda.event;

import com.ltsllc.miranda.Message;
import com.ltsllc.miranda.file.*;
import com.ltsllc.miranda.writer.Writer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;

/**
 * Created by Clark on 1/8/2017.
 */
public class SystemMessages extends Directory {
    public static final String FILE_NAME = "events";

    public SystemMessages (String directory, Writer writer)
    {
        super(directory, writer);

        SystemMessagesReadyState readyState = new SystemMessagesReadyState(this);
        setCurrentState(readyState);

        setInstance(this);
    }

    private static SystemMessages ourInstance;

    public static SystemMessages getInstance () {
        return ourInstance;
    }

    public static void setInstance (SystemMessages systemMessages) {
        ourInstance = systemMessages;
    }

    private List<Event> events = new ArrayList<Event>();

    public List<Event> getEvents() {
        return events;
    }

    @Override
    public boolean isFileOfInterest(String filename) {
        return filename.endsWith("msg");
    }

    @Override
    public MirandaFile createMirandaFile(String filename) {
        return new EventsFile(filename, getWriter());
    }
}
