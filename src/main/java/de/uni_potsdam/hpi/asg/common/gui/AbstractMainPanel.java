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

import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractButton;
import javax.swing.JComboBox;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.JTextField;

import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractBooleanParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractDoubleParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractEnumParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractIntParam;
import de.uni_potsdam.hpi.asg.common.gui.PropertiesPanel.AbstractTextParam;

public abstract class AbstractMainPanel extends JPanel {
    private static final long                           serialVersionUID = 3803172863281872813L;

    protected Map<AbstractTextParam, JTextField>        textfields;
    protected Map<AbstractBooleanParam, AbstractButton> buttons;
    protected Map<AbstractEnumParam, JComboBox<String>> enumfields;
    protected Map<AbstractIntParam, JSlider>            sliders;
    protected Map<AbstractDoubleParam, JSpinner>        spinners;

    public AbstractMainPanel() {
        textfields = new HashMap<>();
        buttons = new HashMap<>();
        enumfields = new HashMap<>();
        sliders = new HashMap<>();
        spinners = new HashMap<>();
    }

    protected void getDataFromPanel(PropertiesPanel panel) {
        this.textfields.putAll(panel.getTextfields());
        this.buttons.putAll(panel.getButtons());
        this.enumfields.putAll(panel.getEnumfields());
        this.sliders.putAll(panel.getSliders());
        this.spinners.putAll(panel.getSpinners());
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

    public int getIntValue(AbstractIntParam param) {
        return sliders.get(param).getValue();
    }

    public double getDoubleValue(AbstractDoubleParam param) {
        return (Double)spinners.get(param).getValue();
    }
}
