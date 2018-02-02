/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.configuration;

import de.bandika.base.log.Log;
import de.bandika.webbase.database.DbBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Locale;
import java.util.Map;

public class WebConfigurationBean extends DbBean {

    public void setLocales(Map<Locale, String> locales) {
        Connection con = null;
        try {
            con = getConnection();
            readLocales(con, locales);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
    }

    public void readLocales(Connection con, Map<Locale, String> locales) throws SQLException {
        locales.clear();
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT locale,home FROM t_locale");
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    try {
                        locales.put(new Locale(rs.getString(1)), rs.getString(2));
                    } catch (Exception e) {
                        Log.error("no appropriate locale", e);
                    }
                }
            }
        } finally {
            try {
                if (pst != null) {
                    pst.close();
                }
            } catch (SQLException ignored) {
            }
        }
    }

}
