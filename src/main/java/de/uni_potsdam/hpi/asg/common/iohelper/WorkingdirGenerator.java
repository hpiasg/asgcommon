package de.uni_potsdam.hpi.asg.common.iohelper;

/*
 * Copyright (C) 2015 - 2017 Norman Kluge
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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class WorkingdirGenerator {
    private static final Logger        logger   = LogManager.getLogger();
    private static final String        filesep  = System.getProperty("file.separator");
    private static final String        ostmpdir = System.getProperty("java.io.tmpdir");

    @Deprecated
    private String                     workingdir;
    private File                       workingDir;

    private static WorkingdirGenerator instance;

    private WorkingdirGenerator() {
    }

    public static WorkingdirGenerator getInstance() {
        if(instance == null) {
            instance = new WorkingdirGenerator();
        }
        return instance;
    }

    public String create(File cmdlinedir, String configdir, String defaultsubdir, Invoker invoker) {
        // Temp dir
        String wdirstr = null;
        if(cmdlinedir != null) {
            wdirstr = cmdlinedir.getAbsolutePath();
        } else if(configdir != null && !configdir.equals("")) {
            wdirstr = configdir.endsWith(filesep) ? configdir.substring(0, configdir.length() - 1) : configdir;
        } else {
            String ostmpdir2 = ostmpdir.endsWith(filesep) ? ostmpdir.substring(0, ostmpdir.length() - 1) : ostmpdir;
            wdirstr = ostmpdir2 + filesep + defaultsubdir;
        }
        workingDir = new File(wdirstr);
        int tmpnum = 0;
        while(!workingDir.mkdirs()) {
            workingDir = new File(wdirstr + Integer.toString(tmpnum++) + filesep);
        }
        workingdir = workingDir.getAbsolutePath() + File.separator;
        FileHelper.getInstance().setWorkingdir(workingdir);
        if(invoker != null) {
            invoker.setWorkingdir(workingdir);
        }
        Zipper.getInstance().setWorkingdir(workingdir);
        logger.debug("Tmp dir: " + workingdir);

        return workingdir;
    }

    public void delete() {
        if(workingdir != null) {
            FileHelper.getInstance().deleteFileR(new File(workingdir));
            workingdir = null;
        }
    }

    @Deprecated
    public String getWorkingdir() {
        return workingdir;
    }

    public File getWorkingDir() {
        return workingDir;
    }
}
