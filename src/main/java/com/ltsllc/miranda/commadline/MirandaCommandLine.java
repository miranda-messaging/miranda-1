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

package com.ltsllc.miranda.commadline;

import com.ltsllc.miranda.property.MirandaProperties;
import org.apache.log4j.Logger;

import java.util.Properties;

/**
 * The arguments to the program.
 *
 * <p>
 *     Note that the default value for things like the properties filename is
 *     null.  This means it was absent from the arguments.
 * </p>
 */
public class MirandaCommandLine extends CommandLine {
    private static Logger logger = Logger.getLogger(MirandaCommandLine.class);

    private String loggingLevel;
    private String log4jFilename;
    private String propertiesFilename;
    private String mirandaMode;

    public String getLoggingLevel() {
        return loggingLevel;
    }

    public void setLoggingLevel(String loggingLevel) {
        if (loggingLevel.equalsIgnoreCase("debug") || loggingLevel.equalsIgnoreCase("debugging"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Debug.toString();
        else if (loggingLevel.equalsIgnoreCase("info") || loggingLevel.equalsIgnoreCase("information"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Info.toString();
        else if (
                loggingLevel.equalsIgnoreCase("warn") || loggingLevel.equalsIgnoreCase("warning")
                || loggingLevel.equalsIgnoreCase("default")
        ) {
            this.loggingLevel = MirandaProperties.LoggingLevel.Warning.toString();
        }
        else if (loggingLevel.equalsIgnoreCase("error"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Error.toString();
        else if (loggingLevel.equalsIgnoreCase("fatal"))
            this.loggingLevel = MirandaProperties.LoggingLevel.Fatal.toString();
        else {
            String level = MirandaProperties.LoggingLevel.Warning.toString();
            logger.error ("Unknown logging level " + loggingLevel + " setting level to " + level);
            this.loggingLevel = level;
        }
    }

    public String getPropertiesFilename() {
        return propertiesFilename;
    }

    public void setPropertiesFilename(String propertiesFilename) {
        this.propertiesFilename = propertiesFilename;
    }

    public String getLog4jFilename() {
        return log4jFilename;
    }

    public void setLog4jFilename(String log4jFilename) {
        this.log4jFilename = log4jFilename;
    }

    public String getMirandaMode() {
        return mirandaMode;
    }

    public void setMirandaMode(String mirandaMode) {
        this.mirandaMode = mirandaMode;
    }

    public MirandaCommandLine (String[] argv) {
        super(argv);

        parse();
    }

    public Properties asProperties () {
        Properties properties = super.asProperties();

        if (null != getLog4jFilename())
            properties.setProperty(MirandaProperties.PROPERTY_LOG4J_FILE, getLog4jFilename());

        if (null != getPropertiesFilename())
            properties.setProperty(MirandaProperties.PROPERTY_PROPERTIES_FILE, getPropertiesFilename());

        return properties;
    }

    public void parse () {
        super.parse();


        if (null != getArg()) {
            setPropertiesFilename(getArgAndAdvance());
        }

        while (hasMoreArgs()) {
            if (null != getArg() && getArg().equalsIgnoreCase("-mode")) {
                advance();

                String mode = getArgAndAdvance();

                if (mode.equalsIgnoreCase("normal"))
                    setMirandaMode(MirandaProperties.MirandaModes.Normal.toString());
                else if (mode.equalsIgnoreCase("debug"))
                    setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
                else {
                    printUsage();
                    throw new IllegalArgumentException ("Unknown Mirana mode: " + mode);
                }
            } else if (getArg().equalsIgnoreCase("-debug")) {
                setMirandaMode(MirandaProperties.MirandaModes.Debugging.toString());
                setLoggingLevel(MirandaProperties.LoggingLevel.Debug.toString());
            } else if (getArg().equalsIgnoreCase("-log4j")) {
                advance();

                if (!hasMoreArgs()) {
                    throw new IllegalStateException ("Must supply a parameter to -log4j");
                }

                setLog4jFilename(getArgAndAdvance());
            } else {
                System.err.println ("Unrecognized option: " + getArgAndAdvance());
            }
        }
    }

    public void printUsage () {
        System.err.println ("usage: miranda [<properties file> [-mode <miranda mode>] [-debug] [-log4j <log4j config file>]]");
    }
}
