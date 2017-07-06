package de.uni_potsdam.hpi.asg.common.remote;

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
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.uni_potsdam.hpi.asg.common.remote.RunSHScript.TimedResult;

public abstract class ImprovedRemoteOperationWorkflow {
    private static final Logger logger            = LogManager.getLogger();

    private static final int    maxReconnectCount = 2;

    private RemoteInformation   rinfo;
    private Session             session;
    private SFTP                sftpcon;
    private String              remoteSubDir;

    public ImprovedRemoteOperationWorkflow(RemoteInformation rinfo, String remoteSubDir) {
        this.rinfo = rinfo;
        this.remoteSubDir = remoteSubDir;
    }

    public boolean run(Set<File> uploadFiles, List<String> execScripts, Set<String> downloadIncludes, File localDir, boolean removeRemoteDir) {
        try {
            int reconnectCount = 0;
            while(!connect()) {
                if(reconnectCount > maxReconnectCount) {
                    break;
                }
                Thread.sleep(5000);
                reconnectCount++;
            }
            if(session == null || !session.isConnected()) {
                logger.error("Connecting to host failed");
                return false;
            }

            if(!upload(uploadFiles)) {
                logger.error("Uploading files failed");
                return false;
            }
            if(!execute(execScripts)) {
                logger.error("Executing scripts failed");
                return false;
            }
            if(!download(localDir, downloadIncludes, removeRemoteDir)) {
                logger.error("Downloading files failed");
                return false;
            }
        } catch(InterruptedException e) {
            return false;
        } finally {
            if(session != null && session.isConnected()) {
                session.disconnect();
            }
        }

        return true;
    }

    private boolean connect() {
        try {
            if(!InetAddress.getByName(rinfo.getHost()).isReachable(1000)) {
                logger.warn("Host " + rinfo.getHost() + " not reachable");
                return false;
            }
            JSch jsch = new JSch();
            session = jsch.getSession(rinfo.getUsername(), rinfo.getHost(), 22);
            session.setPassword(rinfo.getPassword());
            session.setUserInfo(new ASGUserInfo());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
        } catch(UnknownHostException e) {
            logger.warn("Host " + rinfo.getHost() + " unknown");
            return false;
        } catch(IOException e) {
            logger.warn("Host " + rinfo.getHost() + ": " + e.getLocalizedMessage());
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
        File remoteBaseDir = new File(rinfo.getRemoteFolder());
        if(!sftpcon.uploadFiles(uploadFiles, remoteBaseDir, remoteSubDir)) {
            logger.error("Upload failed");
            return false;
        }
        logger.debug("Using directory " + sftpcon.getRemoteDir().getAbsolutePath());
        return true;
    }

    private boolean download(File localDir, Set<String> includeFilenames, boolean removeRemoteDir) {
        logger.debug("Downloading files");
        if(!sftpcon.downloadFiles(localDir, includeFilenames, removeRemoteDir)) {
            return false;
        }
        return true;
    }

    private boolean execute(List<String> execScripts) {
        logger.debug("Running scripts");
        for(String str : execScripts) {
            TimedResult result = RunSHScript.runTimed(session, str, sftpcon.getRemoteDir().getAbsolutePath());
            if(!executeCallBack(str, result)) {
                logger.error("Running script " + str + " failed");
                return false;
            }
        }
        return true;
    }

    protected abstract boolean executeCallBack(String script, TimedResult result);

}
