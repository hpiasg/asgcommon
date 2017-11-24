package de.uni_potsdam.hpi.asg.common.invoker;

/*
 * Copyright (C) 2017 Norman Kluge
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfig;
import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfigFile;
import de.uni_potsdam.hpi.asg.common.invoker.config.ToolConfig;
import de.uni_potsdam.hpi.asg.common.invoker.local.LocalInvoker;
import de.uni_potsdam.hpi.asg.common.invoker.remote.RemoteInvoker;
import de.uni_potsdam.hpi.asg.common.iohelper.WorkingdirGenerator;

public abstract class ExternalToolsInvoker {
    private final static Logger        logger = LogManager.getLogger();

    private static ExternalToolsConfig config;
    private static boolean             tooldebug;

    private String                     cmdname;
    private ToolConfig                 cfg;

    //overwrite with setters if needed
    private File                       workingDir;                     // default: WorkingDirGenerator value
    private int                        timeout;                        // default: 0 (=off)
    private String                     remoteSubDir;                   // default: work
    private boolean                    removeRemoteDir;                // default: true

    protected Set<File>                uploadFiles;
    protected Set<String>              downloadIncludes;

    public static boolean init(File configFile, boolean tooldebug) {
        if(configFile == null) {
            logger.error("No tools config file");
            return false;
        }
        config = ExternalToolsConfigFile.readIn(configFile);
        if(config == null) {
            return false;
        }

        return true;
    }

    protected ExternalToolsInvoker(String cmdname) {
        this.cmdname = cmdname;
        this.workingDir = WorkingdirGenerator.getInstance().getWorkingDir();
        this.timeout = 0;
        this.remoteSubDir = "work";
        this.removeRemoteDir = true;
    }

    protected InvokeReturn run(List<String> params) {
        cfg = config.getToolConfig(cmdname);
        if(cfg == null) {
            logger.error("Config for tool '" + cmdname + "' not found");
            return null;
        }
        if(cfg.getRemoteconfig() == null) {
            //local
            if(!localSetup()) {
                logger.error("Local setup failed for " + cfg.getName());
                return null;
            }
            return runLocal(params);
        } else {
            //remote
            uploadFiles = new HashSet<>();
            downloadIncludes = new HashSet<>();
            if(!remoteSetup()) {
                logger.error("Remote setup failed for " + cfg.getName());
                return null;
            }
            return runRemote(params);
        }
    }

    protected boolean errorHandling(InvokeReturn ret) {
        return errorHandling(ret, Arrays.asList(0));
    }

    protected boolean errorHandling(InvokeReturn ret, List<Integer> okCodes) {
        if(ret != null) {
            switch(ret.getStatus()) {
                case ok:
                    if(!okCodes.contains(ret.getExitCode())) {
                        logger.error("An error was reported while executing " + ret.getCmdline());
                        logger.debug("Exit code: " + ret.getExitCode() + " Output:");
                        logger.debug("##########");
                        logger.debug(ret.getOutput());
                        logger.debug("##########");
                        return false;
                    }
                    break;
                case timeout:
                    logger.error("Timeout while executing " + ret.getCmdline());
                    return false;
                case ioexception:
                case noio:
                    logger.error("I/O error while executing " + ret.getCmdline());
                    return false;
            }
        } else {
            logger.error("Something went really wrong while executing something. I don't even know what the command line was");
            return false;
        }
        return true;
    }

    protected abstract boolean localSetup();

    protected abstract boolean remoteSetup();

    private InvokeReturn runRemote(List<String> params) {
        List<String> cmdline = new ArrayList<>();
        cmdline.addAll(Arrays.asList(cfg.getCmdline().split(" ")));
        cmdline.addAll(params);
        RemoteInvoker inv = new RemoteInvoker(cfg.getRemoteconfig(), remoteSubDir, workingDir, removeRemoteDir, timeout);
        return inv.invoke(uploadFiles, cmdline, downloadIncludes);
    }

    private InvokeReturn runLocal(List<String> params) {
        List<String> cmdline = new ArrayList<>();
        cmdline.addAll(LocalInvoker.convertCmd(cfg.getCmdline()));
        cmdline.addAll(params);
        LocalInvoker inv = new LocalInvoker(workingDir, timeout, tooldebug);
        return inv.invoke(cmdline);
    }

    protected void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    protected void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public void setRemoteSubDir(String remoteSubDir) {
        this.remoteSubDir = remoteSubDir;
    }

    public void setRemoveRemoteDir(boolean removeRemoteDir) {
        this.removeRemoteDir = removeRemoteDir;
    }
}
