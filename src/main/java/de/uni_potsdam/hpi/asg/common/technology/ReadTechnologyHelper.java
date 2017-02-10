package de.uni_potsdam.hpi.asg.common.technology;

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

import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;

public class ReadTechnologyHelper {
    private static final Logger logger = LogManager.getLogger();

    public static Technology read(File optTech, String cfgTech) {
        if(optTech != null) {
            if(optTech.exists()) {
                logger.debug("Using options technology file: " + optTech.getAbsolutePath());
                return Technology.readIn(optTech);
            } else {
                logger.warn("Options technology file " + optTech.getAbsolutePath() + " not found. Trying default from config");
            }
        } else {
            logger.debug("No technology in options passed. Trying default from config");
        }

        if(cfgTech != null) {
            File cfgTechFile = FileHelper.getInstance().replaceBasedir(cfgTech);
            if(cfgTechFile.exists()) {
                logger.debug("Using config technology file: " + cfgTechFile.getAbsolutePath());
                return Technology.readIn(cfgTechFile);
            } else {
                logger.warn("Config technology file " + cfgTechFile.getAbsolutePath() + " not found.");
            }
        } else {
            logger.warn("No default technology in config file defined");
        }

        return null;
    }
}
