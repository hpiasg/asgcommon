package de.uni_potsdam.hpi.asg.common.invoker.config;

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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "toolsconfig")
@XmlAccessorType(XmlAccessType.NONE)
public class ExternalToolsConfig {
    //@formatter:off
    @XmlElementWrapper(name = "tools")
    @XmlElement(name = "tool")
    private List<ToolConfig> tools;
    @XmlElementWrapper(name = "remotes")
    @XmlElement(name = "remote")
    private List<RemoteConfig> remoteconfigs;
    //@formatter:on

    private Map<String, ToolConfig> toolsMap;

    public ExternalToolsConfig(List<ToolConfig> tools, List<RemoteConfig> remoteconfigs) {
        this.tools = tools;
        this.remoteconfigs = remoteconfigs;
    }

    protected ExternalToolsConfig() {
    }

    public ToolConfig getToolConfig(String name) {
        if(toolsMap == null) {
            initToolsMap();
        }
        return toolsMap.get(name);
    }

    private void initToolsMap() {
        toolsMap = new HashMap<>();
        for(ToolConfig cfg : tools) {
            toolsMap.put(cfg.getName(), cfg);
        }
    }
}
