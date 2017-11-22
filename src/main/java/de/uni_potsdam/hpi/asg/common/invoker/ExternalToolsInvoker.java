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
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfig;
import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfigFile;
import de.uni_potsdam.hpi.asg.common.invoker.config.ToolConfig;
import de.uni_potsdam.hpi.asg.common.invoker.local.LocalInvoker;
import de.uni_potsdam.hpi.asg.common.invoker.local.ProcessReturn;
import de.uni_potsdam.hpi.asg.common.invoker.remote.ImprovedRemoteOperationWorkflow;
import de.uni_potsdam.hpi.asg.common.invoker.remote.RunSHScript.TimedResult;
import de.uni_potsdam.hpi.asg.common.iohelper.WorkingdirGenerator;

public abstract class ExternalToolsInvoker extends AbstractScriptGenerator {
    private final static Logger        logger = LogManager.getLogger();

    private static ExternalToolsConfig config;
    private static boolean             tooldebug;

    private String                     cmdname;

    //overwrite with setters if needed
    private File                       workingDir;                     // default: WorkingDirGenerator value
    private int                        timeout;                        // default: 0 (=off)
    private List<Integer>              okCodes;                        // default: {0}

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
        this.okCodes = new ArrayList<Integer>(Arrays.asList(0));
    }

    protected abstract boolean remoteExecutionCallBack(String script, TimedResult result);

    protected boolean run(List<String> params) {
        ToolConfig cfg = config.getToolConfig(cmdname);
        if(cfg == null) {
            logger.error("Config for tool '" + cmdname + "' not found");
            return false;
        }
        if(cfg.getRemoteconfig() == null) {
            //local
            return runLocal(params, cfg);
        } else {
            //remote
            return runRemote(params, cfg);
        }
    }

    private boolean runRemote(List<String> params, ToolConfig cfg) {
        ImprovedRemoteOperationWorkflow flow = new ImprovedRemoteOperationWorkflow(null, cmdname) {
            @Override
            protected boolean executeCallBack(String script, TimedResult result) {
                return remoteExecutionCallBack(script, result);
            }
        };
        //TODO: files
        return flow.run(null, null, null, workingDir, false);
    }

    private boolean runLocal(List<String> params, ToolConfig cfg) {
        String[] cmd = LocalInvoker.convertCmd(cfg.getCmdline());
        ProcessReturn ret = LocalInvoker.invoke(cmd, params, workingDir, timeout, tooldebug);
        return LocalInvoker.errorHandling(ret, okCodes);
    }

    protected void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    protected void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void setOkCodes(List<Integer> okCodes) {
        this.okCodes = okCodes;
    }
}
