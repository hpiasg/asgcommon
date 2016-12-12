package de.uni_potsdam.hpi.asg.common.technology;

/*
 * Copyright (C) 2012 - 2014 Norman Kluge
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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class Balsa implements Serializable {
    private static final long serialVersionUID = -7729551903207056828L;

    @XmlElement(name = "style")
    private String            style;
    @XmlElement(name = "tech")
    private String            tech;

    @Override
    @Deprecated
    public String toString() {
        return tech + "/" + style;
    }

    public String getStyle() {
        return style;
    }

    public String getTech() {
        return tech;
    }
}
