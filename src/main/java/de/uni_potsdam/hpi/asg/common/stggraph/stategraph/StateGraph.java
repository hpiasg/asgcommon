package de.uni_potsdam.hpi.asg.common.stggraph.stategraph;

/*
 * Copyright (C) 2015 - 2019 Norman Kluge
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

import java.util.Set;
import java.util.SortedSet;

import de.uni_potsdam.hpi.asg.common.stg.model.STG;
import de.uni_potsdam.hpi.asg.common.stg.model.Signal;

public class StateGraph {

    private State             initState;
    private Set<State>        states;
    private SortedSet<Signal> sortedSignals;
    private STG               stg;

    public StateGraph(STG stg, State initState, Set<State> states, SortedSet<Signal> sortedSignals) {
        this.initState = initState;
        this.states = states;
        this.sortedSignals = sortedSignals;
        this.stg = stg;
    }

    public State getInitState() {
        return initState;
    }

    public Set<State> getStates() {
        return states;
    }

    public SortedSet<Signal> getAllSignals() {
        return sortedSignals;
    }

    public STG getSTG() {
        return stg;
    }
}
