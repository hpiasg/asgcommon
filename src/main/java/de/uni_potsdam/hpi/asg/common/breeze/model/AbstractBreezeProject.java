package de.uni_potsdam.hpi.asg.common.breeze.model;

/*
 * Copyright (C) 2015 Norman Kluge
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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public abstract class AbstractBreezeProject {

    protected Map<String, AbstractBreezeNetlist> netlists;
    private Set<String>                          alreadyChecked;
    protected Map<String, AbstractHSComponent>   componentList;
    private Set<AbstractBreezeNetlist>           sortedNetlists;

    protected AbstractBreezeProject() {
        alreadyChecked = new HashSet<String>();
        netlists = new HashMap<String, AbstractBreezeNetlist>();
        componentList = new HashMap<String, AbstractHSComponent>();
    }

    public Set<String> getAlreadyChecked() {
        return alreadyChecked;
    }

    public Map<String, AbstractBreezeNetlist> getNetlists() {
        return netlists;
    }

    public Map<String, AbstractHSComponent> getComponentList() {
        return componentList;
    }

    public Set<HSComponentType> getAllHSTypes() {
        Set<HSComponentType> retVal = new HashSet<HSComponentType>();
        for(AbstractHSComponent comp : componentList.values()) {
            retVal.addAll(comp.getTypes());
        }
        return retVal;
    }

    public Set<AbstractBreezeNetlist> getSortedNetlists() {
        if(sortedNetlists == null || netlists.size() > sortedNetlists.size()) {
            BreezeNetlistHierachieOrderer order = new BreezeNetlistHierachieOrderer(netlists.values());
            sortedNetlists = order.order();
        }
        return sortedNetlists;
    }
}
