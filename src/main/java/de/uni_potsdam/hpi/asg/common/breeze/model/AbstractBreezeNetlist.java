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
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.TreeSet;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.breeze.model.HSChannel.DataType;
import de.uni_potsdam.hpi.asg.common.breeze.model.HSChannel.HSChannelConnection;
import de.uni_potsdam.hpi.asg.common.breeze.model.PortComponent.Direction;
import de.uni_potsdam.hpi.asg.common.breeze.parser.breezefile.BreezeComponentElement;
import de.uni_potsdam.hpi.asg.common.breeze.parser.breezefile.BreezePartElement;
import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;
import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper.Filetype;

public abstract class AbstractBreezeNetlist {
    private static final Logger      logger = LogManager.getLogger();

    private String                   name;
    private Set<String>              tempports;
    private Set<PortComponent>       ports;
    private Map<Integer, HSChannel>  channelList;
    protected AbstractBreezeProject  project;
    protected Set<BreezeNetlistInst> subBreezeInst;

    protected AbstractBreezeNetlist(String name, AbstractBreezeProject project) {
        this.name = name;
        this.channelList = new HashMap<Integer, HSChannel>();
        this.tempports = new LinkedHashSet<String>();
        this.project = project;
        this.subBreezeInst = new HashSet<BreezeNetlistInst>();
    }

    @SuppressWarnings("unchecked")
    protected boolean parseBreeze(BreezePartElement part, boolean skipUndefinedComponents) {
        TreeMap<Integer, BreezeComponentElement> tm = part.getComponentList().getComponents();
        for(Entry<Integer, BreezeComponentElement> e : tm.entrySet()) {
            BreezeComponentElement be = e.getValue();
            String compname = be.getName().replaceAll("\"", "").replace("$", "");
            if(!project.getAlreadyChecked().contains(compname)) {
                if(project.getNetlists().containsKey(compname)) { // is already known sub procedure?
                    this.subBreezeInst.add(BreezeNetlistInst.create(be.getID(), be.channels, this, project.getNetlists().get(compname)));
                    continue;
                } else if(this.createInstance(compname, be)) { // is HS component instance?
                    continue;
                } else {
                    if(!skipUndefinedComponents) {
                        logger.error("Could not find defition for component " + compname);
                        return false;
                    } else {
                        logger.warn("Could not find defition for component " + compname);
                        project.getAlreadyChecked().add(compname);
                    }
                }
            }
        }

        for(Object o : part.getPorts()) {
            if(o instanceof LinkedList) {
                LinkedList<Object> ol = (LinkedList<Object>)o;
                String str = ((String)ol.get(1)).replace("\"", "");
                tempports.add(str);
            }
        }

        return true;
    }

    private boolean createInstance(String name, BreezeComponentElement be) {
        AbstractHSComponent comp = project.getComponentList().get(name);
        if(comp != null) {
            return comp.createInstance(be, this);
        }
        return false;
    }

    public SortedSet<HSComponentInst> getAllHSInstances() {
        SortedSet<HSComponentInst> retVal = new TreeSet<HSComponentInst>();
        for(AbstractHSComponent comp : project.getComponentList().values()) {
            retVal.addAll(comp.getInstances(this));
        }
        return retVal;
    }

    public Set<PortComponent> getAllPorts() {
        return ports;
    }

    public Map<Integer, HSChannel> getChannelList() {
        return channelList;
    }

    public HSChannel getChan(int id, int width, DataType datatype, ComponentInst inst, HSChannelConnection type) {
        HSChannel chan = channelList.containsKey(id) ? channelList.get(id) : new HSChannel(id, width, datatype);
        switch(type) {
            case active:
                if(!chan.setActive(inst)) {
                    return null;
                }
                break;
            case passive:
                if(!chan.setPassive(inst)) {
                    return null;
                }
                break;
        }
        channelList.put(id, chan);
        return chan;
    }

    public HSChannel getChan(int id) {
        return channelList.containsKey(id) ? channelList.get(id) : null;
    }

    protected boolean connectPorts(boolean skipUndefinedComponents) {
        ports = new HashSet<PortComponent>();
        int i = 1;
        for(String str : tempports) {
            HSChannel chan = getChan(i);
            PortComponent comp = new PortComponent(-i, str, this);
            ports.add(comp);
            if(chan != null) {
                if(chan.getActive() == null) {
                    chan.setActive(comp);
                    if(chan.getDatawidth() != 0) {
                        if(chan.getDatatype() == DataType.push) {
                            comp.setDirection(Direction.in);
                            comp.addInstDataOut(chan);
                        } else {
                            comp.setDirection(Direction.out);
                            comp.addInstDataIn(chan);
                        }
                    } else {
                        comp.setDirection(Direction.in);
                        comp.addInstControlOut(chan);
                    }
                } else if(chan.getPassive() == null) {
                    chan.setPassive(comp);
                    if(chan.getDatawidth() != 0) {
                        if(chan.getDatatype() == DataType.push) {
                            comp.setDirection(Direction.out);
                            comp.addInstDataIn(chan);
                        } else {
                            comp.setDirection(Direction.in);
                            comp.addInstDataOut(chan);
                        }
                    } else {
                        comp.setDirection(Direction.out);
                        comp.addInstControlIn(chan);
                    }
                } else {
                    logger.error("Channel " + i + " has no open connection for interface");
                    return false;
                }
            } else {
                if(!skipUndefinedComponents) {
                    logger.error("Channel " + i + " which should be connected to port " + str + " not found");
                    return false;
                } else {
                    logger.warn("Channel " + i + " which should be connected to port " + str + " not found");
                }
            }
            i++;
        }
        return true;
    }

    public boolean writeOut() {
        String filename = name + FileHelper.getFileEx(Filetype.breeze);
        String newline = FileHelper.getNewline();
        StringBuilder newbreeze = new StringBuilder();
        newbreeze.append("(breeze-part \"Test\"" + newline);
        newbreeze.append("(components" + newline);
        int breezeOutId = 0;
        for(HSComponentInst inst : getAllHSInstances()) {
            //(component "$BrzSequence" (2 "S") (355 (356 357)) (at 77 35 "EX.balsa" 0)) ; 252
            newbreeze.append("(component ");
            newbreeze.append("\"$" + inst.getBrzStr() + "\" ");
            newbreeze.append(inst.getType().getBeparams().toString().replace("[", "(").replace("]", ")").replace(",", "") + " ");
            newbreeze.append(inst.getBechans().toString().replace("[", "(").replace("]", ")").replace(",", "") + " ");
            newbreeze.append("())");
            newbreeze.append(newline);
            inst.setNewId(breezeOutId);
            breezeOutId++;
        }
        newbreeze.append("))");
        return FileHelper.getInstance().writeFile(filename, newbreeze.toString());
    }

    public String getName() {
        return name;
    }

    public Set<BreezeNetlistInst> getSubBreezeInst() {
        return subBreezeInst;
    }
}
