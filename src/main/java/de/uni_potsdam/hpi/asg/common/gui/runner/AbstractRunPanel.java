package de.uni_potsdam.hpi.asg.common.gui.runner;

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

import javax.swing.JFileChooser;

import de.uni_potsdam.hpi.asg.common.gui.AbstractMainPanel;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel;
import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralBooleanParam;
import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralTextParam;
import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public abstract class AbstractRunPanel extends AbstractMainPanel {
    private static final long    serialVersionUID = -8028988850607540856L;

    protected AbstractParameters params;
    protected boolean            errorOccured;

    public AbstractRunPanel(AbstractParameters params) {
        this.params = params;
        this.params.setFrame(this);
        this.errorOccured = false;
    }

    public boolean hasErrorOccured() {
        return errorOccured;
    }

    protected void addOutSection(PropertiesPanel panel, int beginRow, String defOutfile) {
        addOutSection(panel, beginRow, defOutfile, AbstractParameters.DEF_OUT_DIR);
    }

    protected void addOutSection(PropertiesPanel panel, int beginRow, String defOutfile, String defOutDir) {
        panel.addTextEntry(beginRow, GeneralTextParam.OutDir, "Output directory", defOutDir, true, JFileChooser.DIRECTORIES_ONLY, true);
        panel.addTextEntry(beginRow + 1, GeneralTextParam.OutFile, "Output file name", defOutfile, false, null, false);
    }

    protected void addIOSection(PropertiesPanel panel, int beginRow, String defaultConfig, String defToolConfig) {
        addIOSection(panel, beginRow, defaultConfig, defToolConfig, AbstractParameters.OUTFILE_BASE_STR + CommonConstants.LOG_FILE_EXTENSION, AbstractParameters.OUTFILE_BASE_STR + CommonConstants.ZIP_FILE_EXTENSION);
    }

    protected void addIOSection(PropertiesPanel panel, int beginRow, String defaultConfig, String defaultToolConfig, String defLog, String defZip) {
        String defConfig = CommonConstants.DEF_CONFIG_DIR_STR + File.separator + defaultConfig;
        String defToolConfig = CommonConstants.DEF_CONFIG_DIR_STR + File.separator + defaultToolConfig;
        panel.addTextEntry(beginRow, GeneralTextParam.CfgFile, "Configuration file", defConfig, true, JFileChooser.FILES_ONLY, true);
        panel.addTextEntry(beginRow + 1, GeneralTextParam.ToolCfgFile, "External tools configuration file", defToolConfig, true, JFileChooser.FILES_ONLY, true);
        panel.addTextEntry(beginRow + 2, GeneralTextParam.WorkingDir, "Working directory", AbstractParameters.UNSET_STR, true, JFileChooser.DIRECTORIES_ONLY, true);
        panel.addSingleRadioButtonGroupEntry(beginRow + 3, "Log level", new String[]{"Nothing", "Errors", "+Warnings", "+Info"}, new GeneralBooleanParam[]{GeneralBooleanParam.LogLvl0, GeneralBooleanParam.LogLvl1, GeneralBooleanParam.LogLvl2, GeneralBooleanParam.LogLvl3}, 3);
        panel.addTextEntry(beginRow + 4, GeneralTextParam.LogFile, "Log file name", defLog, false, null, true);
        panel.addTextEntry(beginRow + 5, GeneralTextParam.TempFiles, "Temp files file name", defZip, false, null, true);
    }
}
