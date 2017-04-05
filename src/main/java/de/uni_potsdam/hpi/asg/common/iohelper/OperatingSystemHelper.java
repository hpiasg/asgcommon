package de.uni_potsdam.hpi.asg.common.iohelper;

import org.apache.commons.lang3.SystemUtils;

public class OperatingSystemHelper {

    private static final String WINDOWS_SCRIPT_FILE_EXTENSION = ".bat";
    private static final String UNIX_SCRIPT_FILE_EXTENSION    = "";

    public static String getScriptExtension() {
        if(SystemUtils.IS_OS_WINDOWS) {
            return WINDOWS_SCRIPT_FILE_EXTENSION;
        }
        return UNIX_SCRIPT_FILE_EXTENSION;
    }
}
