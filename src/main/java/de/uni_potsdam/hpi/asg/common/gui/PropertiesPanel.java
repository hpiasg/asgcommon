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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class PropertiesPanel extends JPanel {
    private static final long serialVersionUID = 7178726681556068358L;

    public interface AbstractTextParam {
    }

    public interface AbstractBooleanParam {
    }

    public interface AbstractEnumParam {
    }

    public interface AbstractIntParam {
    }

    public interface AbstractDoubleParam {
    }

    protected Window                                    parent;

    protected Map<AbstractTextParam, JTextField>        textfields;
    protected Map<AbstractBooleanParam, AbstractButton> buttons;
    protected Map<AbstractEnumParam, JComboBox<String>> enumfields;
    protected Map<AbstractIntParam, JSlider>            sliders;
    protected Map<AbstractDoubleParam, JSpinner>        spinners;

    public PropertiesPanel(Window parent) {
        textfields = new HashMap<>();
        buttons = new HashMap<>();
        enumfields = new HashMap<>();
        sliders = new HashMap<>();
        spinners = new HashMap<>();
        this.parent = parent;
    }

    public void addSingleRadioButtonGroupEntry(int row, String labelStr, String[] labels, AbstractBooleanParam[] param, int defaultVal) {
        if(labels.length != param.length) {
            System.err.println("Labels != Param");
            return;
        }

        addLabelCell(row, labelStr);

        JPanel internalPanel = new JPanel();
        internalPanel.setLayout(new FlowLayout());

        GridBagConstraints gbc_panel = new GridBagConstraints();
        gbc_panel.anchor = GridBagConstraints.LINE_START;
        gbc_panel.insets = new Insets(0, 0, 5, 0);
        gbc_panel.gridx = 1;
        gbc_panel.gridy = row;
        this.add(internalPanel, gbc_panel);

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

    public void addComboBoxEntry(int row, AbstractEnumParam paramName, String labelStr, String[] values) {
        addLabelCell(row, labelStr);

        JComboBox<String> combobox = new JComboBox<>(values);
        enumfields.put(paramName, combobox);

        GridBagConstraints gbc_combobox = new GridBagConstraints();
        gbc_combobox.anchor = GridBagConstraints.LINE_START;
        gbc_combobox.insets = new Insets(0, 0, 5, 0);
        gbc_combobox.gridx = 1;
        gbc_combobox.gridy = row;
        this.add(combobox, gbc_combobox);
    }

    public void addCheckboxEntry(int row, AbstractBooleanParam paramName, String labelStr, boolean defaultvalue) {
        addLabelCell(row, labelStr);

        JCheckBox checkbox = new JCheckBox("");
        buttons.put(paramName, checkbox);

        GridBagConstraints gbc_checkbox = new GridBagConstraints();
        gbc_checkbox.anchor = GridBagConstraints.LINE_START;
        gbc_checkbox.insets = new Insets(0, 0, 5, 0);
        gbc_checkbox.gridx = 1;
        gbc_checkbox.gridy = row;
        this.add(checkbox, gbc_checkbox);
        checkbox.setSelected(defaultvalue);
    }

    public void addSliderEntry(int row, AbstractIntParam paramName, String labelStr, int minValue, int maxValue, int defaultValue) {
        addLabelCell(row, labelStr);
        addSliderCell(row, paramName, minValue, maxValue, defaultValue);
    }

    public void addSpinnerEntry(int row, AbstractDoubleParam paramName, String labelStr, double step, double defaultValue) {
        addLabelCell(row, labelStr);
        addSpinnerCell(row, paramName, step, defaultValue);
    }

    public void addTextEntry(int row, AbstractTextParam paramName, String labelStr, final String defaultvalue) {
        addTextEntry(row, paramName, labelStr, defaultvalue, false, null, false, false, null);
    }

    public void addTextEntry(int row, AbstractTextParam paramName, String labelStr, final String defaultvalue, boolean hasPathButton, final Integer filemode, boolean hasdefaultcheckbox) {
        addTextEntry(row, paramName, labelStr, defaultvalue, hasPathButton, filemode, hasdefaultcheckbox, false, null);
    }

    public void addTextEntry(int row, AbstractTextParam paramName, String labelStr, final String defaultvalue, boolean hasPathButton, final Integer filemode, boolean hasdefaultcheckbox, boolean hashelpbutton, String helptext) {
        addLabelCell(row, labelStr);
        final JTextField textfield = addTextfieldCell(row, paramName, defaultvalue, hasdefaultcheckbox);
        final JButton pathbutton = hasPathButton ? addPathButtonCell(row, filemode, hasdefaultcheckbox, textfield) : null;
        if(hasdefaultcheckbox) {
            addDefaultCheckboxCell(row, defaultvalue, textfield, pathbutton);
        }
        if(hashelpbutton) {
            addHelpButtonCell(row, helptext);
        }
    }

    public void addLabelEntry(int row, String labelStr) {
        addLabelCell(row, labelStr);
    }

    public void addTechnologyChooserWithDefaultEntry(int row, String labelStr, String[] techs, String defTech, AbstractEnumParam techParam, AbstractBooleanParam checkboxParam, String checkboxLabel) {
        boolean techsPresent = techs.length > 0;
        if(!techsPresent) {
            return;
        }
        boolean defTechPresent = defTech != null;

        addTechnologyChooserEntry(row, labelStr, techs, techParam, defTech, !defTechPresent, checkboxParam, checkboxLabel, defTechPresent, defTechPresent);
    }

    public void addTechnologyChooserWithUnsetEntry(int row, String labelStr, String[] techs, AbstractEnumParam techParam, AbstractBooleanParam checkboxParam, String checkboxLabel) {
        boolean techsPresent = techs.length > 0;
        if(!techsPresent) {
            techs = new String[]{"No technology found"};
        }
        addTechnologyChooserEntry(row, labelStr, techs, techParam, null, techsPresent, checkboxParam, checkboxLabel, !techsPresent, techsPresent);
    }

    private void addTechnologyChooserEntry(int row, String labelStr, String[] technologies, AbstractEnumParam techParam, String defTech, boolean comboEnabled, AbstractBooleanParam checkboxParam, String checkboxLabel, boolean checkboxSelected, boolean checkboxEnabled) {
        addLabelCell(row, labelStr);
        JComboBox<String> combobox = addComboBoxCell(row, techParam, technologies, comboEnabled);
        addTechnologyCheckboxCell(row, checkboxLabel, checkboxParam, combobox, defTech, checkboxSelected, checkboxEnabled);
    }

    private JComboBox<String> addComboBoxCell(int row, AbstractEnumParam paramName, String[] values, boolean enabled) {
        JComboBox<String> combobox = new JComboBox<>(values);
        enumfields.put(paramName, combobox);

        GridBagConstraints gbc_combobox = new GridBagConstraints();
        gbc_combobox.anchor = GridBagConstraints.LINE_START;
        gbc_combobox.fill = GridBagConstraints.HORIZONTAL;
        gbc_combobox.insets = new Insets(0, 0, 5, 5);
        gbc_combobox.gridx = 1;
        gbc_combobox.gridy = row;
        this.add(combobox, gbc_combobox);

        combobox.setEnabled(enabled);

        return combobox;
    }

    private void addTechnologyCheckboxCell(int row, String label, AbstractBooleanParam paramName, final JComboBox<String> combobox, final String defTech, boolean selected, boolean enabled) {
        JCheckBox checkbox = new JCheckBox(label);
        buttons.put(paramName, checkbox);
        checkbox.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == ItemEvent.SELECTED) {
                    combobox.setEnabled(false);
                    if(defTech != null) {
                        combobox.setSelectedItem(defTech);
                    }
                } else if(e.getStateChange() == ItemEvent.DESELECTED) {
                    combobox.setEnabled(true);
                } else {
                }
            }
        });

        GridBagConstraints gbc_defaultcheckbox = new GridBagConstraints();
        gbc_defaultcheckbox.anchor = GridBagConstraints.LINE_START;
        gbc_defaultcheckbox.insets = new Insets(0, 0, 5, 0);
        gbc_defaultcheckbox.gridx = 3;
        gbc_defaultcheckbox.gridy = row;
        this.add(checkbox, gbc_defaultcheckbox);

        checkbox.setSelected(selected);
        checkbox.setEnabled(enabled);
    }

    public void addLabelCell(int row, String labelStr) {
        JLabel label = new JLabel(labelStr);
        GridBagConstraints gbc_label = new GridBagConstraints();
        gbc_label.insets = new Insets(0, 5, 5, 5);
        gbc_label.anchor = GridBagConstraints.LINE_START;
        gbc_label.gridx = 0;
        gbc_label.gridy = row;
        this.add(label, gbc_label);
    }

    public void addDefaultCheckboxCell(int row, final String defaultvalue, final JTextField textfield, final JButton pathbutton) {
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
                }
            }
        });

        GridBagConstraints gbc_defaultcheckbox = new GridBagConstraints();
        gbc_defaultcheckbox.anchor = GridBagConstraints.LINE_START;
        gbc_defaultcheckbox.insets = new Insets(0, 0, 5, 0);
        gbc_defaultcheckbox.gridx = 3;
        gbc_defaultcheckbox.gridy = row;
        this.add(defaultcheckbox, gbc_defaultcheckbox);
        defaultcheckbox.setSelected(true);
    }

    public JButton addPathButtonCell(int row, final Integer filemode, boolean hasdefaultcheckbox, final JTextField textfield) {
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
        gbc_pathbutton.anchor = GridBagConstraints.CENTER;
        gbc_pathbutton.insets = new Insets(0, 0, 5, 5);
        gbc_pathbutton.gridx = 2;
        gbc_pathbutton.gridy = row;
        this.add(pathbutton, gbc_pathbutton);
        if(hasdefaultcheckbox) {
            pathbutton.setEnabled(false);
        }
        return pathbutton;
    }

    public JTextField addTextfieldCell(int row, AbstractTextParam paramName, final String defaultvalue, boolean hasdefaultcheckbox) {
        final JTextField textfield = new JTextField();
        textfields.put(paramName, textfield);

        GridBagConstraints gbc_text = new GridBagConstraints();
        gbc_text.anchor = GridBagConstraints.LINE_START;
        gbc_text.fill = GridBagConstraints.HORIZONTAL;
        gbc_text.insets = new Insets(0, 0, 5, 5);
        gbc_text.gridx = 1;
        gbc_text.gridy = row;
        this.add(textfield, gbc_text);
        textfield.setColumns(10);
        textfield.setText(defaultvalue);
        if(hasdefaultcheckbox) {
            textfield.setEnabled(false);
        }
        return textfield;
    }

    public JSlider addSliderCell(int row, AbstractIntParam paramName, int minValue, int maxValue, int defaultValue) {
        final JSlider slider = new JSlider(minValue, maxValue, defaultValue);
        sliders.put(paramName, slider);

        GridBagConstraints gbc_slider = new GridBagConstraints();
        gbc_slider.anchor = GridBagConstraints.LINE_START;
        gbc_slider.fill = GridBagConstraints.HORIZONTAL;
        gbc_slider.insets = new Insets(0, 0, 5, 5);
        gbc_slider.gridx = 1;
        gbc_slider.gridy = row;

        Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
        labelTable.put(defaultValue, new JLabel(Integer.toString(defaultValue)));
        slider.setLabelTable(labelTable);
        slider.setSnapToTicks(true);
        slider.setPaintLabels(true);

        slider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent event) {
                Hashtable<Integer, JLabel> labelTable = new Hashtable<Integer, JLabel>();
                labelTable.put(slider.getValue(), new JLabel(Integer.toString(slider.getValue())));
                slider.setLabelTable(labelTable);
            }
        });

        this.add(slider, gbc_slider);
        return slider;
    }

    public void addSpinnerCell(int row, AbstractDoubleParam paramName, double step, double defaultValue) {
        SpinnerNumberModel model = new SpinnerNumberModel(defaultValue, Double.MIN_VALUE, Double.MAX_VALUE, step);
        JSpinner spinner = new JSpinner(model);
        spinners.put(paramName, spinner);

        GridBagConstraints gbc_spinner = new GridBagConstraints();
        gbc_spinner.anchor = GridBagConstraints.LINE_START;
        gbc_spinner.fill = GridBagConstraints.HORIZONTAL;
        gbc_spinner.insets = new Insets(0, 0, 5, 5);
        gbc_spinner.gridx = 1;
        gbc_spinner.gridy = row;
        spinner.setPreferredSize(new Dimension(300, 20));

        this.add(spinner, gbc_spinner);
    }

    public void addHelpButtonCell(int row, final String helptext) {
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
        this.add(helpbutton, gbc_helpbutton);
    }

    public Map<AbstractTextParam, JTextField> getTextfields() {
        return textfields;
    }

    public Map<AbstractBooleanParam, AbstractButton> getButtons() {
        return buttons;
    }

    public Map<AbstractEnumParam, JComboBox<String>> getEnumfields() {
        return enumfields;
    }

    public Map<AbstractIntParam, JSlider> getSliders() {
        return sliders;
    }

    public Map<AbstractDoubleParam, JSpinner> getSpinners() {
        return spinners;
    }
}
