package de.uni_potsdam.hpi.asg.common.invoker;

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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;
import de.uni_potsdam.hpi.asg.common.misc.CommonConstants;

public abstract class AbstractScriptGenerator {
    private static final Logger              logger               = LogManager.getLogger();

    private static final Pattern             templateBeginPattern = Pattern.compile("#\\+([a-z_]+)_begin\\+#");
    private static final Pattern             templateEndPattern   = Pattern.compile("#\\+([a-z_]+)_end\\+#");

    private static Map<String, List<String>> templates;

    private Set<File>                        uploadFiles;
    private String                           execFileName;
    private Set<String>                      downloadIncludeFileNames;

    public AbstractScriptGenerator() {
        this.uploadFiles = new HashSet<>();
        this.downloadIncludeFileNames = new HashSet<>();
    }

    public abstract boolean generate(File targetDir);

    public static boolean readTemplateFiles(String templatesStartString) {
        if(templates == null) {
            templates = new HashMap<String, List<String>>();
        }
        for(File f : CommonConstants.DEF_TEMPLATE_DIR_FILE.listFiles()) {
            if(f.isDirectory()) {
                continue;
            }
            if(!f.getName().startsWith(templatesStartString)) {
                continue;
            }
            if(!readTemplateFile(f)) {
                return false;
            }
        }
        return true;
    }

    private static boolean readTemplateFile(File templatefile) {
        List<String> current = null;
        List<String> lines = FileHelper.getInstance().readFile(templatefile);
        Matcher m = null;
        for(String line : lines) {
            m = templateBeginPattern.matcher(line);
            if(m.matches()) {
                if(current != null) {
                    logger.error("New template found before old template has ended");
                    return false;
                }
                String templateName = m.group(1);
                if(templates.containsKey(templateName)) {
                    logger.error("Templatename already registered: " + templateName);
                    return false;
                }
                current = new ArrayList<>();
                templates.put(templateName, current);
                continue;
            }
            m = templateEndPattern.matcher(line);
            if(m.matches()) {
                if(current == null) {
                    logger.error("No template to end");
                    return false;
                }
                current = null;
                continue;
            }
            // normal line
            if(current != null) {
                current.add(line);
            }
        }

        return true;
    }

    protected void addUploadFiles(File... args) {
        for(File f : args) {
            uploadFiles.add(f);
        }
    }

    protected void setExecFileName(String execFileName) {
        this.execFileName = execFileName;
    }

    protected void addDownloadIncludeFileNames(String... args) {
        for(String str : args) {
            downloadIncludeFileNames.add(str);
        }
    }

    protected List<String> replaceInTemplates(String[] templateNames, Map<String, String> replacements) {
        List<String> code = new ArrayList<>();
        for(String templateName : templateNames) {
            List<String> codepart = replaceInTemplate(templateName, replacements);
            if(codepart == null) {
                return null;
            }
            code.addAll(codepart);
        }
        return code;
    }

    protected List<String> replaceInTemplate(String templateName, Map<String, String> replacements) {
        List<String> templateCode = templates.get(templateName);
        if(templateCode == null) {
            logger.error("Template code for '" + templateName + "' not found");
            return null;
        }
        List<String> code = new ArrayList<>();
        for(String line : templateCode) {
            for(Entry<String, String> entry : replacements.entrySet()) {
                line = line.replace("#*" + entry.getKey() + "*#", entry.getValue());
            }
            code.add(line);
        }
        return code;
    }

    protected boolean replaceInTemplatesAndWriteOut(String[] templateNames, Map<String, String> replacements, File outFile) {
        List<String> code = replaceInTemplates(templateNames, replacements);
        if(code == null) {
            return false;
        }

        if(!FileHelper.getInstance().writeFile(outFile, code)) {
            return false;
        }
        return true;
    }

    protected boolean replaceInTemplateAndWriteOut(String templateName, Map<String, String> replacements, File outFile) {
        List<String> code = replaceInTemplate(templateName, replacements);
        if(code == null) {
            return false;
        }

        if(!FileHelper.getInstance().writeFile(outFile, code)) {
            return false;
        }
        return true;
    }

    public Set<File> getUploadFiles() {
        return uploadFiles;
    }

    public Set<String> getDownloadIncludeFileNames() {
        return downloadIncludeFileNames;
    }

    public String getExecFileName() {
        return execFileName;
    }
}
