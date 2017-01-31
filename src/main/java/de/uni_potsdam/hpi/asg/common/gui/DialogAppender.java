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

import java.io.Serializable;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin(name = "DialogAppender", category = "Core", elementType = "appender", printObject = true)
public class DialogAppender extends AbstractAppender {
    private static final long serialVersionUID = 3262097781348137174L;

    protected DialogAppender(String name, Filter filter, Layout<? extends Serializable> layout) {
        super(name, filter, layout);
    }

    @Override
    public void append(LogEvent event) {
        int msgType = 0;
        String msgTitle = null;
        if(event.getLevel().compareTo(Level.INFO) == 0) {
            msgType = JOptionPane.INFORMATION_MESSAGE;
            msgTitle = "Information";
        } else if(event.getLevel().compareTo(Level.WARN) == 0) {
            msgType = JOptionPane.WARNING_MESSAGE;
            msgTitle = "Warning";
        } else if(event.getLevel().compareTo(Level.ERROR) == 0) {
            msgType = JOptionPane.ERROR_MESSAGE;
            msgTitle = "Error";
        }

        String msg = event.getMessage().getFormattedMessage();

        JOptionPane.showMessageDialog(null, msg, msgTitle, msgType);
    }

    @PluginFactory
    public static DialogAppender createAppender(
        //@formatter:off
        @PluginAttribute("name") String name,
        @PluginElement("Layout") Layout<? extends Serializable> layout,
        @PluginElement("Filter") final Filter filter) {
        //@formatter:on
        if(name == null) {
            return null;
        }
        if(layout == null) {
            layout = PatternLayout.createDefaultLayout();
        }
        return new DialogAppender(name, filter, layout);
    }
}
