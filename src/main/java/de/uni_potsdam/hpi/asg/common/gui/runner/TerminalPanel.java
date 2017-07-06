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

import java.awt.BorderLayout;
import java.awt.Font;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.text.DefaultCaret;

public class TerminalPanel extends JPanel {
    private static final long serialVersionUID = 4466944474984990614L;

    private JTextArea         text;

    public TerminalPanel(String commandline) {
        this.setLayout(new BorderLayout());

        JTextField cmdField = new JTextField(commandline);
        cmdField.setEditable(false);
        this.add(cmdField, BorderLayout.PAGE_START);

        text = new JTextArea();
        text.setEditable(false);
        text.setFont(new Font("monospaced", Font.PLAIN, 12));
        JScrollPane spane = new JScrollPane(text);
        DefaultCaret caret = (DefaultCaret)text.getCaret();
        caret.setUpdatePolicy(DefaultCaret.ALWAYS_UPDATE);
        this.add(spane, BorderLayout.CENTER);
    }

    public JTextArea getText() {
        return text;
    }
}
