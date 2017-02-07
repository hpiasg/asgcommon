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

import java.awt.event.WindowAdapter;

import javax.swing.JFileChooser;

import de.uni_potsdam.hpi.asg.common.gui.PropertiesFrame;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel;
import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralBooleanParam;
import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralTextParam;

public abstract class AbstractRunFrame extends PropertiesFrame {
    private static final long    serialVersionUID = -8028988850607540856L;

    protected AbstractParameters params;
    protected boolean            errorOccured;

    public AbstractRunFrame(String title, AbstractParameters params, WindowAdapter adapt) {
        super(title);
        this.params = params;
        this.params.setFrame(this);
        this.errorOccured = false;
        this.addWindowListener(adapt);
    }

    public boolean hasErrorOccured() {
        return errorOccured;
    }

    protected void addOutSection(PropertiesPanel panel, int beginRow, String defOutfile) {
        panel.addTextEntry(beginRow, GeneralTextParam.OutDir, "Output directory", AbstractParameters.userDirStr, true, JFileChooser.DIRECTORIES_ONLY, true);
        panel.addTextEntry(beginRow + 1, GeneralTextParam.OutFile, "Output file name", defOutfile, false, null, false);
    }

    protected void addIOSection(PropertiesPanel panel, int beginRow, String defaultConfig) {
        panel.addTextEntry(beginRow, GeneralTextParam.CfgFile, "Configuration file", defaultConfig, true, JFileChooser.FILES_ONLY, true);
        panel.addTextEntry(beginRow + 1, GeneralTextParam.WorkingDir, "Working directory", AbstractParameters.unsetStr, true, JFileChooser.DIRECTORIES_ONLY, true);
        panel.addSingleRadioButtonGroupEntry(beginRow + 2, "Log level", new String[]{"Nothing", "Errors", "+Warnings", "+Info"}, new GeneralBooleanParam[]{GeneralBooleanParam.LogLvl0, GeneralBooleanParam.LogLvl1, GeneralBooleanParam.LogLvl2, GeneralBooleanParam.LogLvl3}, 3);
        panel.addTextEntry(beginRow + 3, GeneralTextParam.LogFile, "Log file name", AbstractParameters.outfilebaseName + ".log", false, null, true);
        panel.addTextEntry(beginRow + 4, GeneralTextParam.TempFiles, "Temp files file name", AbstractParameters.outfilebaseName + ".zip", false, null, true);
    }

}
