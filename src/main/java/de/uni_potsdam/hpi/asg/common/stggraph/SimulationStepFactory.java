package de.uni_potsdam.hpi.asg.common.stggraph;

/*
 * Copyright (C) 2015 - 2016 Norman Kluge
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

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

public class SimulationStepFactory<T extends AbstractState<T>> extends BasePooledObjectFactory<SimulationStep<T>> {

    @Override
    public SimulationStep<T> create() throws Exception {
        return new SimulationStep<T>();
    }

    @Override
    public PooledObject<SimulationStep<T>> wrap(SimulationStep<T> obj) {
        return new DefaultPooledObject<SimulationStep<T>>(obj);
    }

    @Override
    public void passivateObject(PooledObject<SimulationStep<T>> p) throws Exception {
        p.getObject().getMarking().clear();
        p.getObject().setFireTrans(null);
        p.getObject().setState(null);
    }
}
