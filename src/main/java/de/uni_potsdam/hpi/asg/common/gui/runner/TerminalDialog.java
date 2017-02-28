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

import java.awt.Dimension;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JDialog;
import javax.swing.JTextArea;

public class TerminalDialog extends JDialog {
    private static final long serialVersionUID = 1L;

    private TerminalPanel     panel;

    public TerminalDialog(Window parent, String title, String commandline, Process p) {
        super(parent, title, ModalityType.APPLICATION_MODAL);
        panel = new TerminalPanel(commandline);
        this.getContentPane().add(panel);
        this.pack();
        this.setSize(new Dimension(900, 500));
        this.setLocationRelativeTo(null);

        this.addWindowListener(new TerminalWindowAdapter(p));

        Thread t = new Thread(new ProcessListener(p, this));
        t.start();
    }

    public JTextArea getText() {
        return panel.getText();
    }

    private class TerminalWindowAdapter extends WindowAdapter {

        private Process process;

        public TerminalWindowAdapter(Process process) {
            this.process = process;
        }

        @Override
        public void windowClosed(WindowEvent e) {
            super.windowClosed(e);
            process.destroy();
        }
    }

    private class ProcessListener implements Runnable {

        private Process process;
        private JDialog processDialog;

        public ProcessListener(Process process, JDialog processDialog) {
            this.process = process;
            this.processDialog = processDialog;
        }

        @Override
        public void run() {
            try {
                process.waitFor();
                Thread.sleep(2000);
            } catch(InterruptedException e) {
            }
            dispatchEvent(new WindowEvent(processDialog, WindowEvent.WINDOW_CLOSING));
        }
    }
}
