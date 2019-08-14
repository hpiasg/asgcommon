package de.uni_potsdam.hpi.asg.common.stg;

/*
 * Copyright (C) 2018 - 2019 Norman Kluge
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
import java.util.Map.Entry;
import java.util.Set;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import de.uni_potsdam.hpi.asg.common.stg.model.Place;
import de.uni_potsdam.hpi.asg.common.stg.model.STG;
import de.uni_potsdam.hpi.asg.common.stg.model.Signal;
import de.uni_potsdam.hpi.asg.common.stg.model.Transition;

public class STGCopy {

    private STG                           inStg;
    private STG                           outStg;

    // orig -> new
    private BiMap<Signal, Signal>         signalMap;
    private BiMap<Transition, Transition> transitionMap;
    private BiMap<Place, Place>           placeMap;

    public STGCopy(STG inStg) {
        this.inStg = inStg;
    }

    /**
     * 
     * @return a copy of the original STG (A single copy. Multiple calls will
     *         yield the same STG)
     */
    public STG getCopy() {
        if(outStg == null) {
            outStg = internalGetCopy();
        }
        return outStg;
    }

    private STG internalGetCopy() {
        STG outStg = new STG(inStg.getFile());

        // Signals
        signalMap = HashBiMap.create();
        for(Signal inSig : inStg.getSignals()) {
            outStg.addSignal(inSig.getName(), inSig.getType());
            Signal outSig = outStg.getSignal(inSig.getName());
            signalMap.put(inSig, outSig);
        }

        // Transitions
        transitionMap = HashBiMap.create();
        for(Transition inTrans : inStg.getTransitions()) {
            Signal outSig = signalMap.get(inTrans.getSignal());
            Transition outTrans = outStg.getTransitionOrAdd(outSig.getName(), inTrans.getEdge(), inTrans.getId());
            transitionMap.put(inTrans, outTrans);
        }

        // Places
        placeMap = HashBiMap.create();
        for(Entry<String, Place> inPlaceEntry : inStg.getPlaces().entrySet()) {
            Place inPlace = inPlaceEntry.getValue();
            Place outPlace = outStg.getPlaceOrAdd(inPlace.getId());
            for(Transition inTransPre : inPlace.getPreset()) {
                Transition outTransPre = transitionMap.get(inTransPre);
                outPlace.addPreTransition(outTransPre);
                outTransPre.addPostPlace(outPlace);
            }
            for(Transition inTransPost : inPlace.getPostset()) {
                Transition outTransPost = transitionMap.get(inTransPost);
                outPlace.addPostTransition(outTransPost);
                outTransPost.addPrePlace(outPlace);
            }
            placeMap.put(inPlace, outPlace);
        }

        // Init marking
        Set<Place> outInitMarking = new HashSet<>();
        for(Place inPlace : inStg.getInitMarking()) {
            Place outPlace = placeMap.get(inPlace);
            outInitMarking.add(outPlace);
        }
        outStg.setInitMarking(outInitMarking);

        return outStg;
    }

    public BiMap<Signal, Signal> getSignalMap() {
        return signalMap;
    }

    public BiMap<Transition, Transition> getTransitionMap() {
        return transitionMap;
    }

    public BiMap<Place, Place> getPlaceMap() {
        return placeMap;
    }
}
