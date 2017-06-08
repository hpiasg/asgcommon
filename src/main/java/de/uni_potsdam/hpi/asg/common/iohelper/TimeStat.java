package de.uni_potsdam.hpi.asg.common.iohelper;

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
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;

public class TimeStat {

    private File             file;
    private FileOutputStream stream;
    private long             userTime;
    private long             systemTime;

    private TimeStat(File file) {
        this.file = file;
        this.userTime = 0;
        this.systemTime = 0;
    }

    public static TimeStat create() {
        File file = null;
        try {
            file = File.createTempFile("timestat", ".txt");
        } catch(IOException e) {
            return null;
        }
        if(file == null) {
            return null;
        }
        return new TimeStat(file);
    }

    public String getRemoteCmdStr() {
        return "/usr/bin/time -f U:%U\\\\nS:%S\\\\n";
    }

    public String[] getLocalCmd() {
        //@formatter:off
        return new String[] {
            "/usr/bin/time",
            "-f", "U:%U\nS:%S\n",
            "-o", file.getAbsolutePath()
        };
        //@formatter:on
    }

    public boolean evaluate() {
        List<String> lines = FileHelper.getInstance().readFile(file);
        userTime = 0;
        systemTime = 0;
        for(String line : lines) {
            if(line.startsWith("U:")) {
                userTime = (long)(Float.parseFloat(line.replace("U:", "")) * 1000);
            }
            if(line.startsWith("S:")) {
                systemTime = (long)(Float.parseFloat(line.replace("S:", "")) * 1000);
            }
        }
        file.delete();
        return true;
    }

    public long getSystemTime() {
        return systemTime;
    }

    public long getUserTime() {
        return userTime;
    }

    public FileOutputStream getStream() {
        if(stream == null) {
            try {
                stream = new FileOutputStream(file);
            } catch(FileNotFoundException e) {
                return null;
            }
        }
        return stream;
    }
}
