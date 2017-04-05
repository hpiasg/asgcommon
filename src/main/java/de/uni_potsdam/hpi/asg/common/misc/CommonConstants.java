package de.uni_potsdam.hpi.asg.common.misc;

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

import de.uni_potsdam.hpi.asg.common.iohelper.BasedirHelper;
import de.uni_potsdam.hpi.asg.common.iohelper.OperatingSystemHelper;

public class CommonConstants {

    private CommonConstants() {
    }

    // env abstract directories
    public static final String USERDIR_STR                = "$USER-DIR";
    public static final String BASEDIR_STR                = "$BASEDIR";
    public static final String BASEDIR_REGEX              = "\\" + BASEDIR_STR;

    // default directories
    public static final String DEF_TECH_DIR_STR           = "$BASEDIR/tech";
    public static final File   DEF_TECH_DIR_FILE          = BasedirHelper.replaceBasedirAsFile(DEF_TECH_DIR_STR);
    public static final File   DEF_BALSA_TECH_DIR_FILE    = BasedirHelper.replaceBasedirAsFile("$BASEDIR/tools/balsa/share/tech");
    public static final String DEF_CONFIG_DIR_STR         = "$BASEDIR/config";
    public static final File   DEF_CONFIG_DIR_FILE        = BasedirHelper.replaceBasedirAsFile(DEF_CONFIG_DIR_STR);
    public static final File   DEF_BIN_DIR_FILE           = BasedirHelper.replaceBasedirAsFile("$BASEDIR/bin");

    // script files
    public static final String SCRIPT_FILE_EXTENSION      = OperatingSystemHelper.getScriptExtension();

    // output files
    public static final String ZIP_FILE_EXTENSION         = ".zip";
    public static final String LOG_FILE_EXTENSION         = ".log";

    // technology files
    public static final String XMLTECH_FILE_EXTENSION     = ".xml";
    public static final String GENLIB_FILE_EXTENSION      = ".lib";
    public static final String EXPORT_TECH_FILE_EXTENSION = ".zip";

    // misc files
    public static final String BALSA_FILE_EXTENSION       = ".balsa";
    public static final String BREEZE_FILE_EXTENSION      = ".breeze";
    public static final String VERILOG_FILE_EXTENSION     = ".v";
    public static final String STG_FILE_EXTENSION         = ".g";

}
