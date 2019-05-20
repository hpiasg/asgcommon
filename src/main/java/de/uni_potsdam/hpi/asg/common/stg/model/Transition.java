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

public class Transition implements Comparable<Transition> {

    public enum Edge {
        rising, falling
    }

    private int        transitionId; // unique per signal and edge
    private int        globalId;     // unique in stg: used if transition gets dummified
    private boolean    isDummy;
    private Edge       edge;
    private Signal     signal;

    private Set<Place> postset;
    private Set<Place> preset;

    public Transition(int transitionId, int globalId, Signal signal, Edge edge) {
        this.transitionId = transitionId;
        this.globalId = globalId;
        this.signal = signal;
        this.edge = edge;
        this.preset = new HashSet<Place>(1);
        this.postset = new HashSet<Place>(1);
        this.isDummy = false;
    }

    public Edge getEdge() {
        return edge;
    }

    public Signal getSignal() {
        return signal;
    }

    public Set<Place> getPostset() {
        return postset;
    }

    public Set<Place> getPreset() {
        return preset;
    }

    public int getId() {
        return transitionId;
    }

    public void addPostPlace(Place post) {
        this.postset.add(post);
    }

    public void addPrePlace(Place pre) {
        this.preset.add(pre);
    }

    @Override
    public String toString() {
        if(isDummy) {
            return "dum" + globalId + "(" + signal.toString() + ((transitionId != 0) ? "/" + transitionId : "") + ")";
        }
        return signal.toString() + ((edge == Edge.falling) ? "-" : "+") + ((transitionId != 0) ? "/" + transitionId : "");
    }

    @Override
    public int compareTo(Transition o) {
        int cmpSigName = this.signal.getName().compareTo(o.getSignal().getName());
        if(cmpSigName == 0) {
            if(this.edge == Edge.falling && o.edge == Edge.rising) {
                return -1;
            } else if(this.edge == Edge.rising && o.edge == Edge.falling) {
                return 1;
            } else {
                return Integer.compare(this.transitionId, o.transitionId);
            }
        } else {
            return cmpSigName;
        }
    }

//    public void setId(int id) {
//        this.transitionId = id;
//    }

    public void dummify() {
        this.isDummy = true;
    }

    public boolean isDummy() {
        return isDummy;
    }

    public int getGlobalId() {
        return globalId;
    }
}
