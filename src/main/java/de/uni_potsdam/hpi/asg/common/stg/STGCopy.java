package de.uni_potsdam.hpi.asg.common.stg;

/*
 * Copyright (C) 2018 Norman Kluge
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import de.uni_potsdam.hpi.asg.common.stg.model.Place;
import de.uni_potsdam.hpi.asg.common.stg.model.STG;
import de.uni_potsdam.hpi.asg.common.stg.model.Signal;
import de.uni_potsdam.hpi.asg.common.stg.model.Transition;

public class STGCopy {

    public static STG getCopy(STG inStg) {
        STG outStg = new STG(inStg.getFile());

        // Signals
        Map<Signal, Signal> signalMap = new HashMap<>();
        for(Signal inSig : inStg.getSignals()) {
            outStg.addSignal(inSig.getName(), inSig.getType());
            Signal outSig = outStg.getSignal(inSig.getName());
            signalMap.put(inSig, outSig);
        }

        // Transitions
        Map<Transition, Transition> transitionMap = new HashMap<>();
        for(Transition inTrans : inStg.getTransitions()) {
            Signal outSig = signalMap.get(inTrans.getSignal());
            Transition outTrans = outStg.getTransitionOrAdd(outSig.getName(), inTrans.getEdge(), inTrans.getId());
            transitionMap.put(inTrans, outTrans);
        }

        // Places
        Map<Place, Place> placeMap = new HashMap<>();
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
        List<Place> outInitMarking = new ArrayList<>();
        for(Place inPlace : inStg.getInitMarking()) {
            Place outPlace = placeMap.get(inPlace);
            outInitMarking.add(outPlace);
        }
        outStg.setInitMarking(outInitMarking);

        return outStg;
    }
}
