package de.uni_potsdam.hpi.asg.common.invoker.config;

/*
 * Copyright (C) 2016 - 2021 Norman Kluge
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlID;

@XmlAccessorType(XmlAccessType.NONE)
public class RemoteConfig {
    //@formatter:off
    @XmlID
    @XmlAttribute(name = "id", required = true)
    private String id;    
    @XmlElement(required = true)
    private String hostname;
    @XmlElement(required = true)
    private int port;
    @XmlElement(required = true)
    private String username;
    @XmlElement(required = true)
    private String password;
    @XmlElement(required = false)
    private String workingdir;
    //@formatter:on

    public RemoteConfig(String id, String hostname, int port, String username, String password, String workingdir) {
        this.id = id;
        this.hostname = hostname;
        this.port = port;
        this.username = username;
        this.password = password;
        this.workingdir = workingdir;
    }

    protected RemoteConfig() {
    }

    public String getHostname() {
        return hostname;
    }

    public int getPort() {
        return port;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getWorkingDir() {
        return workingdir;
    }
}
