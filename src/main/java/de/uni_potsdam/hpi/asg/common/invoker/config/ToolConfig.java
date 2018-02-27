package de.uni_potsdam.hpi.asg.common.invoker.config;

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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlIDREF;

@XmlAccessorType(XmlAccessType.NONE)
public class ToolConfig {
    //@formatter:off
    @XmlAttribute(name = "name", required = true)
    private String name;
    @XmlElement(name = "cmdline", required = true)
    private String cmdline;
    @XmlIDREF
    @XmlElement(name = "remote", required = false)
    private RemoteConfig remoteconfig;
    //@formatter:on

    public ToolConfig(String name, String cmdline, RemoteConfig remoteConfig) {
        this.name = name;
        this.cmdline = cmdline;
        this.remoteconfig = remoteConfig;
    }

    protected ToolConfig() {
    }

    public String getName() {
        return name;
    }

    public String getCmdline() {
        return cmdline;
    }

    public RemoteConfig getRemoteconfig() {
        return remoteconfig;
    }
}
