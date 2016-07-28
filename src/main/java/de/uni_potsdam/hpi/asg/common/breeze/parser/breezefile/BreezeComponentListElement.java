package de.uni_potsdam.hpi.asg.common.breeze.parser.breezefile;

/*
 * Copyright (C) 2012 - 2014 Stanislavs Golubcovs
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

import java.util.Iterator;
import java.util.LinkedList;
import java.util.TreeMap;

public class BreezeComponentListElement extends AbstractBreezeElement {

    private static final long                serialVersionUID = 7202441788468576382L;
    TreeMap<Integer, BreezeComponentElement> components       = new TreeMap<Integer, BreezeComponentElement>();

    public TreeMap<Integer, BreezeComponentElement> getComponents() {
        return components;
    }

    @SuppressWarnings("unchecked")
    public BreezeComponentListElement(LinkedList<Object> list) {
        Iterator<Object> it = list.iterator();
        it.next();

        int number = 0; // starting with 0

        while(it.hasNext()) {
            LinkedList<Object> cur = (LinkedList<Object>)it.next();
            components.put(number++, new BreezeComponentElement(cur));

        }

    }

    public void output() {
        System.out.print("\n  (components");

        for(java.util.Map.Entry<Integer, BreezeComponentElement> be : components.entrySet()) {
            be.getValue().output();
            System.out.print(" ; " + be.getKey());
        }
        System.out.print("\n  )");

    }

}
