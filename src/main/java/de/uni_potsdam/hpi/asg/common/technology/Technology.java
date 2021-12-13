package de.uni_potsdam.hpi.asg.common.technology;

/*
 * Copyright (C) 2012 - 2021 Norman Kluge
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
import java.io.FileWriter;
import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;
import jakarta.xml.bind.Unmarshaller;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAttribute;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXParseException;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class Technology implements Serializable {

    private static final long   serialVersionUID = 1867717371715445576L;
    private static final Logger logger           = LogManager.getLogger();

    //@formatter:off
    
    @XmlAttribute(name = "name")
    private String name;
    @XmlElement(name = "balsa")
    private Balsa balsa;
    @XmlElement(name = "genlib")
    private Genlib genlib;
    @XmlElement(name = "liberty")
    private String liberty;
    @XmlElement(name = "addInfo")
    private String additionalInfo;
    @XmlElement(name = "synctool")
    private SyncTool synctool;
    
    private File folder;

    //@formatter:on

    protected Technology() {
    }

    public Technology(String name, Balsa balsa, Genlib genlib, SyncTool synctool, String liberty, String additionalInfo) {
        this.name = name;
        this.balsa = balsa;
        this.genlib = genlib;
        this.synctool = synctool;
        this.liberty = liberty;
        this.additionalInfo = additionalInfo;
    }

    public static Technology readInSilent(File file) {
        return readInInternal(file, false);
    }

    public static Technology readIn(File file) {
        return readInInternal(file, true);
    }

    private static Technology readInInternal(File file, boolean verbose) {
        try {
            if(!file.exists()) {
                if(verbose) {
                    logger.error("Technologyfile " + file.getAbsolutePath() + " not found");
                }
                return null;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(Technology.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();
            Technology retVal = (Technology)jaxbUnmarshaller.unmarshal(file);
            retVal.folder = file.getParentFile();
            return retVal;
        } catch(JAXBException e) {
            if(verbose) {
                if(e.getLinkedException() instanceof SAXParseException) {
                    SAXParseException e2 = (SAXParseException)e.getLinkedException();
                    logger.error("File: " + file.getAbsolutePath() + ", Line: " + e2.getLineNumber() + ", Col: " + e2.getColumnNumber());
                    logger.error(e2.getLocalizedMessage());
                    return null;
                } else {
                    logger.error(e.getLocalizedMessage());
                }
            }
            return null;
        }
    }

    public static boolean writeOut(Technology tech, File file) {
        try {
            Writer fw = new FileWriter(file);
            JAXBContext context = JAXBContext.newInstance(Technology.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(tech, fw);
            return true;
        } catch(JAXBException e) {
            System.out.println(e.getLocalizedMessage());
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(IOException e) {
            System.out.println(e.getLocalizedMessage());
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }

    public Balsa getBalsa() {
        return balsa;
    }

    public File getGenLib() {
        return new File(folder, genlib.getLibfile());
    }

    public SyncTool getSynctool() {
        return synctool;
    }

    public String getName() {
        return name;
    }

    public File getAdditionalInfoFile() {
        return new File(folder, additionalInfo);
    }

    public File getLibertyFile() {
        return new File(folder, liberty);
    }
}
