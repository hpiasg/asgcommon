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

public class RemoteInformation {

    private String remoteFolder;

    private String host;
    private String username;
    private String password;

    public RemoteInformation(String host, String username, String password, String remotefolder) {
        this.username = username;
        this.password = password;
        this.host = host;
        this.remoteFolder = remotefolder;
    }

    public String getHost() {
        return host;
    }

    public String getPassword() {
        return password;
    }

    public String getRemoteFolder() {
        return remoteFolder;
    }

    public String getUsername() {
        return username;
    }
}
