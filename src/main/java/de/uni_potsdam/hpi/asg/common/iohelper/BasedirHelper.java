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

import org.apache.commons.lang3.SystemUtils;

import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public class BasedirHelper {
    private BasedirHelper() {
    }

    public static String replaceBasedir(String str) {
        String basedir = getBasedir();
        return str.replaceAll(CommonConstants.BASEDIR_REGEX, basedir);
    }

    public static File replaceBasedirAsFile(String str) {
        return new File(replaceBasedir(str));
    }

    public static String getBasedir() {
        String basedir = System.getProperty("basedir");
        if(SystemUtils.IS_OS_WINDOWS) {
            basedir = basedir.replaceAll("\\\\", "/");
        }
        return basedir;
    }

    public static File getFileInBasedir(String filename) {
        String basedir = getBasedir();
        return new File(basedir, filename);
    }
}
