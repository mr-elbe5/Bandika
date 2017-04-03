/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.sql;

import de.bandika.crypto.Crypto;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.apache.tomcat.jdbc.pool.PoolProperties;

import java.io.*;
import java.sql.*;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;
import de.bandika.data.Log;

public class DbHandler {

    public static final String DB_CONFIG_FILE = "db.conf";

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

    private static DbHandler instance=null;

    public static DbHandler getInstance(){
        if (instance==null){
            instance=new DbHandler();
        }
        return instance;
    }

    private Properties dbProperties = new Properties();
    private DataSource dataSource = null;

    public boolean setProperties(String appPath, String dbClass, String dbUrl, String dbUser, String dbPwd) {
        dbProperties.clear();
        Map<String, String> map = new HashMap<>();
        map.put(KEY_DBCLASS, dbClass);
        map.put(KEY_DBURL, dbUrl);
        map.put(KEY_DBUSER, Crypto.encrypt(dbUser));
        map.put(KEY_DBPWD, Crypto.encrypt(dbPwd));
        map.put(KEY_INITIALSIZE, "10");
        map.put(KEY_MAXACTIVE, "100");
        map.put(KEY_MAXWAIT, "30000");
        map.put(KEY_MINIDLE, "10");
        map.put(KEY_VALIDATIONQUERY, "");
        map.put(KEY_VALIDATIONINTERVAL, "30000");
        map.put(KEY_REMOVEABANDONEDTIMEOUT, "60");
        map.put(KEY_MINEVICTABLEIDLETIMEMILLIS, "60000");
        map.put(KEY_TIMEBETWEENEVICTIONRUNSMILLIS, "5000");
        dbProperties.putAll(map);
        File f=new File(appPath,DB_CONFIG_FILE);
        try {
            writeProperties(f);
        } catch (IOException e) {
            return false;
        }
        return true;
    }

    private void writeProperties(File f) throws IOException {
        if (f.exists())
            f.delete();
        if (!f.createNewFile()) {
            throw new IOException("could not create file " + DB_CONFIG_FILE);
        }
        FileOutputStream stream = new FileOutputStream(f);
        dbProperties.store(stream, "");
        stream.flush();
        stream.close();
    }

    private boolean readProperties(File f) {
        if (!f.exists())
            return false;
        try {
            BufferedInputStream stream = new BufferedInputStream(new FileInputStream(f));
            dbProperties.clear();
            dbProperties.load(stream);
            stream.close();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    public boolean initialize(String appPath) {
        File configFile = new File(appPath, DbHandler.DB_CONFIG_FILE);
        if (!configFile.exists()) {
            return false;
        }
        if (!readProperties(configFile) || !initialize()) {
            return false;
        }
        Log.log("database initialized");
        return true;
    }

    public boolean initialize() {
        if (dbProperties.isEmpty())
            return false;
        try {
            Log.log("initializing database...");
            Log.log("database url is: " + dbProperties.getProperty(KEY_DBURL));
            try {
                PoolProperties p = new PoolProperties();
                p.setJdbcInterceptors(
                        "org.apache.tomcat.jdbc.pool.interceptor.ConnectionState;" +
                                "org.apache.tomcat.jdbc.pool.interceptor.StatementFinalizer");
                p.setValidationQuery("SELECT 1");
                p.setDriverClassName(dbProperties.getProperty(DbHandler.KEY_DBCLASS));
                p.setUrl(dbProperties.getProperty(DbHandler.KEY_DBURL));
                p.setUsername(Crypto.decrypt(dbProperties.getProperty(DbHandler.KEY_DBUSER)));
                p.setPassword(Crypto.decrypt(dbProperties.getProperty(DbHandler.KEY_DBPWD)));

                boolean validation = false;

                String prop = dbProperties.getProperty(DbHandler.KEY_INITIALSIZE);
                if (prop != null && !prop.isEmpty())
                    p.setInitialSize(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_MAXACTIVE);
                if (prop != null && !prop.isEmpty())
                    p.setMaxActive(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_MINIDLE);
                if (prop != null && !prop.isEmpty())
                    p.setMinIdle(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_MAXWAIT);
                if (prop != null && !prop.isEmpty())
                    p.setMaxWait(Integer.parseInt(prop));

                prop = dbProperties.getProperty(DbHandler.KEY_VALIDATIONQUERY);
                if (prop != null && !prop.isEmpty()) {
                    p.setValidationQuery(prop);
                    validation = true;
                }
                prop = dbProperties.getProperty(DbHandler.KEY_VALIDATIONINTERVAL);
                if (prop != null && !prop.isEmpty())
                    p.setValidationInterval(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_MINEVICTABLEIDLETIMEMILLIS);
                if (prop != null && !prop.isEmpty())
                    p.setMinEvictableIdleTimeMillis(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_TIMEBETWEENEVICTIONRUNSMILLIS);
                if (prop != null && !prop.isEmpty())
                    p.setTimeBetweenEvictionRunsMillis(Integer.parseInt(prop));
                prop = dbProperties.getProperty(DbHandler.KEY_REMOVEABANDONEDTIMEOUT);
                if (prop != null && !prop.isEmpty())
                    p.setRemoveAbandonedTimeout(Integer.parseInt(prop));

                p.setJmxEnabled(false);
                p.setTestWhileIdle(false);
                p.setTestOnBorrow(validation);
                p.setTestOnReturn(false);
                p.setLogAbandoned(true);
                p.setRemoveAbandoned(true);

                dataSource = new DataSource();
                dataSource.setPoolProperties(p);
                Log.log("connecting...");
                Connection con = dataSource.getConnection();
                if (con.isClosed()) {
                    Log.error("cannot create valid connection");
                    dataSource=null;
                    return false;
                }
                try {
                    con.close();
                } catch (Exception e) {
                    Log.error( "unable to close connection", e);
                }
            } catch (Exception e) {
                Log.error( "error during initialization", e);
                return false;
            }
        } catch (Exception e) {
            Log.error( "error during initialization", e);
        }
        Log.log("data source successfully created");
        return true;
    }

    public DataSource getDataSource() {
        return dataSource;
    }

    public boolean isInitialized(){
        return dataSource!=null;
    }

    public void clear() {
        if (dataSource != null) {
            Log.log("purging data source");
            dataSource.purge();
        }
    }

    public Connection getConnection() throws SQLException {
        if (dataSource == null)
            return null;
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
                e.printStackTrace();
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
            if (con != null)
                con.close();
        } catch (Exception ignore) {/* do nothing */
        }
    }

    public void closeStatement(Statement st) {
        try {
            if (st != null)
                st.close();
        } catch (Exception ignore) {/* do nothing */
        }
    }

    public void closeResultSet(ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
        } catch (Exception ignore) {/* do nothing */
        }
    }

    public void closeAll(ResultSet rs, Statement st, Connection con) {
        closeResultSet(rs);
        closeStatement(st);
        closeConnection(con);
    }

    public void closeAll(Statement st, Connection con) {
        closeStatement(st);
        closeConnection(con);
    }

    public int getNextId() {
        int id = 0;
        Connection con = null;
        try {
            con = getConnection();
            id = getNextId(con);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return id;
    }

    public int getNextId(Connection con) throws SQLException {
        int id = 0;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select id from t_id");
            ResultSet rs = pst.executeQuery();
            rs.next();
            id = rs.getInt(1);
            rs.close();
            pst.close();
            pst = con.prepareStatement("update t_id set id=?");
            pst.setInt(1, id + 1);
            pst.executeUpdate();
        } finally {
            closeStatement(pst);
        }
        return id;
    }

    public Timestamp getServerTime() {
        Timestamp now = null;
        Connection con = null;
        try {
            con = getConnection();
            now = getServerTime(con);
        } catch (Exception ignored) {
        } finally {
            closeConnection(con);
        }
        return now;
    }

    public Timestamp getServerTime(Connection con) throws SQLException {
        Timestamp now = null;
        PreparedStatement pst = null;
        try {
            pst = con.prepareStatement("select now()");
            ResultSet rs = pst.executeQuery();
            rs.next();
            now = rs.getTimestamp(1);
            rs.close();
        } finally {
            closeStatement(pst);
        }
        return now;
    }

    public void setNullableInt(PreparedStatement pst, int idx, int i) throws SQLException {
        if (i == 0)
            pst.setNull(idx, Types.INTEGER);
        else
            pst.setInt(idx, i);
    }

    public java.sql.Timestamp getSqlTimestamp(java.util.Date date) {
        if (date == null)
            return null;
        return new java.sql.Timestamp(date.getTime());
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

    public boolean executeScript(Connection con, String sql) {
        int count = 0;
        StringTokenizer stk = new StringTokenizer(sql, ";", false);
        while (stk.hasMoreTokens()) {
            String sqlCmd = stk.nextToken().trim();
            if (sqlCmd.length() == 0)
                continue;
            if (!executeCommand(con, sqlCmd))
                return false;
            Log.info(MessageFormat.format("executed statement ''{0}''\n", sqlCmd));
            count++;
        }
        Log.info("executed " + count + " statements");
        return true;
    }

    protected String adjustCommand(String sqlCmd) {
        return sqlCmd;
    }

    public boolean executeCommand(Connection con, String sqlCmd) {
        String cmd = adjustCommand(sqlCmd);
        Statement stmt;
        try {
            if (sqlCmd.length() == 0)
                return true;
            stmt = con.createStatement();
            stmt.executeUpdate(cmd);
            stmt.close();
            return true;
        } catch (SQLException e) {
            Log.error( "error on sql command", e);
            return false;
        }
    }


}
