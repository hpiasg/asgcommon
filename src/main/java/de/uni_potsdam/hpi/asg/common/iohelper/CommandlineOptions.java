package de.uni_potsdam.hpi.asg.common.iohelper;

/*
 * Copyright (C) 2015 Norman Kluge
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

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.kohsuke.args4j.CmdLineException;
import org.kohsuke.args4j.CmdLineParser;

public class CommandlineOptions {

    public boolean parseCmdLine(String[] args, String str) {
        CmdLineParser parser = new CmdLineParser(this);
        if(args.length == 0) {
            printUsage(parser, str);
            return false;
        } else {
            try {
                parser.parseArgument(args);
            } catch(CmdLineException e) {
                System.out.println("Arguments Error: " + e.getMessage() + "\n");
                printUsage(parser, str);
                return false;
            }
        }
        return true;
    }

    private void printUsage(CmdLineParser parser, String str) {
        System.out.println(str);
        OutputStream stream = new ByteArrayOutputStream();
        parser.printUsage(stream);
        System.out.println(stream.toString());
    }
}
