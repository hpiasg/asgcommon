package de.uni_potsdam.hpi.asg.common.invoker.remote;

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

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Set;
import java.util.Vector;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.ChannelSftp.LsEntry;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

public class SFTP {
    private static final Logger logger = LogManager.getLogger();

    private Session             session;
    private File                remoteDir;

    public SFTP(Session session) {
        this.session = session;
    }

    public boolean uploadFiles(Set<File> localFiles, File remoteBaseDir, String remoteSubDir) {
        try {
            if(remoteBaseDir == null) {
                logger.error("Remote dir is not defined");
                return false;
            }

            ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
            channel.connect();

            int tmpnum = 0;
            remoteDir = new File(remoteBaseDir, remoteSubDir + Integer.toString(tmpnum));
            boolean mkdirsuccess = false;
            while(!mkdirsuccess) {
                try {
                    channel.mkdir(remoteDir.getAbsolutePath());
                    mkdirsuccess = true;
                } catch(SftpException e) {
                    remoteDir = new File(remoteBaseDir, remoteSubDir + Integer.toString(++tmpnum));
                }
            }

            channel.cd(remoteDir.getAbsolutePath());

            for(File file : localFiles) {
                if(file.exists() && file.isFile()) {
                    if(file.getName().startsWith("__")) {
                        continue;
                    }
                    try(FileInputStream stream = new FileInputStream(file)) {
                        channel.put(stream, file.getName());
                    }
                } else {
                    logger.warn("Omitting " + file.getName());
                }
            }
            channel.disconnect();
            return true;
        } catch(SftpException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(JSchException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    public boolean downloadFiles(File localDir, boolean removeRemote) {
        return downloadFiles(localDir, null, removeRemote);
    }

    public boolean downloadFiles(File localDir, Set<String> includeFilenames, boolean removeRemote) {
        try {
            ChannelSftp channel = (ChannelSftp)session.openChannel("sftp");
            channel.connect();
            channel.cd(remoteDir.getAbsolutePath());

            byte[] buffer = new byte[1024];
            BufferedInputStream bis = null;
            File newFile = null;
            OutputStream os = null;
            BufferedOutputStream bos = null;
            int readCount = -1;
            @SuppressWarnings("unchecked")
            Vector<LsEntry> files = (Vector<LsEntry>)channel.ls(".");
            for(LsEntry entry : files) {
                if(includeFilenames != null) {
                    if(!includeFilenames.contains(entry.getFilename())) {
                        continue;
                    }
                }
                if(entry.getAttrs().getSize() > (1024 * 1024 * 50)) {
                    logger.info(entry.getFilename() + " is larger than 50MB. skipped");
                    continue;
                }
                if(!entry.getAttrs().isDir()) {
                    bis = new BufferedInputStream(channel.get(entry.getFilename()));
                    newFile = new File(localDir, entry.getFilename());
                    os = new FileOutputStream(newFile);
                    bos = new BufferedOutputStream(os);
                    while((readCount = bis.read(buffer)) > 0) {
                        bos.write(buffer, 0, readCount);
                    }
                    bis.close();
                    bos.close();
                }
            }

            if(removeRemote) {
                logger.debug("Removing temp dir");
                deleteFileRec(channel, remoteDir.getAbsolutePath());
            }

            channel.disconnect();
            return true;
        } catch(SftpException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(JSchException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(FileNotFoundException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    public void deleteFileRec(ChannelSftp channel, String filename) throws SftpException {
        if(channel.stat(filename).isDir()) {
            channel.cd(filename);
            @SuppressWarnings("unchecked")
            Vector<LsEntry> entries = (Vector<LsEntry>)channel.ls(".");
            for(LsEntry entry : entries) {
                if(entry.getFilename().equals(".") || entry.getFilename().equals("..")) {
                    continue;
                }
                deleteFileRec(channel, entry.getFilename());
            }
            channel.cd("..");
            channel.rmdir(filename);
        } else {
            channel.rm(filename);
        }
    }

    public File getRemoteDir() {
        return remoteDir;
    }
}
