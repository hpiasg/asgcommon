package de.uni_potsdam.hpi.asg.common.remote;

/*
 * Copyright (C) 2016 - 2017 Norman Kluge
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

import java.io.OutputStream;

import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import de.uni_potsdam.hpi.asg.common.iohelper.TimeStat;

public class RunSHScript {

    public static int run(Session session, String script, String folder) {
        try {
            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd " + folder + "; sh " + script);

            int x = 0;
            channel.connect();
            while(channel.isConnected()) {
                Thread.sleep(1000);
                x++;
                if(x >= 300) { // 5min
                    channel.disconnect();
                    return -2;
                }
            }
            channel.disconnect();
            return channel.getExitStatus();
        } catch(JSchException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static class TimedResult {
        private int  code;
        private long userTime;
        private long systemTime;

        public TimedResult(int code, long userTime, long systemTime) {
            this.code = code;
            this.userTime = userTime;
            this.systemTime = systemTime;
        }

        public int getCode() {
            return code;
        }

        public long getSystemTime() {
            return systemTime;
        }

        public long getUserTime() {
            return userTime;
        }

        public long getCPUTime() {
            return systemTime + userTime;
        }
    }

    public static TimedResult runTimed(Session session, String script, String folder) {
        try {
            TimeStat stat = TimeStat.create();
            if(stat == null) {
                return new TimedResult(-3, 0, 0);
            }

            ChannelExec channel = (ChannelExec)session.openChannel("exec");
            channel.setCommand("cd " + folder + ";" + stat.getRemoteCmdStr() + " sh " + script);

            OutputStream out = stat.getStream();
            if(out == null) {
                return new TimedResult(-3, 0, 0);
            }
            channel.setOutputStream(out);
            channel.setErrStream(out);

            int x = 0;
            channel.connect();
            while(channel.isConnected()) {
                Thread.sleep(1000);
                x++;
                if(x >= 300) { // 5min
                    channel.disconnect();
                    return new TimedResult(-2, 0, 0);
                }
            }

            channel.disconnect();

            long userTime = 0;
            long systemTime = 0;
            if(stat.evaluate()) {
                userTime = stat.getUserTime();
                systemTime = stat.getSystemTime();
            }

            return new TimedResult(channel.getExitStatus(), userTime, systemTime);
        } catch(

        JSchException e) {
            e.printStackTrace();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
        return new TimedResult(-1, 0, 0);
    }
}
