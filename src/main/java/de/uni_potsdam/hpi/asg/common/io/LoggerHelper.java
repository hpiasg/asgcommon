package de.uni_potsdam.hpi.asg.common.io;

/*
 * Copyright (C) 2012 - 2015 Norman Kluge
 * 
 * This file is part of ASGcommon.
 * 
 * ASGcommon is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * ASGcommon is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with ASGcommon.  If not, see <http://www.gnu.org/licenses/>.
 */

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class LoggerHelper {

    public static Logger initLogger(int outputlevel, File logfile, boolean debug) {
        if(logfile == null) {
            logfile = new File("log.txt");
        }
        System.setProperty("logFilename", logfile.getAbsolutePath());
        Logger logger = LogManager.getLogger();

        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig rootConfig = config.getLoggerConfig(LogManager.ROOT_LOGGER_NAME);

        Level consoleLevel = Level.OFF;
        Level fileLevel = Level.OFF;
        if(debug) {
            consoleLevel = Level.DEBUG;
            fileLevel = Level.DEBUG;
            System.setProperty("isdebug", "true");
        } else {
            switch(outputlevel) {
                case 0:
                    consoleLevel = Level.OFF;
                    break;
                case 1:
                    consoleLevel = Level.ERROR;
                    break;
                case 2:
                    consoleLevel = Level.WARN;
                    break;
                case 3:
                    consoleLevel = Level.INFO;
                    break;
                default:
                    consoleLevel = Level.WARN;
            }
            fileLevel = Level.INFO;
            System.setProperty("isdebug", "false");
        }

        if(rootConfig.getAppenders().containsKey("Routing_console") && rootConfig.getAppenders().containsKey("Routing_file")) {
            // advanced mode
            rootConfig.setLevel(Level.ALL);
            rootConfig.addAppender(rootConfig.getAppenders().get("Routing_console"), consoleLevel, null);
            rootConfig.addAppender(rootConfig.getAppenders().get("Routing_file"), fileLevel, null);
        } else {
            // normal mode
            rootConfig.setLevel(consoleLevel);
        }

        context.updateLoggers();
        logger.debug("Logger initialised");

        return logger;
    }

    public static void setLogLevel(Logger logger, Level level) {
        LoggerContext context = (LoggerContext)LogManager.getContext(false);
        Configuration config = context.getConfiguration();
        LoggerConfig loggerConfig = new LoggerConfig();
        loggerConfig.setLevel(level);
        config.addLogger(logger.getName(), loggerConfig);
        context.updateLoggers(config);
    }

    /**
     * Formats the runtime for output
     * 
     * @param time
     * @return the formatted time
     */
    public static String formatRuntime(long time, boolean fixedlength) {
        double h_full = ((double)time) / 3600000;
        long h = (long)h_full;
        double min_full = ((h_full - h) * 60);
        long min = (long)min_full;
        double sec_full = ((min_full - min) * 60);
        long sec = (long)sec_full;
        double msec_full = ((sec_full - sec) * 1000);
        long msec = (long)msec_full;

        return ((h == 0) ? (fixedlength ? "     " : "") : String.format("%3d", h) + "h ") + ((h == 0 && min == 0) ? (fixedlength ? "       " : "") : String.format("%3d", min) + "min ") + ((h == 0 && min == 0 && sec == 0) ? (fixedlength ? "     " : "") : String.format("%3d", sec) + "s ") + String.format("%3d", msec) + "ms";
    }
}
