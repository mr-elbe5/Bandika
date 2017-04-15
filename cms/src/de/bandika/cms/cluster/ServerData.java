/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.cluster;

import java.util.Date;

public class ServerData {

    public static int PORT_DEFAULT = 7321;

    protected String address;
    protected int port = PORT_DEFAULT;
    protected boolean active = false;
    protected Date changeDate = new Date();

    protected int timeouts = 0;

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Date getChangeDate() {
        return changeDate;
    }

    public java.sql.Date getSqlChangeDate() {
        return new java.sql.Date(changeDate.getTime());
    }

    public void setChangeDate(Date changeDate) {
        this.changeDate = changeDate;
    }

    public int getTimeouts() {
        return timeouts;
    }

    public void setTimeouts(int timeouts) {
        this.timeouts = timeouts;
    }

    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ServerData))
            return false;
        ServerData srv = (ServerData) obj;
        return address != null && srv.address != null && address.equals(srv.address);
    }

}