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

import java.awt.Window;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralBooleanParam;
import de.uni_potsdam.hpi.asg.common.gui.runner.AbstractParameters.GeneralTextParam;

public abstract class AbstractRunner {

    public enum TerminalMode {
        textfield, frame, dialog
    }

    private AbstractParameters params;

    public AbstractRunner(AbstractParameters params) {
        this.params = params;
    }

    protected void exec(List<String> cmd, String label, TerminalMode mode, JTextArea text, Window parent) {
        StringBuilder str = new StringBuilder();
        for(String s : cmd) {
            str.append(s + " ");
        }

        ProcessBuilder pb = new ProcessBuilder(cmd);
        Process process = null;
        try {
            process = pb.start();
        } catch(IOException e) {
            e.printStackTrace();
        }

        Window window = null;
        switch(mode) {
            case dialog:
                TerminalDialog tdia = new TerminalDialog(parent, label, str.toString(), process);
                tdia.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
                window = tdia;
                text = tdia.getText();
                break;
            case frame:
                TerminalFrame tframe = new TerminalFrame(label, str.toString(), process);
                tframe.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                window = tframe;
                text = tframe.getText();
                break;
            case textfield:
                break;
        }

        if(text != null) {
            IOStreamReader ioreader = new IOStreamReader(process, text);
            Thread streamThread = new Thread(ioreader);
            streamThread.start();
        }
        if(window != null) {
            window.setVisible(true);
        }
    }

    protected void addStandardIOParams(List<String> cmd, String outfileOption) {
        String outDir = params.getTextValue(GeneralTextParam.OutDir);
        String outFile = params.getTextValue(GeneralTextParam.OutFile);
        if(outFile != null) {
            cmd.add(outfileOption);
            if(outDir == null) {
                cmd.add(outFile);
            } else {
                File file = new File(outDir, outFile);
                cmd.add(file.getAbsolutePath());
            }
        }

        String cfgFile = params.getTextValue(GeneralTextParam.CfgFile);
        if(cfgFile != null) {
            cmd.add("-cfg");
            cmd.add(cfgFile);
        }

        String workDir = params.getTextValue(GeneralTextParam.WorkingDir);
        if(workDir != null) {
            cmd.add("-w");
            cmd.add(workDir);
        }

        cmd.add("-o");
        if(params.getBooleanValue(GeneralBooleanParam.LogLvl0)) {
            cmd.add("0");
        } else if(params.getBooleanValue(GeneralBooleanParam.LogLvl1)) {
            cmd.add("1");
        } else if(params.getBooleanValue(GeneralBooleanParam.LogLvl2)) {
            cmd.add("2");
        } else if(params.getBooleanValue(GeneralBooleanParam.LogLvl3)) {
            cmd.add("3");
        }

        String logFile = params.getTextValue(GeneralTextParam.LogFile);
        if(logFile != null) {
            cmd.add("-log");
            if(outDir == null) {
                cmd.add(logFile);
            } else {
                File file = new File(outDir, logFile);
                cmd.add(file.getAbsolutePath());
            }
        }

        String zipFile = params.getTextValue(GeneralTextParam.TempFiles);
        if(zipFile != null) {
            cmd.add("-zip");
            if(outDir == null) {
                cmd.add(zipFile);
            } else {
                File file = new File(outDir, zipFile);
                cmd.add(file.getAbsolutePath());
            }
        }
    }
}
