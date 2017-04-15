/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.cluster;

import de.bandika.database.DbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClusterBean extends DbBean {

    private static ClusterBean instance = null;

    public static void setInstance(ClusterBean instance) {
        ClusterBean.instance = instance;
    }

    public static ClusterBean getInstance() {
        if (instance == null)
            instance = new ClusterBean();
        return instance;
    }

    public ServerData assertSelf(String ownAddress) {
        Connection con = null;
        PreparedStatement pst = null;
        ServerData data = null;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT port,active,change_date FROM t_cluster WHERE ipaddress=?");
            pst.setString(1, ownAddress);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                int i = 1;
                data = new ServerData();
                data.setAddress(ownAddress);
                data.setPort(rs.getInt(i++));
                data.setActive(rs.getBoolean(i++));
                data.setChangeDate(rs.getTimestamp(i));
            }
            rs.close();
            if (data == null) {
                pst.close();
                data = new ServerData();
                data.setAddress(ownAddress);
                pst = con.prepareStatement("INSERT INTO t_cluster (ipaddress,port,active) VALUES(?,?,?)");
                pst.setString(1, data.getAddress());
                pst.setInt(2, data.getPort());
                pst.setBoolean(3, data.isActive());
                pst.executeUpdate();
            }
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return data;
    }

    public List<ServerData> getOtherServers(String ownAddress) {
        List<ServerData> list = new ArrayList<>();
        Connection con = null;
        PreparedStatement pst = null;
        ServerData data;
        try {
            con = getConnection();
            pst = con.prepareStatement("SELECT ipaddress,port,change_date FROM t_cluster");
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                int i = 1;
                data = new ServerData();
                data.setAddress(rs.getString(i++));
                data.setPort(rs.getInt(i++));
                data.setActive(true);
                data.setChangeDate(rs.getTimestamp(i));
                if (!data.getAddress().equals(ownAddress))
                    list.add(data);
            }
            rs.close();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return list;
    }

    public boolean updatePort(ServerData data) {
        Connection con = null;
        PreparedStatement pst = null;
        int count = 0;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_cluster SET port=?, change_date=now() WHERE ipaddress=?");
            pst.setInt(1, data.getPort());
            pst.setString(2, data.getAddress());
            count = pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count != 0;
    }

    public boolean activateServer(String address, boolean flag) {
        Connection con = null;
        PreparedStatement pst = null;
        int count = 0;
        try {
            con = getConnection();
            pst = con.prepareStatement("UPDATE t_cluster SET active=?, change_date=now() WHERE ipaddress=?");
            pst.setBoolean(1, flag);
            pst.setString(2, address);
            count = pst.executeUpdate();
        } catch (SQLException se) {
            se.printStackTrace();
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return count != 0;
    }

}