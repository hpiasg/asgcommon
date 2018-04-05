package de.uni_potsdam.hpi.asg.common.invoker.local;

/*
 * Copyright (C) 2012 - 2018 Norman Kluge
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.invoker.InvokeReturn;
import de.uni_potsdam.hpi.asg.common.invoker.TimeStat;
import de.uni_potsdam.hpi.asg.common.invoker.InvokeReturn.Status;
import de.uni_potsdam.hpi.asg.common.iohelper.BasedirHelper;

public class LocalInvoker {
    private final static Logger  logger = LogManager.getLogger();

    private static List<Process> subProcesses;

    private File                 workingDir;
    private int                  timeout;
    private boolean              tooldebug;

    static {
        subProcesses = new ArrayList<>();
    }

    public LocalInvoker(File workingDir, int timeout, boolean tooldebug) {
        this.workingDir = workingDir;
        this.timeout = timeout;
        this.tooldebug = tooldebug;
    }

    public InvokeReturn invoke(List<String> command) {
        TimeStat stat = TimeStat.create(workingDir);
        if(stat == null) {
            return null;
        }
        command.addAll(0, stat.getCmd());
        InvokeReturn ret = run(command);
        if(!stat.evaluate()) {
            return null;
        }
        ret.setLocalUserTime(stat.getUserTime());
        ret.setLocalSystemTime(stat.getSystemTime());
        return ret;
    }

    private InvokeReturn run(List<String> command) {
        InvokeReturn retVal = new InvokeReturn(command);
        Process process = null;
        try {
            logger.debug("Exec command: " + command.toString().replace("\n", ""));
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.directory(workingDir);
            builder.environment(); // bugfix setting env in test-mode (why this works? i dont know..)
            process = builder.start();
            subProcesses.add(process);

            Thread timeoutThread = null;
            if(timeout > 0) {
                timeoutThread = new Thread(new Timeout(Thread.currentThread(), timeout));
                timeoutThread.setName("Timout for " + command.toString());
                timeoutThread.start();
            }
            IOStreamReader ioreader = new IOStreamReader(process, tooldebug);
            Thread streamThread = new Thread(ioreader);
            streamThread.setName("StreamReader for " + command.toString());
            streamThread.start();
            process.waitFor();
            streamThread.join();
            if(timeoutThread != null) {
                timeoutThread.interrupt();
            }
            String out = ioreader.getOutResult();
            String err = ioreader.getErrResult();
            //System.out.println(out);
            if(out == null) {
                //System.out.println("out = null");
                retVal.setStatus(Status.noio);
            }
            retVal.setExitCode(process.exitValue());
            retVal.setOutputStr(out);
            retVal.setErrorStr(err);
            retVal.setStatus(Status.ok);
        } catch(InterruptedException e) {
            process.destroy();
            retVal.setStatus(Status.timeout);
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            retVal.setStatus(Status.ioexception);
        }
        return retVal;
    }

    public static List<String> convertCmd(String cmd) {
        if(cmd == null) {
            return null;
        }

        cmd = BasedirHelper.replaceBasedir(cmd);
        return Arrays.asList(cmd.split(" "));
    }

    public static void killSubProcesses() {
        for(Process p : subProcesses) {
            p.destroy();
        }
    }
}
