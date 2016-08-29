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
import java.util.LinkedList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.breeze.model.HSChannel.HSChannelConnection;
import de.uni_potsdam.hpi.asg.common.breeze.model.Signal.Direction;

public class BreezeNetlistInst extends ComponentInst {
    private static final Logger   logger = LogManager.getLogger();

    private AbstractBreezeNetlist instantiatedNetlist;
    private List<Signal>          signals;

    public BreezeNetlistInst(int id, AbstractBreezeNetlist netlist, AbstractBreezeNetlist instantiatedNetlist) {
        super(id, netlist);
        this.instantiatedNetlist = instantiatedNetlist;
        this.signals = new ArrayList<Signal>();
    }

    public static BreezeNetlistInst create(int id, LinkedList<Object> bechans, AbstractBreezeNetlist netlist, AbstractBreezeNetlist instantiatedNetlist) {
        int instantiatedId = 1;
        BreezeNetlistInst retVal = new BreezeNetlistInst(id, netlist, instantiatedNetlist);
        for(Object o : bechans) {
            if(o instanceof Integer) {
                int chanid = (Integer)o;
                HSChannel chan = instantiatedNetlist.getChan(instantiatedId);
                if(chan == null) {
                    logger.error("Could not find port " + instantiatedId + " in instantiated " + instantiatedNetlist.getName());
                    return null;
                }
                HSChannelConnection type = null;
                if(chan.getActive() instanceof PortComponent) {
                    type = HSChannelConnection.passive;
                } else if(chan.getPassive() instanceof PortComponent) {
                    type = HSChannelConnection.active;
                } else {
                    logger.error("Channel " + instantiatedId + " in " + instantiatedNetlist.getName() + " is no port, but should be");
                    return null;
                }
                HSChannel localchan = netlist.getChan(chanid, chan.getDatawidth(), chan.getDatatype(), retVal, type);
                if(localchan == null) {
                    return null;
                }
                boolean isdata = localchan.getDatawidth() == 0 ? false : true;
                if(isdata) {
                    switch(localchan.getDatatype()) {
                        case pull:
                            switch(type) {
                                case active:
                                    retVal.dataIn.add(localchan);
                                    retVal.signals.add(new Signal("r" + chanid, 0, Direction.out));
                                    retVal.signals.add(new Signal("a" + chanid, 0, Direction.in));
                                    retVal.signals.add(new Signal("d" + chanid, localchan.getDatawidth(), Direction.in));
                                    break;
                                case passive:
                                    retVal.dataOut.add(localchan);
                                    retVal.signals.add(new Signal("r" + chanid, 0, Direction.in));
                                    retVal.signals.add(new Signal("a" + chanid, 0, Direction.out));
                                    retVal.signals.add(new Signal("d" + chanid, localchan.getDatawidth(), Direction.out));
                                    break;
                            }
                            break;
                        case push:
                            switch(type) {
                                case active:
                                    retVal.dataOut.add(localchan);
                                    retVal.signals.add(new Signal("r" + chanid, 0, Direction.out));
                                    retVal.signals.add(new Signal("a" + chanid, 0, Direction.in));
                                    retVal.signals.add(new Signal("d" + chanid, localchan.getDatawidth(), Direction.out));
                                    break;
                                case passive:
                                    retVal.dataIn.add(localchan);
                                    retVal.signals.add(new Signal("r" + chanid, 0, Direction.in));
                                    retVal.signals.add(new Signal("a" + chanid, 0, Direction.out));
                                    retVal.signals.add(new Signal("d" + chanid, localchan.getDatawidth(), Direction.in));
                                    break;
                            }
                            break;
                    }
                } else {
                    switch(type) {
                        case active:
                            retVal.controlOut.add(localchan);
                            retVal.signals.add(new Signal("r" + chanid, 0, Direction.out));
                            retVal.signals.add(new Signal("a" + chanid, 0, Direction.in));
                            break;
                        case passive:
                            retVal.controlIn.add(localchan);
                            retVal.signals.add(new Signal("r" + chanid, 0, Direction.in));
                            retVal.signals.add(new Signal("a" + chanid, 0, Direction.out));
                            break;
                    }
                }
            } else {
                logger.error("Channellist of instantiated component " + instantiatedNetlist.getName() + " in " + netlist.getName() + " has at least one entry which is not a channel number (Maybe a list?). Thats not expected");
                return null;
            }
            instantiatedId++;
        }

        return retVal;
    }

    public AbstractBreezeNetlist getInstantiatedNetlist() {
        return instantiatedNetlist;
    }

    public List<Signal> getSignals() {
        return signals;
    }
}
