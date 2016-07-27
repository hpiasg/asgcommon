package de.uni_potsdam.hpi.asg.common.io;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOStreamReader implements Runnable {
    private final static Logger logger = LogManager.getLogger();

    private Process             p;
    private String              result;
    private boolean             debug;

    public IOStreamReader(Process p) {
        this.p = p;
        this.debug = false;
    }

    public IOStreamReader(Process p, boolean debug) {
        this.p = p;
        this.debug = debug;
    }

    @Override
    public void run() {
        result = getOutAndErrStream();
    }

    private String getOutAndErrStream() {
        StringBuffer cmd_out = new StringBuffer("");
        if(p != null) {
            BufferedReader is = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String buf = "";
            try {
                while((buf = is.readLine()) != null) {
                    cmd_out.append(buf);
                    if(debug) {
                        logger.debug(buf);
                    }
                    cmd_out.append(System.getProperty("line.separator"));
                }
                is.close();
                is = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                while((buf = is.readLine()) != null) {
                    cmd_out.append(buf);
                    cmd_out.append("\n");
                }
                is.close();
            } catch(IOException e) {
                ;
            } catch(Exception e) {
                logger.error(e.getLocalizedMessage());
                return null;
            }
        }
        if(cmd_out.length() > 0) {
            cmd_out = cmd_out.deleteCharAt(cmd_out.length() - 1);
        }
        return cmd_out.toString();
    }

    public String getResult() {
        return result;
    }

}
