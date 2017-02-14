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
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public class FileHelper {
    private final static Logger logger = LogManager.getLogger();

    private static FileHelper   instance;

    private String              workingdir;

    private FileHelper() {
    }

    public static FileHelper getInstance() {
        if(instance == null) {
            instance = new FileHelper();
        }
        return FileHelper.instance;
    }

    public void setWorkingdir(String workingdir) {
        this.workingdir = workingdir;
    }

    public static String getNewline() {
        return System.getProperty("line.separator");
    }

    /**
     * @deprecated Use {@link File#separator} instead
     */
    @Deprecated
    public static String getFileSeparator() {
        return File.separator;
    }

    /**
     * @deprecated Use {@link CommonConstants} instead
     */
    @Deprecated
    public static String getFileEx(Filetype type) {
        switch(type) {
            case balsa:
                return ".balsa";
            case breeze:
                return ".breeze";
            case stg:
                return ".g";
            case verilog:
                return ".v";
            case log:
                return ".log";
            default:
                return null;
        }
    }

    public boolean copyfile(File srFile, File dtFile) {
        try {
            // i dont know why, but sometimes FileInputStream doesnt find files with /a/b/c/../../b1
            // with canonical path, this works
            File newSrFile = new File(srFile.getCanonicalPath());
            InputStream in = null;
            OutputStream out = null;
            try {
                in = new FileInputStream(newSrFile);
                out = new FileOutputStream(dtFile);

                byte[] buf = new byte[1024];
                int len;
                while((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
                return true;
            } finally {
                if(in != null) {
                    in.close();
                }
                if(out != null) {
                    out.close();
                }
            }
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    public boolean copyfile(String srFile, String dtFile) {
        File f1 = new File(workingdir + srFile);
        File f2 = new File(workingdir + dtFile);
        return copyfile(f1, f2);
    }

    public boolean copyfile(String srFile, File dtFile) {
        File f1 = new File(workingdir + srFile);
        return copyfile(f1, dtFile);
    }

    public boolean copyfile(File srFile, String dtFile) {
        File f1 = new File(workingdir + dtFile);
        return copyfile(srFile, f1);
    }

    /**
     * @deprecated Use {@link CommonConstants} instead
     */
    @Deprecated
    public enum Filetype {
        verilog, breeze, balsa, stg, log
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
        File file = new File(workingdir + filename);
        return readFile(file);
    }

    public boolean writeFile(String filename, List<String> text) {
        return writeFile(new File(workingdir + filename), text);
    }

    public boolean writeFile(String filename, String text) {
        return writeFile(new File(workingdir + filename), text);
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

    public String mergeFileContents(List<String> filelist) {
        try {
            StringBuilder text = new StringBuilder();
            List<String> modules = new ArrayList<String>();
            for(String filename : filelist) {
                File file = new File(workingdir + filename);
                BufferedReader reader = new BufferedReader(new FileReader(file));
                String line;
                while((line = reader.readLine()) != null) {
                    if(line.startsWith("module")) {
                        String modulename = line.split(" ")[1];
                        if(modules.contains(modulename)) {
                            while(!line.startsWith("endmodule")) {
                                line = reader.readLine();
                            }
                        } else {
                            modules.add(modulename);
                            while(!line.startsWith("endmodule")) {
                                text.append(line + FileHelper.getNewline());
                                line = reader.readLine();
                            }
                            text.append(line + FileHelper.getNewline() + FileHelper.getNewline());
                        }
                    }
                }
                reader.close();
            }

            return text.toString();
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public File newTmpFile(String name) {
        File f = new File(workingdir + name);
        int id = 0;
        while(f.exists()) {
            String split[] = name.split("\\.");
            String newname = null;
            if(split.length == 2) {
                newname = workingdir + split[0] + (id++) + "." + split[1];
            } else {
                newname = workingdir + name + (id++);
            }
            f = new File(newname);
        }
        return f;
    }

    /**
     * @deprecated Use {@link BasedirHelper#getFileInBasedir(String)} instead
     */
    @Deprecated
    public static File getBasedirFile(String filename) {
        String basedir = BasedirHelper.getBasedir();
        return new File(basedir, filename);
    }

    public void deleteFileR(File file) {
        if(file.exists()) {
            if(file.isFile()) {
                file.delete();
                return;
            }
            if(file.isDirectory()) {
                String files[] = file.list();
                for(String str : files) {
                    deleteFileR(new File(file, str));
                }
                file.delete();
            }
        }
    }

}