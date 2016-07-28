package de.uni_potsdam.hpi.asg.common.breeze.model.xml;

/*
 * Copyright (C) 2012 - 2015 Norman Kluge
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Component {
    @XmlAttribute(name = "breezename", required = true)
    String             breezename;
    @XmlAttribute(name = "symbol")
    String             symbol;

    @XmlElement(name = "parameters")
    private Parameters parameters;
    @XmlElement(name = "channels")
    private Channels   channels;

    public String getBreezename() {
        return breezename;
    }

    public Channels getChannels() {
        return channels;
    }

    public Parameters getParameters() {
        return parameters;
    }

    public String getSymbol() {
        return symbol;
    }
}
