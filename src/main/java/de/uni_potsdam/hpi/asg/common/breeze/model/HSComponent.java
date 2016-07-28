package de.uni_potsdam.hpi.asg.common.breeze.model;

/*
 * Copyright (C) 2012 - 2015 Norman Kluge
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

import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Component;
import de.uni_potsdam.hpi.asg.common.breeze.parser.breezefile.BreezeComponentElement;

/**
 * See {@link AbstractHSComponent}
 * 
 */
public class HSComponent extends AbstractHSComponent {

    public HSComponent(Component comp) {
        super(comp);
    }

    @Override
    public boolean createInstance(BreezeComponentElement be, AbstractBreezeNetlist netlist) {
        HSComponentType type = internalCreateInstanceType(be);
        if(type == null) {
            return false;
        }
        HSComponentInst inst = internalCreateInstanceInst(be, netlist, type);
        if(inst == null) {
            return false;
        }
        return true;
    }
}
