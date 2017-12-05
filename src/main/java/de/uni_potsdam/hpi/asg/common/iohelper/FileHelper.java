package de.uni_potsdam.hpi.asg.common.iohelper;

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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.Files;

public class FileHelper {
    private final static Logger logger = LogManager.getLogger();

    private static FileHelper   instance;

    private File                workingDir;

    private FileHelper() {
    }

    public static FileHelper getInstance() {
        if(instance == null) {
            instance = new FileHelper();
        }
        return FileHelper.instance;
    }

    public void setWorkingdir(File workingDir) {
        this.workingDir = workingDir;
    }

    public static String getNewline() {
        return System.getProperty("line.separator");
    }

    public boolean copyfile(File srFile, File dtFile) {
        try {
            Files.copy(srFile, dtFile);
            return true;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return false;
    }

    public boolean copyfile(String srFile, String dtFile) {
        File f1 = new File(workingDir, srFile);
        File f2 = new File(workingDir, dtFile);
        return copyfile(f1, f2);
    }

    public boolean copyfile(String srFile, File dtFile) {
        File f1 = new File(workingDir, srFile);
        return copyfile(f1, dtFile);
    }

    public boolean copyfile(File srFile, String dtFile) {
        File f1 = new File(workingDir, dtFile);
        return copyfile(srFile, f1);
    }

    public List<String> readFile(File file) {
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;
            List<String> text = new ArrayList<String>();
            while((line = reader.readLine()) != null) {
                text.add(line);
            }
            reader.close();
            return text;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public List<String> readFile(String filename) {
        File file = new File(workingDir, filename);
        return readFile(file);
    }

    public boolean writeFile(String filename, List<String> text) {
        return writeFile(new File(workingDir, filename), text);
    }

    public boolean writeFile(String filename, String text) {
        return writeFile(new File(workingDir, filename), text);
    }

    public boolean writeFile(File file, List<String> text) {
        StringBuilder builder = new StringBuilder();
        for(String str : text) {
            builder.append(str + FileHelper.getNewline());
        }
        return writeFile(file, builder.toString());
    }

    public boolean writeFile(File file, String text) {
        if(text != null) {
            try {
                FileWriter writer = new FileWriter(file);
                writer.write(text);
                writer.close();
                return true;
            } catch(IOException e) {
                logger.error(e.getLocalizedMessage());
                return false;
            }
        } else {
            logger.error("Nothing to write");
            return false;
        }
    }

    public File newTmpFile(String name) {
        String split[] = name.split("\\.");
        try {
            return File.createTempFile(split[0], split[1], workingDir);
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
        }
        return null;
    }
}