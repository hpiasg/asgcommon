package de.uni_potsdam.hpi.asg.common.stggraph;

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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.stg.model.Place;
import de.uni_potsdam.hpi.asg.common.stg.model.Signal;
import de.uni_potsdam.hpi.asg.common.stg.model.Transition;

public abstract class AbstractState<T extends AbstractState<T>> {
    private static final Logger logger = LogManager.getLogger();
    private static int          sid    = 0;

    public enum Value {
        low("0", 0), high("1", 2), rising("+", 1), falling("-", 3);

        private String str;
        private int    val;

        Value(String str_n, int val_n) {
            str = str_n;
            val = val_n;
        }

        @Override
        public String toString() {
            return str;
        }

        public int compare(Value v2) {
            return Integer.compare(val, v2.val);
        }

        public Value normalise() {
            switch(this) {
                case falling:
                    return Value.high;
                case high:
                    return Value.high;
                case low:
                    return Value.low;
                case rising:
                    return Value.low;
                default:
                    return null;
            }
        }
    }

    protected Set<T>             prevStates;
    protected Map<Transition, T> nextStates;
    protected int                id;
    protected Set<Set<Place>>    markings;

    public AbstractState() {
        this.nextStates = new HashMap<Transition, T>();
        this.prevStates = new HashSet<T>();
        this.id = sid++;
        this.markings = new HashSet<>();
    }

    public void addMarking(Set<Place> marking) {
        boolean found = false;
        for(Set<Place> entry : markings) {
            if(entry.containsAll(marking) && marking.containsAll(entry)) {
                //skip - sets are identical
                found = true;
                break;
            }
        }
        if(!found) {
            Set<Place> newMarking = new HashSet<>();
            for(Place p : marking) {
                newMarking.add(p);
            }
            markings.add(newMarking);
        }
    }

    public abstract void setSignalState(Signal sig, Value val);

    public abstract Map<Signal, Value> getStateValues();

    public abstract boolean isSignalSet(Signal sig);

    public Set<T> getPrevStates() {
        return prevStates;
    }

    public Map<Transition, T> getNextStates() {
        return nextStates;
    }

    public int getId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public void addEdgeNextState(T state, Transition t) {
        if(this.nextStates.containsValue(state)) {
            Transition t1 = null;
            for(Entry<Transition, T> entry : nextStates.entrySet()) {
                if(entry.getValue() == state) {
                    t1 = entry.getKey();
                    break;
                }
            }
            logger.warn("Doubled edge: S" + id + " => S" + state.id + ", Transitions: Old: " + t1 + ", New: " + t);
        }
        this.nextStates.put(t, state);
        state.prevStates.add((T)this);
    }

    public Set<Set<Place>> getMarkings() {
        return markings;
    }
}
