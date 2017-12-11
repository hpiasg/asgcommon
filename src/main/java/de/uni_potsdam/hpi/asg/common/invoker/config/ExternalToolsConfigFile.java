package de.uni_potsdam.hpi.asg.common.invoker.config;

/*
 * Copyright (C) 2012 - 2017 Norman Kluge
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
import java.io.Writer;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class ExternalToolsConfigFile {
    private static final Logger logger = LogManager.getLogger();

    public static ExternalToolsConfig readIn(File file) {
        try {
            if(!file.exists()) {
                logger.error("External tools config file not found");
                return null;
            }
            JAXBContext jaxbContext = JAXBContext.newInstance(ExternalToolsConfig.class);
            Unmarshaller jaxbUnmarshaller = jaxbContext.createUnmarshaller();

            System.setProperty("javax.xml.accessExternalDTD", "all");
            SchemaFactory sf = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = sf.newSchema(new StreamSource(ExternalToolsConfig.class.getResourceAsStream("/" + ExternalToolsConfigSchemaGenerator.schemaFileName)));
            jaxbUnmarshaller.setSchema(schema);

            return (ExternalToolsConfig)jaxbUnmarshaller.unmarshal(file);
        } catch(JAXBException e) {
            if(e.getLinkedException() instanceof SAXParseException) {
                SAXParseException e2 = (SAXParseException)e.getLinkedException();
                logger.error("File: " + file.getAbsolutePath() + ", Line: " + e2.getLineNumber() + ", Col: " + e2.getColumnNumber());
                logger.error(e2.getLocalizedMessage());
                return null;
            }
            logger.error(e.getLocalizedMessage());
            return null;
        } catch(SAXException e) {
            logger.error(e.getLocalizedMessage());
            return null;
        }
    }

    public static boolean writeOut(ExternalToolsConfig cfg, String filename) {
        try {
            Writer fw = new FileWriter(filename);
            JAXBContext context = JAXBContext.newInstance(ExternalToolsConfig.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
            m.marshal(cfg, fw);
            return true;
        } catch(JAXBException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        } catch(IOException e) {
            logger.error(e.getLocalizedMessage());
            return false;
        }
    }
}
