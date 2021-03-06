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

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

public class BreezePartElement extends AbstractBreezeElement implements NamedBreezeElement {
    private static final long  serialVersionUID = -2932159559337348649L;
    String                     name;

    LinkedList<Object>         ports            = new LinkedList<Object>();
    LinkedList<Object>         attributes       = new LinkedList<Object>();

    BreezeChannelListElement   channels         = null;
    BreezeComponentListElement components       = null;

    public LinkedList<Object> getPorts() {
        return ports;
    }

    public BreezeComponentListElement getComponentList() {
        return components;
    }

    LinkedList<Object> call_contexts = new LinkedList<Object>();

    @SuppressWarnings("unchecked")
    public BreezePartElement(LinkedList<Object> list) {
        Iterator<Object> it = list.iterator();
        it.next();

        this.name = (String)it.next();
        if(name.startsWith("\"")) {
            this.name = this.name.split("\"")[1];
        }

        while(it.hasNext()) {
            Object cur = it.next();
            String symb = BreezeElementFactory.symbolOf(cur);

            if(symb.equals("ports"))
                ports.addAll((Collection<? extends Object>)cur);

            else if(symb.equals("attributes"))
                attributes.addAll((Collection<? extends Object>)cur);

            else if(symb.equals("channels")) {

                channels = new BreezeChannelListElement((LinkedList<Object>)cur);

            } else if(symb.equals("components")) {
                components = new BreezeComponentListElement((LinkedList<Object>)cur);
            }
            //else if (symb.equals("call-contexts"))	call_contexts.addAll((Collection<? extends Object>) cur);
            else
                this.add(cur);
        }
    }

    public String getName() {
        return name;
    }

    @Override
    public void output() {
        System.out.printf("(breeze-part \"" + name + "\"");
        super.output(ports, 2, false, 2);

        super.output(attributes, 2, false, 1);

        channels.output();

        components.output();
        //super.output(call_contexts, 2, false, 2);
        super.output(this, 0, true, 3);

        System.out.printf("\n)");
    }

}
