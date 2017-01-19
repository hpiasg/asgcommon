package de.uni_potsdam.hpi.asg.common.gui;

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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

public abstract class ParamFrame extends JFrame {
    private static final long serialVersionUID = 3803172863281872813L;

    public interface AbstractTextParam {
    }

    public interface AbstractBooleanParam {
    }

    public interface AbstractEnumParam {
    }

    protected ParamFrame                                parent;

    protected Map<AbstractTextParam, JTextField>        textfields;
    protected Map<AbstractBooleanParam, AbstractButton> buttons;
    protected Map<AbstractEnumParam, JComboBox<String>> enumfields;

    public ParamFrame(String title) {
        super(title);
        textfields = new HashMap<>();
        buttons = new HashMap<>();
        enumfields = new HashMap<>();
        parent = this;
    }

    protected void constructSingleRadioButtonGroup(JPanel panel, int row, String labelStr, String[] labels, AbstractBooleanParam[] param, int defaultVal) {
        if(labels.length != param.length) {
            System.err.println("Labels != Param");
            return;
        }

        constructLabelCell(panel, row, labelStr);

        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new FlowLayout());

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.anchor = GridBagConstraints.NORTHWEST;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridx = 1;
        gbc_panel.gridy = row;
        panel.add(internalPanel, gbc_panel);

        ButtonGroup group = new ButtonGroup();
        for(int i = 0; i < labels.length; i++) {
            JRadioButton button = new JRadioButton(labels[i]);
            buttons.put(param[i], button);
            internalPanel.add(button);
            group.add(button);
            if(i == defaultVal) {
                button.setSelected(true);
            }
        }
    }

    protected void constructComboBox(JPanel panel, int row, AbstractEnumParam paramName, String labelStr, String[] values) {
        constructLabelCell(panel, row, labelStr);

        JComboBox<String> combobox = new JComboBox<>(values);
        enumfields.put(paramName, combobox);

        GridBagConstraints gbc_combobox = new GridBagConstraints();
        gbc_combobox.anchor = GridBagConstraints.NORTHWEST;
        gbc_combobox.insets = new Insets(0, 0, 5, 0);
        gbc_combobox.gridx = 1;
        gbc_combobox.gridy = row;
        panel.add(combobox, gbc_combobox);
    }

    protected void constructCheckboxEntry(JPanel panel, int row, AbstractBooleanParam paramName, String labelStr, boolean defaultvalue) {
        constructLabelCell(panel, row, labelStr);

        JCheckBox checkbox = new JCheckBox("");
        buttons.put(paramName, checkbox);

        GridBagConstraints gbc_checkbox = new GridBagConstraints();
        gbc_checkbox.anchor = GridBagConstraints.NORTHWEST;
        gbc_checkbox.insets = new Insets(0, 0, 5, 0);
        gbc_checkbox.gridx = 1;
        gbc_checkbox.gridy = row;
        panel.add(checkbox, gbc_checkbox);
        checkbox.setSelected(defaultvalue);
    }

    protected void constructTextEntry(JPanel panel, int row, AbstractTextParam paramName, String labelStr, final String defaultvalue) {
        constructTextEntry(panel, row, paramName, labelStr, defaultvalue, false, null, false, false, null);
    }

    protected void constructTextEntry(JPanel panel, int row, AbstractTextParam paramName, String labelStr, final String defaultvalue, boolean hasPathButton, final Integer filemode, boolean hasdefaultcheckbox) {
        constructTextEntry(panel, row, paramName, labelStr, defaultvalue, hasPathButton, filemode, hasdefaultcheckbox, false, null);
    }

    protected void constructTextEntry(JPanel panel, int row, AbstractTextParam paramName, String labelStr, final String defaultvalue, boolean hasPathButton, final Integer filemode, boolean hasdefaultcheckbox, boolean hashelpbutton, String helptext) {
        constructLabelCell(panel, row, labelStr);
        final JTextField textfield = constructTextfieldCell(panel, row, paramName, defaultvalue, hasdefaultcheckbox);
        final JButton pathbutton = hasPathButton ? constructPathButtonCell(panel, row, filemode, hasdefaultcheckbox, textfield) : null;
        if(hasdefaultcheckbox) {
            constructDefaultCheckboxCell(panel, row, defaultvalue, textfield, pathbutton);
        }
        if(hashelpbutton) {
            constructHelpButtonCell(panel, row, helptext);
        }
    }

    protected void constructLabelCell(JPanel panel, int row, String labelStr) {
        JLabel label = new JLabel(labelStr);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 0, 5, 5);
        gbc_label.anchor = GridBagConstraints.WEST;
        gbc_label.gridx = 0;
        gbc_label.gridy = row;
        panel.add(label, gbc_label);
    }

    protected void constructDefaultCheckboxCell(JPanel panel, int row, final String defaultvalue, final JTextField textfield, final JButton pathbutton) {
        JCheckBox defaultcheckbox = new JCheckBox("Default");
        defaultcheckbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    if(pathbutton != null) {
                        pathbutton.setEnabled(false);
                    }
                    textfield.setText(defaultvalue);
                    textfield.setEnabled(false);
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    if(pathbutton != null) {
                        pathbutton.setEnabled(true);
                    }
                    textfield.setText("");
                    textfield.setEnabled(true);
                } else {
                    System.err.println("error");
                }
            }
        });

        GridBagConstraints gbc_defaultcheckbox = new GridBagConstraints();
        gbc_defaultcheckbox.anchor = GridBagConstraints.NORTHWEST;
        gbc_defaultcheckbox.insets = new Insets(0, 0, 5, 0);
        gbc_defaultcheckbox.gridx = 3;
        gbc_defaultcheckbox.gridy = row;
        panel.add(defaultcheckbox, gbc_defaultcheckbox);
        defaultcheckbox.setSelected(true);
    }

    protected JButton constructPathButtonCell(JPanel panel, int row, final Integer filemode, boolean hasdefaultcheckbox, final JTextField textfield) {
        final JButton pathbutton = new JButton("...");
        pathbutton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                fileChooser.setFileSelectionMode(filemode);
                int result = fileChooser.showOpenDialog(parent);
                if(result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    textfield.setText(selectedFile.getAbsolutePath());
                }
            }
        });
        GridBagConstraints gbc_pathbutton = new GridBagConstraints();
        gbc_pathbutton.insets = new Insets(0, 0, 5, 5);
        gbc_pathbutton.gridx = 2;
        gbc_pathbutton.gridy = row;
        panel.add(pathbutton, gbc_pathbutton);
        if(hasdefaultcheckbox) {
            pathbutton.setEnabled(false);
        }
        return pathbutton;
    }

    protected JTextField constructTextfieldCell(JPanel panel, int row, AbstractTextParam paramName, final String defaultvalue, boolean hasdefaultcheckbox) {
        final JTextField textfield = new JTextField();
        textfields.put(paramName, textfield);

        GridBagConstraints gbc_text = new GridBagConstraints();
        gbc_text.fill = GridBagConstraints.HORIZONTAL;
        gbc_text.insets = new Insets(0, 0, 5, 5);
        gbc_text.gridx = 1;
        gbc_text.gridy = row;
        panel.add(textfield, gbc_text);
        textfield.setColumns(10);
        textfield.setText(defaultvalue);
        if(hasdefaultcheckbox) {
            textfield.setEnabled(false);
        }
        return textfield;
    }

    protected void constructHelpButtonCell(JPanel panel, int row, final String helptext) {
        final JLabel helpbutton = new JLabel(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D)g;
                RenderingHints rh = new RenderingHints(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setRenderingHints(rh);
                g2.setStroke(new BasicStroke(0.5f));
                g2.setColor(Color.BLACK);
                g2.drawOval(x + 1, y + 1, 23, 23);
                g2.setFont(new Font("default", Font.PLAIN, 15));
                g2.drawString("?", x + 9, y + 18);
            }

            @Override
            public int getIconWidth() {
                return 25;
            }

            @Override
            public int getIconHeight() {
                return 25;
            }
        });
        helpbutton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                JOptionPane.showMessageDialog(parent, helptext, "Info", JOptionPane.PLAIN_MESSAGE);
            }
        });
        GridBagConstraints gbc_helpbutton = new GridBagConstraints();
        gbc_helpbutton.anchor = GridBagConstraints.CENTER;
        gbc_helpbutton.fill = GridBagConstraints.NONE;
        gbc_helpbutton.insets = new Insets(0, 0, 5, 0);
        gbc_helpbutton.gridx = 4;
        gbc_helpbutton.gridy = row;
        panel.add(helpbutton, gbc_helpbutton);
    }

    public String getTextValue(AbstractTextParam param) {
        return textfields.get(param).getText();
    }

    public boolean getBooleanValue(AbstractBooleanParam param) {
        return buttons.get(param).isSelected();
    }

    public int getEnumValue(AbstractEnumParam param) {
        return enumfields.get(param).getSelectedIndex();
    }
}
