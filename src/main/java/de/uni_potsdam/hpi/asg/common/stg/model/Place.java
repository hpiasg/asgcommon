package de.uni_potsdam.hpi.asg.common.stg.model;

/*
 * Copyright (C) 2014 - 2019 Norman Kluge
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

import java.util.HashSet;
import java.util.Set;

public class Place {

    private String          id;
    private Set<Transition> preset;
    private Set<Transition> postset;

    public Place(String id) {
        this.id = id;
        this.preset = new HashSet<Transition>();
        this.postset = new HashSet<Transition>();
    }

    public String getId() {
        return id;
    }

    public void addPostTransition(Transition post) {
        this.postset.add(post);
    }

    public void addPreTransition(Transition pre) {
        this.preset.add(pre);
    }

    public Set<Transition> getPostset() {
        return postset;
    }

    public Set<Transition> getPreset() {
        return preset;
    }

    @Override
    public String toString() {
        if(id.startsWith("tmp")) {
            return "<" + this.preset.iterator().next() + "," + this.postset.iterator().next() + ">";
        }
        return id;
    }

    public boolean isMarkedGraphPlace() {
        return preset.size() == 1 && postset.size() == 1;
    }
}
