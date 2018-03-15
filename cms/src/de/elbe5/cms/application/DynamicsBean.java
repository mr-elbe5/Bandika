/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.log.Log;
import de.elbe5.webbase.database.DbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DynamicsBean extends DbBean {

    private static DynamicsBean instance = null;

    public static DynamicsBean getInstance() {
        if (instance == null) {
            instance = new DynamicsBean();
        }
        return instance;
    }

    public String getCss() {
        Connection con = getConnection();
        PreparedStatement pst = null;
        String css="";
        try {
            pst = con.prepareStatement("SELECT css_code FROM t_dynamics");
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    css=rs.getString(1);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return css;
    }

    public String getJs() {
        Connection con = getConnection();
        PreparedStatement pst = null;
        String js="";
        try {
            pst = con.prepareStatement("SELECT js_code FROM t_dynamics");
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    js=rs.getString(1);
                }
            }
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeStatement(pst);
            closeConnection(con);
        }
        return js;
    }

    public boolean saveCss(String css) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_dynamics SET css_code=? ");
            pst.setString(1,css);
            pst.executeUpdate();
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        } finally {
            closeStatement(pst);
        }
    }

    public boolean saveJs(String js) {
        Connection con = startTransaction();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_dynamics SET js_code=? ");
            pst.setString(1,js);
            pst.executeUpdate();
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        } finally {
            closeStatement(pst);
        }
    }

}
