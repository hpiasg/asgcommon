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

public class BreezeComponentElement extends AbstractBreezeElement implements NamedBreezeElement {

    private static final long serialVersionUID  = -1344287802409223810L;
    private static int        component_counter = 0;

    boolean                   isDeclared        = true;
    int                       ID;
    String                    symbol;
    String                    name;

    public LinkedList<Object> parameters        = null;
    public LinkedList<Object> channels          = null;

    @SuppressWarnings("unchecked")
    public BreezeComponentElement(LinkedList<Object> value) {

        Iterator<Object> it = value.iterator();
        symbol = (String)it.next();
        if(!symbol.equals("component"))
            isDeclared = false;

        name = (String)it.next();

        parameters = (LinkedList<Object>)it.next();
        channels = (LinkedList<Object>)it.next();

        while(it.hasNext()) {
            Object cur = it.next();
            this.add(cur);
        }

        ID = component_counter++;
    }

    public int getID() {
        return ID;
    }

    @Override
    public String getName() {
        return name;
    }

    public void output() {

        System.out.print("\n    (" + symbol + " " + name + " ");
        output(parameters, 0, false, 0);
        indent(1);
        output(channels, 0, false, 0);

        if(isDeclared) {
            // output parameters and channels
            output(this, 0, true, 0);
            System.out.print(")");
        } else {
            output(this, 4, true, 3);
            System.out.print("\n    )");

        }

    }

    public static void resetComponent_counter() {
        BreezeComponentElement.component_counter = 0;
    }
}
