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

public class HSChannel {
    public enum DataType {
        pull, push
    }

    private int           id;
    private ComponentInst active;
    private ComponentInst passive;
    private int           datawidth;
    private DataType      datatype;

    public HSChannel(int id, int datawidth, DataType datatype) {
        this.datawidth = datawidth;
        this.id = id;
        this.datatype = datatype;
    }

    public boolean setActive(ComponentInst active) {
        if(this.active == null) {
            this.active = active;
            return true;
        }
        return false;
    }

    public boolean setPassive(ComponentInst passive) {
        if(this.passive == null) {
            this.passive = passive;
            return true;
        }
        return false;
    }

    public ComponentInst getActive() {
        return active;
    }

    public ComponentInst getPassive() {
        return passive;
    }

    public int getDatawidth() {
        return datawidth;
    }

    public enum HSChannelConnection {
        active, passive
    }

    public int getId() {
        return id;
    }

    public DataType getDatatype() {
        return datatype;
    }
}
