package de.uni_potsdam.hpi.asg.common.invoker;

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

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfig;
import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfigFile;
import de.uni_potsdam.hpi.asg.common.iohelper.WorkingdirGenerator;

public class ExternalToolsInvoker {
    private final static Logger         logger = LogManager.getLogger();

    private static ExternalToolsInvoker instance;

    private File                        workingDir;
    private ExternalToolsConfig         config;

    public static ExternalToolsInvoker getInstance() {
        if(instance == null) {
            logger.error("ExternalToolsInvoker not set. Run init!");
        }
        return instance;
    }

    public static boolean init(File configFile) {
        ExternalToolsInvoker tmpInst = new ExternalToolsInvoker();

        if(WorkingdirGenerator.getInstance() == null) {
            logger.error("WorkingDirGenerator not initialised! Init it before Invoker");
            return false;
        }
        tmpInst.workingDir = WorkingdirGenerator.getInstance().getWorkingDir();

        if(configFile == null) {
            logger.error("No tools config file");
            return false;
        }
        tmpInst.config = ExternalToolsConfigFile.readIn(configFile);
        if(tmpInst.config == null) {
            return false;
        }

        ExternalToolsInvoker.instance = tmpInst;
        return true;
    }

    private ExternalToolsInvoker() {
    }
}
