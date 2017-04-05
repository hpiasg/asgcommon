package de.uni_potsdam.hpi.asg.common.iohelper;

/*
 * Copyright (C) 2017 Florian Meinel
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

import org.apache.commons.lang3.SystemUtils;

public class OperatingSystemHelper {

    private static final String WINDOWS_SCRIPT_FILE_EXTENSION = ".bat";
    private static final String UNIX_SCRIPT_FILE_EXTENSION    = "";

    public static String getScriptExtension() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS_SCRIPT_FILE_EXTENSION;
        }
        return UNIX_SCRIPT_FILE_EXTENSION;
    }
}
