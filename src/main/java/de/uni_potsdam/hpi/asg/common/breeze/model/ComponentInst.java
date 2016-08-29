package de.uni_potsdam.hpi.asg.common.breeze.model;

/*
 * Copyright (C) 2015 Norman Kluge
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
import java.util.List;

public abstract class ComponentInst {

    protected int                   id;
    protected List<HSChannel>       dataIn;
    protected List<HSChannel>       dataOut;
    protected List<HSChannel>       controlIn;
    protected List<HSChannel>       controlOut;

    protected AbstractBreezeNetlist netlist;

    protected ComponentInst(int id, AbstractBreezeNetlist netlist) {
        this.id = id;
        this.netlist = netlist;
        this.dataIn = new ArrayList<HSChannel>();
        this.dataOut = new ArrayList<HSChannel>();
        this.controlIn = new ArrayList<HSChannel>();
        this.controlOut = new ArrayList<HSChannel>();
    }

    public int getId() {
        return id;
    }

    public List<HSChannel> getControlIn() {
        return controlIn;
    }

    public List<HSChannel> getControlOut() {
        return controlOut;
    }

    public List<HSChannel> getDataIn() {
        return dataIn;
    }

    public List<HSChannel> getDataOut() {
        return dataOut;
    }

}
