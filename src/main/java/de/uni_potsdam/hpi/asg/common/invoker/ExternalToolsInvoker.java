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
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfig;
import de.uni_potsdam.hpi.asg.common.invoker.config.ExternalToolsConfigFile;
import de.uni_potsdam.hpi.asg.common.invoker.config.ToolConfig;
import de.uni_potsdam.hpi.asg.common.invoker.local.LocalInvoker;
import de.uni_potsdam.hpi.asg.common.invoker.remote.RemoteInvoker;
import de.uni_potsdam.hpi.asg.common.iohelper.FileHelper;
import de.uni_potsdam.hpi.asg.common.iohelper.WorkingdirGenerator;

public abstract class ExternalToolsInvoker {
    private final static Logger        logger = LogManager.getLogger();

    private static ExternalToolsConfig config;
    private static boolean             tooldebug;

    private String                     cmdType;

    //overwrite with setters if needed
    private File                       workingDir;                       // default: WorkingDirGenerator value
    private int                        timeout;                          // default: 0 (=off)
    private boolean                    removeRemoteDir;                  // default: true

    private Set<File>                  inputFilesToCopy;
    private Map<String, File>          outputFilesToExport;
    private Set<String>                outputFilesToCopyStartsWith;
    private Set<String>                outputFilesDownloadOnlyStartsWith;

    public static boolean init(File configFile, boolean tooldebug) {
        if(configFile == null) {
            logger.error("No tools config file");
            return false;
        }
        config = ExternalToolsConfigFile.readIn(configFile);
        if(config == null) {
            return false;
        }

        return true;
    }

    protected ExternalToolsInvoker(String cmdType) {
        this.cmdType = cmdType;
        this.workingDir = WorkingdirGenerator.getInstance().getWorkingDir();
        this.timeout = 0;
        this.removeRemoteDir = true;
        inputFilesToCopy = new HashSet<>();
        outputFilesToExport = new HashMap<>();
        outputFilesToCopyStartsWith = new HashSet<>();
        outputFilesDownloadOnlyStartsWith = new HashSet<>();
    }

    protected void addInputFilesToCopy(File... args) {
        for(File f : args) {
            inputFilesToCopy.add(f);
        }
    }

    protected void addOutputFileToExport(String name, File file) {
        outputFilesToExport.put(name, file);
    }

    protected void addOutputFilesToExport(File... args) {
        for(File f : args) {
            outputFilesToExport.put(f.getName(), f);
        }
    }

    protected void addOutputFilesToCopyStartsWith(String... args) {
        for(String s : args) {
            outputFilesToCopyStartsWith.add(s);
        }
    }

    protected void addOutputFilesDownloadOnlyStartsWith(String... args) {
        for(String s : args) {
            outputFilesDownloadOnlyStartsWith.add(s);
        }
    }

    protected InvokeReturn run(List<String> params, String subDir) {
        return run(params, subDir, null);
    }

    protected InvokeReturn run(List<String> params, String subDir, AbstractScriptGenerator generator) {
        if(config == null) {
            logger.error("No toolconfig. Run init");
            return null;
        }

        ToolConfig cfg = config.getToolConfig(cmdType);
        if(cfg == null) {
            logger.error("Config for tool '" + cmdType + "' not found");
            return null;
        }

        // create (local) directory
        File localWorkingDir = createLocalTempDirectory(subDir + "_");
        if(localWorkingDir == null) {
            return null;
        }

        List<File> additionalUploadFiles = null;
        if(generator != null) {
            if(!generator.generate(localWorkingDir)) {
                return null;
            }
            additionalUploadFiles = new ArrayList<>();
            additionalUploadFiles.addAll(generator.getGeneratedFiles());
            outputFilesDownloadOnlyStartsWith.addAll(generator.getDownloadIncludeFileNames());
        }

        if(cfg.getRemoteconfig() == null) {
            //local
            return runLocal(params, cfg, localWorkingDir);
        } else {
            //remote
            return runRemote(params, cfg, localWorkingDir, subDir, additionalUploadFiles);
        }
    }

    protected boolean errorHandling(InvokeReturn ret) {
        return errorHandling(ret, Arrays.asList(0));
    }

    protected boolean errorHandling(InvokeReturn ret, List<Integer> okCodes) {
        if(ret != null) {
            switch(ret.getStatus()) {
                case ok:
                    if(!okCodes.contains(ret.getExitCode())) {
                        logger.error("An error was reported while executing " + ret.getCmdline());
                        logger.debug("Exit code: " + ret.getExitCode() + " Output:");
                        logger.debug("##########");
                        logger.debug(ret.getOutput());
                        logger.debug("##########");
                        return false;
                    }
                    break;
                case timeout:
                    logger.error("Timeout while executing " + ret.getCmdline());
                    return false;
                case ioexception:
                case noio:
                    logger.error("I/O error while executing " + ret.getCmdline());
                    return false;
            }
        } else {
            logger.error("Something went really wrong while executing something. I don't even know what the command line was");
            return false;
        }
        return true;
    }

    private InvokeReturn runRemote(List<String> params, ToolConfig cfg, File localWorkingDir, String subDir, List<File> additionalUploadFiles) {
        // build command
        List<String> cmdline = new ArrayList<>();
        cmdline.addAll(Arrays.asList(cfg.getCmdline().split(" ")));
        cmdline.addAll(params);

        // copy inputs
        if(!copyInputFiles(localWorkingDir, cfg)) {
            return null;
        }

        // create data structures for RemoteWorkflow
        Set<File> uploadFiles = new HashSet<>();
        uploadFiles.addAll(inputFilesToCopy);
        if(additionalUploadFiles != null) {
            uploadFiles.addAll(additionalUploadFiles);
        }
        Set<String> downloadIncludeFileStarts = new HashSet<>();
        downloadIncludeFileStarts.addAll(outputFilesDownloadOnlyStartsWith);
        downloadIncludeFileStarts.addAll(outputFilesToCopyStartsWith);
        downloadIncludeFileStarts.addAll(outputFilesToExport.keySet());

        // invoke
        RemoteInvoker inv = new RemoteInvoker(cfg.getRemoteconfig(), subDir, workingDir, removeRemoteDir, timeout);
        InvokeReturn ret = inv.invoke(uploadFiles, cmdline, downloadIncludeFileStarts);
        if(ret == null) {
            return null;
        }

        // copy outputs
        if(!copyOutputFiles(localWorkingDir, cfg)) {
            return null;
        }

        return ret;
    }

    private InvokeReturn runLocal(List<String> params, ToolConfig cfg, File localWorkingDir) {
        // build command
        List<String> cmdline = new ArrayList<>();
        cmdline.addAll(LocalInvoker.convertCmd(cfg.getCmdline()));
        cmdline.addAll(params);

        // copy inputs
        if(!copyInputFiles(localWorkingDir, cfg)) {
            return null;
        }

        // invoke
        LocalInvoker inv = new LocalInvoker(localWorkingDir, timeout, tooldebug);
        InvokeReturn ret = inv.invoke(cmdline);
        if(ret == null) {
            return null;
        }

        // copy outputs
        if(!copyOutputFiles(localWorkingDir, cfg)) {
            return null;
        }

        return ret;
    }

    private boolean copyOutputFiles(File localWorkingDir, ToolConfig cfg) {
        String[] outputFilesToCopyStartsWithArray = new String[outputFilesToCopyStartsWith.size()];
        outputFilesToCopyStartsWithArray = outputFilesToCopyStartsWith.toArray(outputFilesToCopyStartsWithArray);
        File[] dirFiles = localWorkingDir.listFiles();
        for(File f : dirFiles) {
            if(StringUtils.startsWithAny(f.getName(), outputFilesToCopyStartsWithArray)) {
                if(!FileHelper.getInstance().copyfile(f, new File(workingDir, f.getName()))) {
                    logger.error("Failed to copy output file '" + f.getName() + "' for " + cfg.getName());
                    return false;
                }
            }
            if(outputFilesToExport.containsKey(f.getName())) {
                if(!FileHelper.getInstance().copyfile(f, outputFilesToExport.get(f.getName()))) {
                    logger.error("Failed to copy output file '" + f.getName() + "' for " + cfg.getName());
                    return false;
                }
            }
        }
        return true;
    }

    private boolean copyInputFiles(File localWorkingDir, ToolConfig cfg) {
        for(File f : inputFilesToCopy) {
            if(!FileHelper.getInstance().copyfile(f, new File(localWorkingDir, f.getName()))) {
                logger.error("Failed to copy input file '" + f.getName() + "' for " + cfg.getName());
                return false;
            }
        }
        return true;
    }

    private File createLocalTempDirectory(String subDir) {
        File localWorkingDir = null;
        try {
            localWorkingDir = Files.createTempDirectory(workingDir.toPath(), subDir).toFile();
        } catch(IOException e) {
            logger.error("Failed to create Temp Directory");
            return null;
        }
        return localWorkingDir;
    }

    protected void setWorkingDir(File workingDir) {
        this.workingDir = workingDir;
    }

    protected void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    protected void setRemoveRemoteDir(boolean removeRemoteDir) {
        this.removeRemoteDir = removeRemoteDir;
    }
}
