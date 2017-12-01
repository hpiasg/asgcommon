package de.uni_potsdam.hpi.asg.common.invoker.config;

/*
 * Copyright (C) 2017 Norman Kluge
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
import java.io.IOException;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.SchemaOutputResolver;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;

public class ExternalToolsConfigSchemaGenerator {

    private static final File  baseDir        = new File("./src/main/resources");
    public static final String schemaFileName = "common_toolconfig.xsd";

    public static void main(String[] args) {

        class MySchemaOutputResolver extends SchemaOutputResolver {
            public Result createOutput(String namespaceUri, String suggestedFileName) throws IOException {
                return new StreamResult(new File(baseDir, schemaFileName));
            }
        }

        if(!baseDir.exists()) {
            baseDir.mkdirs();
        }

        JAXBContext context;
        try {
            context = JAXBContext.newInstance(ExternalToolsConfig.class);
            context.generateSchema(new MySchemaOutputResolver());
        } catch(JAXBException e) {
            e.printStackTrace();
        } catch(IOException e) {
            e.printStackTrace();
        }
        System.out.println("done");
    }
}
