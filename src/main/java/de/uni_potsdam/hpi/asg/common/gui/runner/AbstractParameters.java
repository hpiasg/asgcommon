package de.uni_potsdam.hpi.asg.common.gui.runner;

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

import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractBooleanParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractEnumParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractTextParam;
import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;

public abstract class AbstractParameters {
    public static String unsetStr        = "$UNSET";
    public static String userDirStr      = "$USER-DIR";
    public static String basedirStr      = "$BASEDIR";
    public static String outfilebaseName = "$OUTFILE";

    //@formatter:off
    public enum GeneralTextParam implements AbstractTextParam {
        /*general*/ OutDir, OutFile, CfgFile, WorkingDir, LogFile, TempFiles
    }

    public enum GeneralBooleanParam implements AbstractBooleanParam {
        /*general*/ LogLvl0, LogLvl1, LogLvl2, LogLvl3,
        /*debug*/ debug
    }
    //@formatter:on

    protected AbstractRunFrame frame;

    public void setFrame(AbstractRunFrame frame) {
        this.frame = frame;
    }

    public String getTextValue(AbstractTextParam param) {
        String str = frame.getTextValue(param);
        if(str.equals(unsetStr)) {
            return null;
        }
        if(str.equals(userDirStr)) {
            return System.getProperty("user.dir");
        }
        String retVal = str.replaceAll("\\" + basedirStr, FileHelper.getInstance().getBasedir());
        if(param != GeneralTextParam.OutFile) {
            retVal = retVal.replaceAll("\\" + outfilebaseName, frame.getTextValue(GeneralTextParam.OutFile).replaceAll(".v", ""));
        }
        return retVal;
    }

    public boolean getBooleanValue(AbstractBooleanParam param) {
        return frame.getBooleanValue(param);
    }

    public abstract String getEnumValue(AbstractEnumParam param);
}
