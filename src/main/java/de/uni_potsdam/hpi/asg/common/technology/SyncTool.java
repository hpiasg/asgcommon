package de.uni_potsdam.hpi.asg.common.technology;

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

import java.io.Serializable;

import java.util.List;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.NONE)
public class SyncTool implements Serializable {

    private static final long serialVersionUID = 4564277794397798986L;

    //@formatter:off
    @XmlElement(name = "searchpaths")
    private String searchPaths;
    @XmlElement(name = "libraries")
    private String libraries;
    @XmlElement(name = "postcompilecmd")
    private List<String> postCompileCmds;
    @XmlElement(name = "veriloginclude")
    private List<String> verilogIncludes;
    @XmlElement(name = "layouttcl")
    private String layouttcl;
    //@formatter:on

    protected SyncTool() {
    }

    public SyncTool(String searchPaths, String libraries, List<String> postCompileCmds, List<String> verilogIncludes, String layouttcl) {
        this.searchPaths = searchPaths;
        this.libraries = libraries;
        this.postCompileCmds = postCompileCmds;
        this.verilogIncludes = verilogIncludes;
        this.layouttcl = layouttcl;
    }

    public String getLibraries() {
        return libraries;
    }

    public String getSearchPaths() {
        return searchPaths;
    }

    public List<String> getPostCompileCmds() {
        return postCompileCmds;
    }

    public List<String> getVerilogIncludes() {
        return verilogIncludes;
    }

    public String getLayouttcl() {
        return layouttcl;
    }
}
