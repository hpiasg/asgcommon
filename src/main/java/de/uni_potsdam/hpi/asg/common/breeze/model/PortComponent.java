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

public class PortComponent extends ComponentInst {
    public enum Direction {
        in, out
    }

    private Direction direction;
    private String    name;

    public PortComponent(int id, String str, AbstractBreezeNetlist netlist) {
        super(id, netlist);
        name = str;
    }

    public void setDirection(Direction direction) {
        this.direction = direction;
    }

    public Direction getDirection() {
        return direction;
    }

    public String getName() {
        return name;
    }

    public void addInstControlIn(HSChannel instControlIn) {
        this.controlIn.add(instControlIn);
    }

    public void addInstControlOut(HSChannel instControlOut) {
        this.controlOut.add(instControlOut);
    }

    public void addInstDataIn(HSChannel instDataIn) {
        this.dataIn.add(instDataIn);
    }

    public void addInstDataOut(HSChannel instDataOut) {
        this.dataOut.add(instDataOut);
    }
}
