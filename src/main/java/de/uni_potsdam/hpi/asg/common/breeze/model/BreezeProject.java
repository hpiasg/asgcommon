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

import java.io.File;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Component;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Components;

public class BreezeProject extends AbstractBreezeProject {
    private static final Logger logger = LogManager.getLogger();

    public static BreezeProject create(File rootfile, String componentconfig, boolean skipUndefinedComponents, boolean skipSubComponents) {
        BreezeProject retVal = new BreezeProject();
        if(!retVal.readComponentsList(componentconfig)) {
            return null;
        }
        if(!BreezeNetlist.create(rootfile, skipUndefinedComponents, skipSubComponents, retVal)) {
            logger.error("Could not create Breeze netlist for " + rootfile);
            return null;
        }
        return retVal;
    }

    private boolean readComponentsList(String componentconfig) {
        Components components = Components.readIn(componentconfig);
        if(components != null) {
            for(Component comp : components.getComponents()) {
                componentList.put(comp.getBreezename(), new HSComponent(comp));
            }
            return true;
        }
        logger.error("Componentlist not found");
        return false;
    }
}
