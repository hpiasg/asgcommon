package de.uni_potsdam.hpi.asg.common.invoker.local;

/*
 * Copyright (C) 2012 - 2018 Norman Kluge
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
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class IOStreamReader implements Runnable {
    private final static Logger logger = LogManager.getLogger();

    private Process             p;
    private String              outResult;
    private String              errResult;
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
        if(p != null) {
            StringBuffer outStream = new StringBuffer();
            StringBuffer errStream = new StringBuffer();
            Thread tout = new Thread(new InnerReader(p.getInputStream(), outStream));
            Thread terr = new Thread(new InnerReader(p.getErrorStream(), errStream));
            tout.start();
            terr.start();
            try {
                tout.join();
                terr.join();
            } catch(InterruptedException e) {
                return;
            }
            if(outStream.length() > 0) {
                outStream = outStream.deleteCharAt(outStream.length() - 1);
            }
            outResult = outStream.toString();
            if(errStream.length() > 0) {
                errStream = errStream.deleteCharAt(errStream.length() - 1);
            }
            errResult = errStream.toString();
        }
    }

    private class InnerReader implements Runnable {

        private InputStream  stream;
        private StringBuffer out;

        public InnerReader(InputStream stream, StringBuffer out) {
            this.stream = stream;
            this.out = out;
        }

        @Override
        public void run() {
            BufferedReader is = new BufferedReader(new InputStreamReader(stream));
            String buf = "";
            try {
                while((buf = is.readLine()) != null) {
                    out.append(buf);
                    out.append(System.getProperty("line.separator"));
                    if(debug) {
                        logger.debug(buf);
                    }
                }
                is.close();
            } catch(IOException e) {
                ;
            } catch(Exception e) {
                logger.error(e.getLocalizedMessage());
                return;
            }
            return;
        }
    }

    public String getErrResult() {
        return errResult;
    }

    public String getOutResult() {
        return outResult;
    }
}
