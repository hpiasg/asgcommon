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
import de.uni_potsdam.hpi.asg.common.iohelper.BasedirHelper;
import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public abstract class AbstractParameters {
    public static String UNSET_STR          = "$UNSET";
    public static String OUTFILE_BASE_STR   = "$OUTFILE";
    public static String OUTFILE_BASE_REGEX = "\\" + OUTFILE_BASE_STR;

    //@formatter:off
    public enum GeneralTextParam implements AbstractTextParam {
        /*general*/ OutDir, OutFile, CfgFile, WorkingDir, LogFile, TempFiles
    }

    public enum GeneralBooleanParam implements AbstractBooleanParam {
        /*general*/ LogLvl0, LogLvl1, LogLvl2, LogLvl3,
        /*debug*/ debug
    }
    //@formatter:on

    private String outfileEnding;

    public AbstractParameters(String outfileEnding) {
        this.outfileEnding = outfileEnding;
    }

    protected AbstractRunFrame frame;

    public void setFrame(AbstractRunFrame frame) {
        this.frame = frame;
    }

    public String getTextValue(AbstractTextParam param) {
        String str = frame.getTextValue(param);
        if(str.equals(UNSET_STR)) {
            return null;
        }
        if(str.equals(CommonConstants.USERDIR_STR)) {
            return System.getProperty("user.dir");
        }
        String retVal = BasedirHelper.replaceBasedir(str);
        if(param != GeneralTextParam.OutFile) {
            retVal = retVal.replaceAll(OUTFILE_BASE_REGEX, frame.getTextValue(GeneralTextParam.OutFile).replaceAll(outfileEnding, ""));
        }
        return retVal;
    }

    public boolean getBooleanValue(AbstractBooleanParam param) {
        return frame.getBooleanValue(param);
    }

    public abstract String getEnumValue(AbstractEnumParam param);
}
