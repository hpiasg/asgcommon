package de.uni_potsdam.hpi.asg.common.breeze.model.xml;

/*
 * Copyright (C) 2012 - 2021 Norman Kluge
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

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;

@XmlAccessorType(XmlAccessType.NONE)
public class Channel {
    public enum ChannelType {
        data_in, data_out, control_in, control_out, dataA_in, dataA_out, dataB_in, dataB_out, decision_in, decision_out, index_in, extension_out, arbA_in, arbB_in, arbA_out, arbB_out
    }

    public enum PortType {
        passive, active
    }

    @XmlAttribute(name = "id", required = true)
    private int         id;
    @XmlAttribute(name = "type", required = true)
    private ChannelType type;
    @XmlAttribute(name = "porttype", required = true)
    private PortType    porttype;

    public int getId() {
        return id;
    }

    public PortType getPorttype() {
        return porttype;
    }

    public ChannelType getType() {
        return type;
    }
}
