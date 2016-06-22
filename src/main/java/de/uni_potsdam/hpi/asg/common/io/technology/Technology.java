package de.uni_potsdam.hpi.asg.common.io.technology;

/*
 * Copyright (C) 2012 - 2016 Norman Kluge
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
import java.io.Serializable;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Technology implements Serializable {

    private static final long   serialVersionUID = 1867717371715445576L;
    private static final Logger logger           = LogManager.getLogger();

    //@formatter:off
    
    @XmlAttribute(name = "name")
    private String name;
    @XmlElement(name = "balsa")
    private Balsa  balsa;
    @XmlElement(name = "genlib")
    private Genlib genlib;
    @XmlElement(name = "synctool")
    private SyncTool synctool;
    
    private File folder;

    //@formatter:on

    public static Technology readIn(File file) {
        try {
            if(!file.exists()) {
                logger.error("Technologyfile not found");
                return null;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(Technology.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Technology retVal = (Technology)jaxbUnmarshaller.unmarshal(file);
            retVal.folder = file.getParentFile();
            return retVal;
        } catch(JAXBException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public Balsa getBalsa() {
        return balsa;
    }

    public String getGenLib() {
        return folder.getAbsolutePath() + File.separator + genlib.getLibfile();
    }

    public SyncTool getSynctool() {
        return synctool;
    }

    public String getName() {
        return name;
    }
}
