package de.uni_potsdam.hpi.asg.common.io.remote;

/*
 * Copyright (C) 2016 Norman Kluge
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

import de.uni_potsdam.hpi.asg.common.io.WorkingdirGenerator;

public abstract class SimpleRemoteOperationWorkflow {
    private static final Logger logger = LogManager.getLogger();

    private RemoteInformation   rinfo;
    private Session             session;
    private SFTP                sftpcon;
    private String              subdir;

    public SimpleRemoteOperationWorkflow(RemoteInformation rinfo, String subdir) {
        this.rinfo = rinfo;
        this.subdir = subdir;
    }

    public boolean run(Set<String> uploadfiles, List<String> execScripts) {
        return run(uploadfiles, execScripts, WorkingdirGenerator.getInstance().getWorkingdir(), true);
    }

    public boolean run(Set<String> uploadfiles, List<String> execScripts, String targetfolder, boolean remove) {
        try {
            if(!connect()) {
                logger.error("Connecting to host failed");
                return false;
            }
            if(!upload(uploadfiles)) {
                logger.error("Uploading files failed");
                return false;
            }
            if(!execute(execScripts)) {
                logger.error("Executing scripts failed");
                return false;
            }
            if(!download(targetfolder, remove)) {
                logger.error("Downloading files failed");
                return false;
            }
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
                logger.error("Host " + rinfo.getHost() + " not reachable");
                return false;
            }
            JSch jsch = new JSch();
            session = jsch.getSession(rinfo.getUsername(), rinfo.getHost(), 22);
            session.setPassword(rinfo.getPassword());
            session.setUserInfo(new ASGUserInfo());
            session.setConfig("StrictHostKeyChecking", "no");
            session.connect(30000);
        } catch(UnknownHostException e) {
            logger.error("Host " + rinfo.getHost() + " unknown");
            return false;
        } catch(IOException e) {
            logger.error("Host " + rinfo.getHost() + ": " + e.getLocalizedMessage());
            return false;
        } catch(JSchException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
        return true;
    }

    private boolean upload(Set<String> uploadfiles) {
        logger.info("Uploading files");
        sftpcon = new SFTP(session);
        if(!sftpcon.uploadFiles(uploadfiles, rinfo.getRemoteFolder(), subdir)) {
            logger.error("Upload failed");
            return false;
        }
        logger.debug("Using directory " + sftpcon.getDirectory());
        return true;
    }

    private boolean download(String targetfolder, boolean remove) {
        logger.info("Downloading files");
        if(!sftpcon.downloadFiles(session, targetfolder, remove)) {
            return false;
        }
        return true;
    }

    private boolean execute(List<String> execScripts) {
        logger.info("Running scripts");
        int code = -1;
        for(String str : execScripts) {
            code = RunSHScript.run(session, str, sftpcon.getDirectory());
            if(!executeCallBack(str, code)) {
                logger.error("Running script " + str + " failed");
                return false;
            }
        }
        return true;
    }

    protected abstract boolean executeCallBack(String script, int code);

}
