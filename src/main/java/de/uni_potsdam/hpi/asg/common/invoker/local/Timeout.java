package de.uni_potsdam.hpi.asg.common.invoker.local;

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

public class Timeout implements Runnable {

    private Thread t;
    private int    timeout;

    public Timeout(Thread t, int timeout) {
        this.t = t;
        this.timeout = timeout;
    }

    public void run() {
        try {
            Thread.sleep(timeout);
            t.interrupt();
            return;
        } catch(InterruptedException e) {
            return;
        }
    }
}