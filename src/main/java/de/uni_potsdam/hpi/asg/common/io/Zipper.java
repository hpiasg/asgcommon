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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Zipper {
    public final static Logger logger = LogManager.getLogger();

    private static Zipper      instance;

    private String             workingdir;

    private Zipper() {
    };

    public static Zipper getInstance() {
        if(instance == null) {
            instance = new Zipper();
        }
        return instance;
    }

    public void setWorkingdir(String workingdir) {
        this.workingdir = workingdir;
    }

    public boolean zip(File zipFile) {
        return zip(zipFile, new File(workingdir));
    }

    public boolean zip(File zipFile, File dir) {
        byte[] buffer = new byte[1024];
        try {
            List<File> filelist = new ArrayList<File>();
            if(!getFileList(dir, filelist)) {
                logger.error("Could not get list of files for zipping");
                return false;
            }

            FileOutputStream fos = new FileOutputStream(zipFile);
            ZipOutputStream zos = new ZipOutputStream(fos);
            FileInputStream in = null;
            for(File file : filelist) {
                String subname = file.getAbsolutePath().substring(dir.getAbsolutePath().length(), file.getAbsolutePath().length());
                ZipEntry ze = new ZipEntry(subname);
                zos.putNextEntry(ze);
                in = new FileInputStream(file);
                int len;
                while((len = in.read(buffer)) > 0) {
                    zos.write(buffer, 0, len);
                }
                in.close();
                zos.closeEntry();
            }
            zos.close();
            return true;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    private boolean getFileList(File file, List<File> files) {
        if(file == null) {
            return false;
        }
        if(file.isFile()) {
            files.add(file);
        } else if(file.isDirectory()) {
            for(String str : file.list()) {
                if(!getFileList(new File(file, str), files)) {
                    logger.error("Could not zip subentry: " + str);
                }
            }
        } else {
            logger.warn("Unknown filesystem entry: " + file.toString());
        }
        return true;
    }
}
