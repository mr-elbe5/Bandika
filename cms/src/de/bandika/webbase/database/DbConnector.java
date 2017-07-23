/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.database;

import de.bandika.base.crypto.SimpleEncryption;
import de.bandika.base.log.Log;
import de.bandika.webbase.util.ApplicationPath;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.io.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DbConnector {

    public static final String DB_CONFIG_FILE = "bandikadb.conf";
    public static final String KEY_DBCLASS = "dbClass";
    public static final String KEY_DBURL = "dbUrl";
    public static final String KEY_DBUSER = "dbUser";
    public static final String KEY_DBPWD = "dbPwd";
    public static final String KEY_INITIALSIZE = "initialSize";
    public static final String KEY_MAXACTIVE = "maxActive";
    public static final String KEY_MAXWAIT = "maxWait";
    public static final String KEY_MINIDLE = "minIdle";
    public static final String KEY_VALIDATIONQUERY = "validationQuery";
    public static final String KEY_VALIDATIONINTERVAL = "validationInterval";
    public static final String KEY_REMOVEABANDONEDTIMEOUT = "removeAbandonedTimeout";
    public static final String KEY_MINEVICTABLEIDLETIMEMILLIS = "minEvictableIdleTimeMillis";
    public static final String KEY_TIMEBETWEENEVICTIONRUNSMILLIS = "timeBetweenEvictionRunsMillis";
    private static DbConnector instance = null;

    public static DbConnector getInstance() {
        if (instance == null) {
            instance = new DbConnector();
        }
        return instance;
    }

    protected Properties dbProperties = new Properties();
    private DataSource dataSource = null;

    public void writeProperties() throws IOException {
        File f = new File(ApplicationPath.getAppPath(), DB_CONFIG_FILE);
        if (f.exists()) {
            f.delete();
        }
        if (!f.createNewFile()) {
            throw new IOException("could not create file " + f.getName());
        }
        try (FileOutputStream stream = new FileOutputStream(f)) {
            dbProperties.store(stream, "");
            stream.flush();
        }
    }

    public boolean readProperties() {
        File f = new File(ApplicationPath.getAppPath(), DB_CONFIG_FILE);
        return readProperties(f);
    }

    public boolean readProperties(File f) {
        if (!f.exists()) {
            return false;
        }
        try {
            try (BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f))) {
                dbProperties.clear();
                dbProperties.load(stream);
            }
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public void setProperties(String dbClass, String dbUrl, String dbUser, String dbPwd) {
        dbProperties.clear();
        dbProperties.setProperty(KEY_DBCLASS, dbClass);
        dbProperties.setProperty(KEY_DBURL, dbUrl);
        dbProperties.setProperty(KEY_DBUSER, SimpleEncryption.encrypt(dbUser));
        dbProperties.setProperty(KEY_DBPWD, SimpleEncryption.encrypt(dbPwd));
        dbProperties.setProperty(KEY_INITIALSIZE, "10");
        dbProperties.setProperty(KEY_MAXACTIVE, "100");
        dbProperties.setProperty(KEY_MAXWAIT, "30000");
        dbProperties.setProperty(KEY_MINIDLE, "10");
        dbProperties.setProperty(KEY_VALIDATIONQUERY, "");
        dbProperties.setProperty(KEY_VALIDATIONINTERVAL, "30000");
        dbProperties.setProperty(KEY_REMOVEABANDONEDTIMEOUT, "60");
        dbProperties.setProperty(KEY_MINEVICTABLEIDLETIMEMILLIS, "60000");
        dbProperties.setProperty(KEY_TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
    }

    public boolean loadDataSource() {
        if (dataSource != null) {
            return true;
        }
        if (dbProperties.isEmpty()) {
            return false;
        }
        try {
            Log.log("initializing database...");
            Log.log("database url is: " + dbProperties.getProperty(KEY_DBURL));
            try {
                PoolProperties p = new PoolProperties();
                p.setJdbcInterceptors("org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" + "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
                p.setValidationQuery("SELECT 1");
                p.setDriverClassName(dbProperties.getProperty(DbConnector.KEY_DBCLASS));
                p.setUrl(dbProperties.getProperty(DbConnector.KEY_DBURL));
                p.setUsername(SimpleEncryption.decrypt(dbProperties.getProperty(DbConnector.KEY_DBUSER)));
                p.setPassword(SimpleEncryption.decrypt(dbProperties.getProperty(DbConnector.KEY_DBPWD)));
                boolean validation = false;
                String prop = dbProperties.getProperty(DbConnector.KEY_INITIALSIZE);
                if (prop != null && !prop.isEmpty()) {
                    p.setInitialSize(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_MAXACTIVE);
                if (prop != null && !prop.isEmpty()) {
                    p.setMaxActive(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_MINIDLE);
                if (prop != null && !prop.isEmpty()) {
                    p.setMinIdle(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_MAXWAIT);
                if (prop != null && !prop.isEmpty()) {
                    p.setMaxWait(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_VALIDATIONQUERY);
                if (prop != null && !prop.isEmpty()) {
                    p.setValidationQuery(prop);
                    validation = true;
                }
                prop = dbProperties.getProperty(DbConnector.KEY_VALIDATIONINTERVAL);
                if (prop != null && !prop.isEmpty()) {
                    p.setValidationInterval(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_MINEVICTABLEIDLETIMEMILLIS);
                if (prop != null && !prop.isEmpty()) {
                    p.setMinEvictableIdleTimeMillis(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_TIMEBETWEENEVICTIONRUNSMILLIS);
                if (prop != null && !prop.isEmpty()) {
                    p.setTimeBetweenEvictionRunsMillis(Integer.parseInt(prop));
                }
                prop = dbProperties.getProperty(DbConnector.KEY_REMOVEABANDONEDTIMEOUT);
                if (prop != null && !prop.isEmpty()) {
                    p.setRemoveAbandonedTimeout(Integer.parseInt(prop));
                }
                p.setJmxEnabled(false);
                p.setTestWhileIdle(false);
                p.setTestOnBorrow(validation);
                p.setTestOnReturn(false);
                p.setLogAbandoned(true);
                p.setRemoveAbandoned(true);
                dataSource = new DataSource();
                dataSource.setPoolProperties(p);
                Log.log("trying to connect...");
                Connection con = null;
                try {
                    con = dataSource.getConnection();
                    if (con == null || con.isClosed()) {
                        dataSource = null;
                    }
                } catch (Exception e) {
                    dataSource = null;
                }
                if (con != null) {
                    Log.log("connection ok");
                    try {
                        con.close();
                    } catch (Exception ignore) {
                    }
                }
            } catch (Exception e) {
                Log.error("error during database initialization", e);
                dataSource = null;
            }
        } catch (Exception e) {
            Log.error("error during initialization", e);
        }
        if (dataSource != null) {
            Log.log("data source successfully created");
        } else {
            Log.error("cannot create valid connection");
        }
        return dataSource != null;
    }

    public boolean isInitialized() {
        return dataSource != null;
    }

    public void clear() {
        if (dataSource != null) {
            Log.log("purging data source");
            dataSource.purge();
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null) {
            return null;
        }
        return dataSource.getConnection();
    }

    public Connection startTransaction() {
        try {
            Connection con = getConnection();
            con.setAutoCommit(false);
            return con;
        } catch (Exception e) {
            return null;
        }
    }

    public boolean commitTransaction(Connection con) {
        boolean result = true;
        try {
            con.commit();
            con.setAutoCommit(true);
        } catch (Exception e) {
            result = false;
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public boolean rollbackTransaction(Connection con) {
        return rollbackTransaction(con, null);
    }

    public boolean rollbackTransaction(Connection con, Exception e) {
        try {
            if (e != null)
                Log.error("rollback", e);
            con.rollback();
            con.setAutoCommit(true);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return false;
    }

    public void closeConnection(Connection con) {
        try {
            if (con != null) {
                con.close();
            }
        } catch (Exception ignore) {/* do nothing */

        }
    }

    public void closeStatement(Statement st) {
        try {
            if (st != null) {
                st.close();
            }
        } catch (Exception ignore) {/* do nothing */

        }
    }

    public boolean executeScript(String sql) {
        Connection con = null;
        try {
            con = startTransaction();
            if (!executeScript(con, sql)) {
                rollbackTransaction(con);
                return false;
            }
            commitTransaction(con);
            return true;
        } finally {
            closeConnection(con);
        }
    }

    public static List<String> splitScript(String src) {
        List<String> list = new ArrayList<>();
        LineNumberReader reader = new LineNumberReader(new StringReader(src));
        StringBuilder sb = null;
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                line = line.trim();
                if (line.isEmpty())
                    continue;
                if (line.startsWith("--")) {
                    if (sb != null) {
                        String command = sb.toString().trim();
                        if (!command.isEmpty()) {
                            list.add(command);
                        }
                        sb = null;
                    }
                    continue;
                }
                if (sb == null)
                    sb = new StringBuilder();
                sb.append(line).append('\n');
            }
            if (sb != null) {
                String command = sb.toString().trim();
                if (!command.isEmpty()) {
                    list.add(command);
                }
            }
        } catch (IOException ex) {
            Log.error("unable to parse sql", ex);
            list.clear();
        }
        return list;
    }

    public boolean executeScript(Connection con, String sql) {
        int count = 0;
        List<String> commands = splitScript(sql);
        for (String command : commands) {
            if (!executeCommand(con, command.trim())) {
                return false;
            }
            count++;
        }
        Log.info("executed " + count + " statement(s)");
        return true;
    }

    protected String adjustCommand(String sqlCmd) {
        return sqlCmd.trim();
    }

    public boolean executeCommand(Connection con, String sqlCmd) {
        String cmd = adjustCommand(sqlCmd);
        Statement stmt;
        try {
            if (sqlCmd.length() == 0) {
                return true;
            }
            stmt = con.createStatement();
            if (cmd.toLowerCase().startsWith("select"))
                stmt.executeQuery(cmd);
            else
                stmt.executeUpdate(cmd);
            stmt.close();
            return true;
        } catch (SQLException e) {
            Log.error("error on sql command", e);
            return false;
        }
    }

    public boolean checkSelect(String sql) {
        Connection con = null;
        boolean result = false;
        try {
            con = getConnection();
            if (con != null) {
                result = checkSelect(con, sql);
            }
        } catch (SQLException e) {
            Log.error("error on sql command", e);
        } finally {
            closeConnection(con);
        }
        return result;
    }

    public boolean checkSelect(Connection con, String sqlCmd) {
        String cmd = sqlCmd.replace(';', ' ').trim();
        cmd = adjustCommand(cmd);
        Statement stmt;
        boolean result;
        try {
            stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(cmd);
            result = rs.next();
            stmt.close();
            return result;
        } catch (SQLException e) {
            return false;
        }
    }
}
