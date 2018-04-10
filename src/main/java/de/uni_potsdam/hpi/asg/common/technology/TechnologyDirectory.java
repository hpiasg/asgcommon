package de.uni_potsdam.hpi.asg.common.technology;

/*
 * Copyright (C) 2017 - 2018 Norman Kluge
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.Files;

import de.uni_potsdam.hpi.asg.common.iohelper.Zipper;
import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public class TechnologyDirectory {
    private static final Logger       logger = LogManager.getLogger();

    private File                      dir;
    private BiMap<String, Technology> techs;
    private File                      balsaTechDir;

    private TechnologyDirectory(File dir, BiMap<String, Technology> techs, File balsaTechDir) {
        this.dir = dir;
        this.techs = techs;
        this.balsaTechDir = balsaTechDir;
    }

    public static TechnologyDirectory createDefault() {
        return create(CommonConstants.DEF_TECH_DIR_FILE, CommonConstants.DEF_BALSA_TECH_DIR_FILE);
    }

    public static TechnologyDirectory create(File dir, File balsaTechDir) {
        if(!dir.exists()) {
            dir.mkdirs();
        }
        if(!dir.isDirectory()) {
            return null;
        }
        BiMap<String, Technology> techs = TechnologyDirectory.readTechnologies(dir);

        return new TechnologyDirectory(dir, techs, balsaTechDir);
    }

    private static BiMap<String, Technology> readTechnologies(File dir) {
        BiMap<String, Technology> techs = HashBiMap.create();
        for(File f : dir.listFiles()) {
            Technology t = Technology.readInSilent(f);
            if(t != null) {
                techs.put(t.getName(), t);
            }
        }
        return techs;
    }

    public Technology createTechnology(String name, File balsafolder, File genlibfile, String searchPaths, String libraries, List<String> postCompileCmds, List<String> verilogIncludes, String layouttcl, File libertyfile, File additionalInfoFile) {
        Balsa balsa = new Balsa(name);
        File targetdir = new File(balsaTechDir, name);
        targetdir.mkdirs();
        try {
            FileUtils.copyDirectory(balsafolder, targetdir);
        } catch(IOException e) {
            logger.error("Error while copying balsa technology directory");
            return null;
        }

        Genlib genlib = new Genlib(name + CommonConstants.GENLIB_FILE_EXTENSION);
        File targetfile = new File(dir, name + CommonConstants.GENLIB_FILE_EXTENSION);
        try {
            FileUtils.copyFile(genlibfile, targetfile);
        } catch(IOException e) {
            logger.error("Error while copying genlib file");
            return null;
        }

        SyncTool synctool = new SyncTool(searchPaths, libraries, postCompileCmds, verilogIncludes, layouttcl);

        String liberty = null;
        if(libertyfile != null) {
            liberty = name + CommonConstants.LIBERTY_FILE_EXTENSION;
            targetfile = new File(dir, liberty);
            try {
                FileUtils.copyFile(libertyfile, targetfile);
            } catch(IOException e) {
                logger.error("Error while copying liberty file");
                return null;
            }
        }

        String additionalInfo = null;
        if(additionalInfoFile != null) {
            additionalInfo = name + CommonConstants.ADDINFO_FILE_EXTENSION;
            targetfile = new File(dir, additionalInfo);
            try {
                FileUtils.copyFile(additionalInfoFile, targetfile);
            } catch(IOException e) {
                logger.error("Error while copying addInfo file");
                return null;
            }
        }

        Technology tech = new Technology(name, balsa, genlib, synctool, liberty, additionalInfo);
        if(!Technology.writeOut(tech, new File(dir, name + CommonConstants.XMLTECH_FILE_EXTENSION))) {
            logger.error("Error while creating technology file");
            return null;
        }
        this.techs.put(name, tech);

        return tech;
    }

    public Set<Technology> importTechnology(File file) {
        if(!file.exists()) {
            return null;
        }
        Set<Technology> retVal = null;
        if(file.isDirectory()) {
            retVal = importTechFromDir(file);
            return null;
        } else {
            retVal = importTechFromFile(file);
        }
        return retVal;
    }

    private Set<Technology> importTechFromFile(File file) {
        Set<Technology> retVal = new HashSet<>();

        // xml file
        Technology tech = Technology.readInSilent(file);
        if(tech != null) {
            Technology newTech = this.importTechnology(tech, file.getParentFile());
            if(newTech != null) {
                retVal.add(newTech);
                return retVal;
            }
        }

        // zip
        File tmpDir = Files.createTempDir();
        if(Zipper.getInstance().unzip(file, tmpDir)) {
            retVal = importTechFromDir(tmpDir);
            try {
                FileUtils.deleteDirectory(tmpDir);
            } catch(IOException e) {
            }
            return retVal;
        }

        return null;
    }

    private Set<Technology> importTechFromDir(File file) {
        TechnologyDirectory tmpTechDir = TechnologyDirectory.create(file, null);
        if(tmpTechDir == null) {
            return null;
        }

        Set<Technology> retVal = new HashSet<>();
        for(Technology srcTech : tmpTechDir.getTechs()) {
            Technology newTech = this.importTechnology(srcTech, file);
            if(newTech != null) {
                retVal.add(newTech);
            }
        }
        return retVal;
    }

    public Technology importTechnology(Technology srcTech, File srcDir) {
        String name = srcTech.getName();

        if(techs.containsKey(name)) {
            return null;
        }

        File balsaSourceFolder = new File(srcDir, srcTech.getBalsa().getTech());
        File balsafolder = balsaSourceFolder;

        File genlibfile = srcTech.getGenLib();

        String searchPaths = srcTech.getSynctool().getSearchPaths();
        String libraries = srcTech.getSynctool().getLibraries();
        List<String> postCompileCmds = srcTech.getSynctool().getPostCompileCmds();
        List<String> verilogIncludes = srcTech.getSynctool().getVerilogIncludes();
        String layouttcl = srcTech.getSynctool().getLayouttcl();

        File libertyfile = srcTech.getLibertyFile();
        File addInfoFile = srcTech.getAdditionalInfoFile();

        return createTechnology(name, balsafolder, genlibfile, searchPaths, libraries, postCompileCmds, verilogIncludes, layouttcl, libertyfile, addInfoFile);
    }

    public void exportTechnology(String name, File dstDir) {
        if(!techs.containsKey(name)) {
            return;
        }

        File tmpDir = Files.createTempDir();

        File balsadir = new File(balsaTechDir, name);
        File balsaDstDir = new File(tmpDir, name);
        try {
            FileUtils.copyDirectory(balsadir, balsaDstDir);
        } catch(IOException e) {
            logger.error("Failed to copy Balsa technology folder");
        }

        File genlibfile = new File(dir, name + CommonConstants.GENLIB_FILE_EXTENSION);
        File genlibDstFile = new File(tmpDir, name + CommonConstants.GENLIB_FILE_EXTENSION);
        try {
            FileUtils.copyFile(genlibfile, genlibDstFile);
        } catch(IOException e) {
            logger.error("Failed to copy Genlib file");
        }

        File techfile = new File(dir, name + CommonConstants.XMLTECH_FILE_EXTENSION);
        File techDstFile = new File(tmpDir, name + CommonConstants.XMLTECH_FILE_EXTENSION);
        try {
            FileUtils.copyFile(techfile, techDstFile);
        } catch(IOException e) {
            logger.error("Failed to copy technology file");
        }

        File libertyfile = new File(dir, name + CommonConstants.LIBERTY_FILE_EXTENSION);
        File libertyDstFile = new File(tmpDir, name + CommonConstants.LIBERTY_FILE_EXTENSION);
        try {
            FileUtils.copyFile(libertyfile, libertyDstFile);
        } catch(IOException e) {
            logger.error("Failed to copy liberty file");
        }

        File addInfofile = new File(dir, name + CommonConstants.ADDINFO_FILE_EXTENSION);
        File addInfoDstFile = new File(tmpDir, name + CommonConstants.ADDINFO_FILE_EXTENSION);
        try {
            FileUtils.copyFile(addInfofile, addInfoDstFile);
        } catch(IOException e) {
            logger.error("Failed to copy addInfo file");
        }

        File dstFile = new File(dstDir, name + CommonConstants.EXPORT_TECH_FILE_EXTENSION);
        if(!Zipper.getInstance().zip(dstFile, tmpDir)) {
            logger.error("Failed to create export file");
        }

        try {
            FileUtils.deleteDirectory(tmpDir);
        } catch(IOException e) {
        }
    }

    public void deleteTechnology(String name) {
        if(!techs.containsKey(name)) {
            return;
        }

        File balsadir = new File(balsaTechDir, name);
        try {
            FileUtils.deleteDirectory(balsadir);
        } catch(IOException e) {
            logger.error("Failed to remove Balsa technology folder");
        }

        File genlibfile = new File(dir, name + CommonConstants.GENLIB_FILE_EXTENSION);
        if(!genlibfile.delete()) {
            logger.error("Failed to remove Genlib file");
        }

        File techfile = new File(dir, name + CommonConstants.XMLTECH_FILE_EXTENSION);
        if(!techfile.delete()) {
            logger.error("Failed to remove technology file");
        }

        File libertyfile = new File(dir, name + CommonConstants.LIBERTY_FILE_EXTENSION);
        if(!libertyfile.delete()) {
            logger.error("Failed to remove liberty file");
        }

        File addInfoFile = new File(dir, name + CommonConstants.ADDINFO_FILE_EXTENSION);
        if(!addInfoFile.delete()) {
            logger.error("Failed to remove addInfo file");
        }

        techs.remove(name);
    }

    public String[] getTechNames() {
        List<String> techNames = new ArrayList<>();
        for(Technology t : techs.values()) {
            techNames.add(t.getName());
        }

        String[] retVal = new String[techNames.size()];
        return techNames.toArray(retVal);
    }

    public Set<Technology> getTechs() {
        return techs.values();
    }

    public File getBalsaTechDir() {
        return balsaTechDir;
    }

    public Technology getTechnology(String name) {
        return techs.get(name);
    }
}
