package de.uni_potsdam.hpi.asg.common.invoker;

/*
 * Copyright (C) 2012 - 2017 Norman Kluge
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

import java.util.List;

public class InvokeReturn {

    public static enum Status {
        ok, noio, timeout, ioexception
    }

    private Status       status;
    private List<String> cmdline;
    private int          exitCode;
    private String       output;

    private boolean      result;
    private long         localUserTime;
    private long         localSystemTime;
    private long         remoteUserTime;
    private long         remoteSystemTime;

    public InvokeReturn(List<String> cmdline) {
        this.cmdline = cmdline;
    }

    public InvokeReturn(List<String> cmdline, Status status) {
        this.cmdline = cmdline;
        this.status = status;
    }

    public long getLocalCPUTime() {
        return localSystemTime + localUserTime;
    }

    public long getRemoteCPUTime() {
        return remoteSystemTime + remoteUserTime;
    }

    public List<String> getCmdline() {
        return cmdline;
    }

    public int getExitCode() {
        return exitCode;
    }

    public void setExitCode(int exitCode) {
        this.exitCode = exitCode;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public Status getStatus() {
        return status;
    }

    public void setOutput(String output) {
        this.output = output;
    }

    public String getOutput() {
        return output;
    }

    public long getLocalSystemTime() {
        return localSystemTime;
    }

    public long getLocalUserTime() {
        return localUserTime;
    }

    public long getRemoteSystemTime() {
        return remoteSystemTime;
    }

    public long getRemoteUserTime() {
        return remoteUserTime;
    }

    public void setLocalSystemTime(long localSystemTime) {
        this.localSystemTime = localSystemTime;
    }

    public void setLocalUserTime(long localUserTime) {
        this.localUserTime = localUserTime;
    }

    public void setRemoteSystemTime(long remoteSystemTime) {
        this.remoteSystemTime = remoteSystemTime;
    }

    public void setRemoteUserTime(long remoteUserTime) {
        this.remoteUserTime = remoteUserTime;
    }

    public void setResult(boolean result) {
        this.result = result;
    }

    public boolean getResult() {
        return result;
    }
}
