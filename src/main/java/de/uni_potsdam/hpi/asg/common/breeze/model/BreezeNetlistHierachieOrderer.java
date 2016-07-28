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

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

public class BreezeNetlistHierachieOrderer {

    private Queue<AbstractBreezeNetlist> todo;

    public BreezeNetlistHierachieOrderer(Collection<AbstractBreezeNetlist> netlists) {
        this.todo = new LinkedList<AbstractBreezeNetlist>(netlists);
    }

    public Set<AbstractBreezeNetlist> order() {
        Set<AbstractBreezeNetlist> retVal = new LinkedHashSet<AbstractBreezeNetlist>();
        while(!todo.isEmpty()) {
            AbstractBreezeNetlist list = todo.poll();
            boolean isleaf = true;
            for(BreezeNetlistInst inst : list.getSubBreezeInst()) {
                if(todo.contains(inst.getInstantiatedNetlist())) {
                    isleaf = false;
                    break;
                }
            }
            if(isleaf) {
                retVal.add(list);
            } else {
                todo.add(list);
            }
        }
        return retVal;
    }
}
