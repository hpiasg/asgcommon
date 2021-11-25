package de.uni_potsdam.hpi.asg.common.iohelper;

/*
 * Copyright (C) 2017 - 2021 Norman Kluge
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
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "statistics")
@XmlAccessorType(XmlAccessType.NONE)
public class Statistics {

    //@formatter:off
    @XmlElement(name = "remoteTime")
    private long remoteTime;
    //@formatter:on

    public long getRemoteTime() {
        return remoteTime;
    }

    public void setRemoteTime(long remoteTime) {
        this.remoteTime = remoteTime;
    }

}
