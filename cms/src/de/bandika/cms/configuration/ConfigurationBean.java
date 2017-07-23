/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.configuration;

import de.bandika.base.log.Log;
import de.bandika.base.mail.Mailer;
import de.bandika.webbase.configuration.WebConfigurationBean;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Locale;

public class ConfigurationBean extends WebConfigurationBean {

    private static ConfigurationBean instance = null;

    public static ConfigurationBean getInstance() {
        if (instance == null) {
            instance = new ConfigurationBean();
        }
        return instance;
    }

    public Configuration getConfiguration() {
        Connection con = null;
        Configuration config = new Configuration();
        try {
            con = getConnection();
            readConfiguration(con, config);
        } catch (SQLException se) {
            Log.error("sql error", se);
        } finally {
            closeConnection(con);
        }
        return config;
    }

    public void readConfiguration(Connection con, Configuration config) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("SELECT defaultLocale, mailHost, mailPort, mailConnectionType, mailUser, mailPassword, mailSender, timerInterval, maxVersions FROM t_configuration");
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    int i = 1;
                    config.setDefaultLocale(new Locale(rs.getString(i++)));
                    config.setSmtpHost(rs.getString(i++));
                    config.setSmtpPort(rs.getInt(i++));
                    config.setSmtpConnectionType(Mailer.SmtpConnectionType.valueOf(rs.getString(i++)));
                    config.setSmtpUser(rs.getString(i++));
                    config.setSmtpPassword(new String(Base64.getEncoder().encode(rs.getString(i++).getBytes())));
                    config.setMailSender(rs.getString(i++));
                    config.setTimerInterval(rs.getInt(i++));
                    config.setMaxVersions(rs.getInt(i));
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

    public boolean saveConfiguration(Configuration config) {
        Connection con = startTransaction();
        try {
            writeConfiguration(con, config);
            return commitTransaction(con);
        } catch (Exception se) {
            return rollbackTransaction(con, se);
        }
    }

    protected void writeConfiguration(Connection con, Configuration config) throws SQLException {
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("UPDATE t_configuration SET defaultLocale=?, mailHost=?, mailPort=?, mailConnectionType=?, mailUser=?, mailPassword=?, mailSender=?, timerInterval=?, maxVersions=? ");
            int i = 1;
            pst.setString(i++, config.getDefaultLocale().getLanguage());
            pst.setString(i++, config.getSmtpHost());
            pst.setInt(i++, config.getSmtpPort());
            pst.setString(i++, config.getSmtpConnectionType().name());
            pst.setString(i++, config.getSmtpUser());
            pst.setString(i++, new String(Base64.getDecoder().decode(config.getSmtpPassword().getBytes())));
            pst.setString(i++, config.getMailSender());
            pst.setInt(i++, config.getTimerInterval());
            pst.setInt(i, config.getMaxVersions());
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
    }
}
