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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Parameter;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Parameter.ParameterType;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Parameters;

/**
 * Represents a specific type of a handshake component
 * ({@link AbstractHSComponent})
 * (e.g. a BrzVariable with 8bit width and 4 readers)
 * 
 */
public class HSComponentType {
    private static final Logger         logger = LogManager.getLogger();

    private Map<ParameterType, Object>  parammap;
    private Map<Integer, ParameterType> idmap;
    private AbstractHSComponent         comp;
    private LinkedList<Object>          beparams;

    private HSComponentType(AbstractHSComponent comp) {
        this.parammap = new HashMap<Parameter.ParameterType, Object>();
        this.idmap = new HashMap<Integer, Parameter.ParameterType>();
        this.comp = comp;
    }

    public static HSComponentType create(Parameters params, LinkedList<Object> beparams, AbstractHSComponent comp) {
        HSComponentType retVal = new HSComponentType(comp);
        retVal.beparams = beparams;

        int i = 0;
        if(params != null) {
            for(Object o : beparams) {
                Parameter param = params.getParameter(i);
                if(param != null) {
                    if(param.getType() != null) {
                        retVal.idmap.put(i, param.getType());
                        retVal.parammap.put(param.getType(), o);
                    } else {
                        logger.error("Type for parameter " + i + " in component " + comp.getBrzString() + " undefined");
                        return null;
                    }
                } else {
                    logger.error("param null");
                    return null;
                }
                i++;
            }
        }
        return retVal;
    }

    public Object getParamValue(ParameterType param) {
        return parammap.get(param);
    }

    public Object getParamValue(int id) {
        return parammap.get(idmap.get(id));
    }

    public AbstractHSComponent getComp() {
        return comp;
    }

    public LinkedList<Object> getBeparams() {
        return beparams;
    }
}
