package de.uni_potsdam.hpi.asg.common.iohelper;

/*
 * Copyright (C) 2012 - 2022 Norman Kluge
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
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.config.LoggerConfig;

public class LoggerHelper {

    private static final URL    GUI_CONFIG            = LoggerHelper.class.getResource("/asg_log4j2_gui.xml");
    private static final URL    CMDLINE_CONFIG        = LoggerHelper.class.getResource("/asg_log4j2_cmdline.xml");

    private static final String CONSOLE_APPENDER_NAME = "Routing_console";
    private static final String FILE_APPENDER_NAME    = "Routing_file";

    public enum Mode {
        cmdline, gui
    }

    public static Logger initLogger(int outputlevel, File logfile, boolean debug, Mode mode) {
        if(mode == null) {
            return null;
        }
        switch(mode) {
            case cmdline:
                return createCmdLineLogger(outputlevel, logfile, debug);
            case gui:
                return createGuiLogger(outputlevel, debug);
        }
        return null;
    }

    private static Logger createGuiLogger(int outputlevel, boolean debug) {
        try {
            Configurator.initialize(null, (ClassLoader)null, GUI_CONFIG.toURI());
        } catch(URISyntaxException e) {
            return null;
        }
        return configureLogger(outputlevel, debug);
    }

    public static Logger initLogger(int outputlevel, File logfile, boolean debug) {
        handleLogfile(logfile);
        return configureLogger(outputlevel, debug);
    }

    private static Logger createCmdLineLogger(int outputlevel, File logfile, boolean debug) {
        handleLogfile(logfile);
        try {
            Configurator.initialize(null, (ClassLoader)null, CMDLINE_CONFIG.toURI());
        } catch(URISyntaxException e) {
            return null;
        }
        return configureLogger(outputlevel, debug);
    }

    public static Logger configureLogger(int outputlevel, boolean debug) {
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
            fileLevel = Level.DEBUG;
            System.setProperty("isdebug", "false");
        }

        if(rootConfig.getAppenders().containsKey(CONSOLE_APPENDER_NAME) && rootConfig.getAppenders().containsKey(FILE_APPENDER_NAME)) {
            // advanced mode
            rootConfig.setLevel(Level.ALL);
            Appender consoleAppender = rootConfig.getAppenders().get(CONSOLE_APPENDER_NAME);
            rootConfig.removeAppender(CONSOLE_APPENDER_NAME);
            rootConfig.addAppender(consoleAppender, consoleLevel, null);
            Appender fileAppender = rootConfig.getAppenders().get(FILE_APPENDER_NAME);
            rootConfig.removeAppender(FILE_APPENDER_NAME);
            rootConfig.addAppender(fileAppender, fileLevel, null);
        } else {
            // normal mode
            rootConfig.setLevel(consoleLevel);
        }

        context.updateLoggers();
        logger.debug("Logger initialised");

        return logger;
    }

    private static void handleLogfile(File logfile) {
        if(logfile == null) {
            logfile = new File("log.txt");
        }
        System.setProperty("logFilename", logfile.getAbsolutePath());
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
