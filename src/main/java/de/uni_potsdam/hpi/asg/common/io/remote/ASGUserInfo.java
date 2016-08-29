package de.uni_potsdam.hpi.asg.common.io.remote;

/*
 * Copyright (C) 2016 Norman Kluge
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

import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;

public class ASGUserInfo implements UserInfo, UIKeyboardInteractive {
    public String getPassword() {
        return null;
    }

    public boolean promptYesNo(String str) {
        return false;
    }

    public String getPassphrase() {
        return null;
    }

    public boolean promptPassphrase(String message) {
        return false;
    }

    public boolean promptPassword(String message) {
        return false;
    }

    public void showMessage(String message) {
    }

    public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt, boolean[] echo) {
        return null;
    }

}
