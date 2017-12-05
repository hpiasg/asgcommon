package de.uni_potsdam.hpi.asg.common.invoker.remote;

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
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.uni_potsdam.hpi.asg.common.invoker.InvokeReturn;
import de.uni_potsdam.hpi.asg.common.invoker.TimeStat;
import de.uni_potsdam.hpi.asg.common.invoker.InvokeReturn.Status;
import de.uni_potsdam.hpi.asg.common.invoker.config.RemoteConfig;

public class RemoteInvoker {
    private static final Logger logger            = LogManager.getLogger();

    private static final int    maxReconnectCount = 2;
    private static final int    reconnectWaitTime = 5000;
    private static final int    sessionTimeout    = 30000;

    private RemoteConfig        rinfo;
    private int                 timeout;
    private Session             session;
    private SFTP                sftpcon;
    private String              remoteSubDir;
    private File                localDir;
    private boolean             removeRemoteDir;

    public RemoteInvoker(RemoteConfig rinfo, String remoteSubDir, File localDir, boolean removeRemoteDir, int timeout) {
        this.rinfo = rinfo;
        this.remoteSubDir = remoteSubDir;
        this.localDir = localDir;
        this.removeRemoteDir = removeRemoteDir;
        this.timeout = timeout;
    }

    public InvokeReturn invoke(Set<File> uploadFiles, List<String> command, Set<String> downloadIncludeFileStarts) {
        InvokeReturn ret = null;
        try {
            int reconnectCount = 0;
            while(!connect()) {
                if(reconnectCount > maxReconnectCount) {
                    break;
                }
                Thread.sleep(reconnectWaitTime);
                reconnectCount++;
            }
            if(session == null || !session.isConnected()) {
                logger.error("Connecting to host failed");
                return ret;
            }

            if(!upload(uploadFiles)) {
                logger.error("Uploading files failed");
                return ret;
            }
            ret = execute(command);
            if(ret == null) {
                logger.error("Executing scripts failed");
                return ret;
            }
            if(ret.getStatus() != Status.ok) {
                logger.error("Executing scripts failed 2");
                return ret;
            }
            if(!download(localDir, downloadIncludeFileStarts, removeRemoteDir)) {
                logger.error("Downloading files failed");
                return ret;
            }
        } catch(InterruptedException e) {
            return ret;
        } finally {
            if(session != null && session.isConnected()) {
                session.disconnect();
            }
        }

        return ret;
    }

    private boolean connect() {
        try {
            if(!InetAddress.getByName(rinfo.getHostname()).isReachable(1000)) {
                logger.warn("Host " + rinfo.getHostname() + " not reachable");
                return false;
            }
            JSch jsch = new JSch();
            session = jsch.getSession(rinfo.getUsername(), rinfo.getHostname(), rinfo.getPort());
            session.setPassword(rinfo.getPassword());
            session.setUserInfo(new ASGUserInfo());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(sessionTimeout);
        } catch(UnknownHostException e) {
            logger.warn("Host " + rinfo.getHostname() + " unknown");
            return false;
        } catch(IOException e) {
            logger.warn("Host " + rinfo.getHostname() + ": " + e.getLocalizedMessage());
            return false;
        } catch(JSchException e) {
            logger.warn(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private boolean upload(Set<File> uploadFiles) {
        logger.debug("Uploading files");
        sftpcon = new SFTP(session);
        File remoteBaseDir = new File(rinfo.getWorkingDir());
        if(!sftpcon.uploadFiles(uploadFiles, remoteBaseDir, remoteSubDir)) {
            logger.error("Upload failed");
            return false;
        }
        logger.debug("Using directory " + sftpcon.getRemoteDir().getAbsolutePath());
        return true;
    }

    private boolean download(File localDir, Set<String> includeFileStarts, boolean removeRemoteDir) {
        logger.debug("Downloading files");
        if(!sftpcon.downloadFiles(localDir, includeFileStarts, removeRemoteDir)) {
            return false;
        }
        return true;
    }

    private InvokeReturn execute(List<String> cmd) {
        logger.debug("Running scripts");
        try {
            TimeStat stat = TimeStat.create();
            if(stat == null) {
                return null;
            }

            StringBuilder command = new StringBuilder();
            command.append("cd " + sftpcon.getRemoteDir().getAbsolutePath() + ";");
            command.append(stat.getRemoteCmdStr() + " ");
            for(String str : cmd) {
                command.append(str + " ");
            }
            logger.debug("Exec command: " + command.toString().replace("\n", ""));

            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand(command.toString());

            OutputStream out = stat.getStream();
            if(out == null) {
                return null;
            }
            channel.setOutputStream(out);
            channel.setErrStream(out);

            int x = 0;
            channel.connect();
            while(channel.isConnected()) {
                Thread.sleep(1000);
                x++;
                if(timeout != 0 && (x >= (timeout / 1000))) {
                    channel.disconnect();
                    return null;
                }
            }

            channel.disconnect();

            long userTime = 0;
            long systemTime = 0;
            if(stat.evaluate()) {
                userTime = stat.getUserTime();
                systemTime = stat.getSystemTime();
            }

            InvokeReturn ret = new InvokeReturn(cmd);
            ret.setStatus(Status.ok);
            ret.setExitCode(channel.getExitStatus());
            ret.setOutput(out.toString());
            ret.setRemoteUserTime(userTime);
            ret.setRemoteSystemTime(systemTime);

            return ret;
        } catch(JSchException e) {
            e.printStackTrace();
            return null;
        } catch(InterruptedException e) {
            InvokeReturn ret = new InvokeReturn(cmd);
            ret.setStatus(Status.timeout);
            return ret;
        }
    }
}
