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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.breeze.model.HSChannel.DataType;
import de.uni_potsdam.hpi.asg.common.breeze.model.HSChannel.HSChannelConnection;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Channel;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Channel.ChannelType;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Channel.PortType;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Channels;
import de.uni_potsdam.hpi.asg.common.breeze.model.xml.Parameter.ParameterType;

/**
 * Represents a handshake component instance of a specific type
 * ({@link HSComponentType})
 * (e.g. an instance of a [BrzVariable with 8bit width and 3 readers]
 * with the writing channel 1 and the reading channels 2, 3 and 4.
 * 
 */
public class HSComponentInst extends ComponentInst implements Comparable<HSComponentInst> {
    private static final Logger           logger = LogManager.getLogger();

    private HSComponentType               type;
    private AbstractHSComponent           comp;
    private Map<Integer, List<HSChannel>> chanmap;
    private LinkedList<Object>            bechans;

    private int                           newId;

    protected HSComponentInst(int id, AbstractBreezeNetlist netlist, HSComponentType type, AbstractHSComponent comp) {
        super(id, netlist);
        this.chanmap = new HashMap<Integer, List<HSChannel>>();
        this.type = type;
        this.comp = comp;
        this.newId = id;
    }

    public static HSComponentInst create(int id, LinkedList<Object> bechans, HSComponentType type, Channels chans, AbstractBreezeNetlist netlist, AbstractHSComponent comp) {
        HSComponentInst retVal = new HSComponentInst(id, netlist, type, comp);
        retVal.bechans = bechans;

        int i = 0;
        for(Object o : bechans) {
            Channel xmlchan = chans.getChannel(i);
            List<HSChannel> chanlist = new ArrayList<HSChannel>();
            retVal.chanmap.put(i, chanlist);
            if(o instanceof Integer) {
                HSChannel chan = retVal.addChannel(xmlchan, (Integer)o);
                if(chan == null) {
                    return null;
                }
                chanlist.add(chan);
            } else if(o instanceof LinkedList<?>) {
                if(type.getParamValue(ParameterType.var_spec) != null) {
                    List<HSChannel> list = retVal.parsevarspec((LinkedList<?>)o);
                    if(list == null) {
                        return null;
                    }
                    chanlist.addAll(list);
                } else {
                    for(Object o2 : (LinkedList<?>)o) {
                        HSChannel chan = retVal.addChannel(xmlchan, (Integer)o2);
                        if(chan == null) {
                            return null;
                        }
                        chanlist.add(chan);
                    }
                }
            }
            i++;
        }
        return retVal;
    }

    private List<HSChannel> parsevarspec(LinkedList<?> channels) {
        String spec = (String)type.getParamValue(ParameterType.var_spec);
        String[] specsplit = spec.replace("\"", "").split(";");
        List<HSChannel> retVal = new ArrayList<HSChannel>();
        for(int i = 0; i < channels.size(); i++) {
            int thiswidth = (Integer)type.getParamValue(ParameterType.input_width);
            if(i < specsplit.length) {
                if(!specsplit[i].isEmpty()) {
                    String[] specpart = specsplit[i].split("\\.\\.");
                    int end = Integer.parseInt(specpart[0]);
                    int start = Integer.parseInt(specpart[1]);
                    thiswidth = Math.abs(end - start) + 1;
                }
            }
            HSChannel chan = netlist.getChan((Integer)channels.get(i), thiswidth, DataType.pull, this, HSChannelConnection.passive);
            if(chan == null) {
                logger.error("Chan null");
                return null;
            }
            dataOut.add(chan);
            retVal.add(chan);
        }
        return retVal;
    }

    private HSChannel addChannel(Channel xmlchan, int id) {
        HSChannelConnection contype = xmlchan.getPorttype() == PortType.active ? HSChannelConnection.active : HSChannelConnection.passive;
        int width = getChanWidth(xmlchan.getType(), id);
        if(width == -1) {
            return null;
        }
        DataType datatype = getDataType(xmlchan);
        HSChannel chan = netlist.getChan(id, width, datatype, this, contype);
        if(chan == null) {
            logger.error("An error occured while evaluating channel " + id);
            return null;
        }
        switch(xmlchan.getType()) {
            case control_in:
                controlIn.add(chan);
                break;
            case control_out:
                controlOut.add(chan);
                break;
            case data_in:
            case dataA_in:
            case dataB_in:
            case decision_in:
            case index_in:
            case arbA_in:
            case arbB_in:
                dataIn.add(chan);
                break;
            case data_out:
            case decision_out:
            case arbA_out:
            case arbB_out:
            case dataA_out:
            case dataB_out:
            case extension_out:
                dataOut.add(chan);
                break;
            default:
                logger.error("Unknown Channel Type " + xmlchan.getType());
                return null;
        }
        return chan;
    }

    private DataType getDataType(Channel xmlchan) {
        switch(xmlchan.getType()) {
            case data_in:
            case dataA_in:
            case dataB_in:
            case index_in:
                if(xmlchan.getPorttype() == PortType.active) {
                    return DataType.pull;
                } else {
                    return DataType.push;
                }
            case dataA_out:
            case dataB_out:
            case data_out:
            case extension_out:
                if(xmlchan.getPorttype() == PortType.active) {
                    return DataType.push;
                } else {
                    return DataType.pull;
                }
            case arbA_in:
            case arbA_out:
            case arbB_in:
            case arbB_out:
            case control_in:
            case control_out:
            case decision_in:
            case decision_out:
                // These are control only - no datatype
            default:
                return null;
        }
    }

    private int getChanWidth(ChannelType channelType, int id) {
        int width = 0;
        switch(channelType) {
            case control_in:
            case control_out:
            case decision_in:
            case decision_out:
            case arbA_in:
            case arbA_out:
            case arbB_in:
            case arbB_out:
                width = 0;
                break;
            case data_in: {
                Object data = type.getParamValue(ParameterType.input_width);
                Object data2 = type.getParamValue(ParameterType.width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else if(data2 instanceof Integer) {
                    width = (Integer)data2;
                } else {
                    width = 1;
                    logger.warn("Data(In) width for channel " + id + " unknown (guessing 1)");
                }
                break;
            }
            case data_out: {
                Object data = type.getParamValue(ParameterType.output_width);
                Object data2 = type.getParamValue(ParameterType.width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else if(data2 instanceof Integer) {
                    width = (Integer)data2;
                } else {
                    width = 1;
                    logger.warn("Data(Out) width for channel " + id + " unknown (guessing 1)");
                }
                break;
            }
            case dataA_in: {
                Object data = type.getParamValue(ParameterType.inputA_width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(InA) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }
            case dataB_in: {
                Object data = type.getParamValue(ParameterType.inputB_width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(InB) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }
            case dataA_out: {
                Object data = type.getParamValue(ParameterType.outputA_width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(OutA) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }
            case dataB_out: {
                Object data = type.getParamValue(ParameterType.outputB_width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(OutB) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }

            case index_in: {
                Object data = type.getParamValue(ParameterType.index_width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(Index) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }
            case extension_out: {
                Object data = type.getParamValue(ParameterType.width);
                if(data instanceof Integer) {
                    width = (Integer)data;
                } else {
                    logger.error("Data(Extension) width for channel " + id + " unknown");
                    return -1;
                }
                break;
            }

            default:
                logger.error("Unkown Chan Type");
                return -1;
        }
        return width;
    }

    @Override
    public int compareTo(HSComponentInst o) {
        return Integer.valueOf(this.id).compareTo(o.getId());
    }

    public List<HSChannel> getChan(int id) {
        return chanmap.get(id);
    }

    public AbstractHSComponent getComp() {
        return comp;
    }

    public HSComponentType getType() {
        return type;
    }

    public String getBrzStr() {
        return comp.getBrzString();
    }

    public LinkedList<Object> getBechans() {
        return bechans;
    }

    public int getNewId() {
        return newId;
    }

    public void setNewId(int newId) {
        this.newId = newId;
    }
}
