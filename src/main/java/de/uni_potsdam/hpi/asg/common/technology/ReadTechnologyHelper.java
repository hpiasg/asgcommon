package de.uni_potsdam.hpi.asg.common.technology;

/*
 * Copyright (C) 2017 - 2018 Norman Kluge
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

import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public class ReadTechnologyHelper {
    private static final Logger logger = LogManager.getLogger();

    public static Technology read(String optTechName, File optTechFile, String cfgTech) {
        if(optTechName != null) {
            File f = new File(CommonConstants.DEF_TECH_DIR_FILE, optTechName + CommonConstants.XMLTECH_FILE_EXTENSION);
            if(f.exists()) {
                logger.debug("Using installed technology '" + optTechName + "'");
                return Technology.readIn(f);
            } else {
                logger.warn("Installed technology '" + optTechName + "' does not exist. Trying other options..");
            }
        }

        if(optTechFile != null) {
            if(optTechFile.exists()) {
                logger.debug("Using options technology file: " + optTechFile.getAbsolutePath());
                return Technology.readIn(optTechFile);
            } else {
                logger.warn("Options technology file " + optTechFile.getAbsolutePath() + " not found. Trying default from config");
            }
        }

        if(cfgTech != null) {
            File f = new File(CommonConstants.DEF_TECH_DIR_FILE, cfgTech + CommonConstants.XMLTECH_FILE_EXTENSION);
            if(f.exists()) {
                logger.debug("Using default installed technology '" + optTechName + "'");
                return Technology.readIn(f);
            } else {
                logger.warn("Default installed technology '" + optTechName + "' does not exist");
            }
        }

        return null;
    }
}
