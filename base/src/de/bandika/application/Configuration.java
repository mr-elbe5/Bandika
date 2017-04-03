/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.Logger;
import de.bandika._base.StringHelper;

import javax.sql.DataSource;
import javax.naming.InitialContext;
import javax.naming.Context;
import java.util.HashSet;
import java.util.Locale;
import java.util.HashMap;
import java.text.SimpleDateFormat;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.StringTokenizer;

public class Configuration {

  public static final int DBTYPE_NONE = 0;
  public static final int DBTYPE_POSTGRES = 1;
  public static final int DBTYPE_MYSQL = 2;

  public static String WEB_APP_START =
    "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n" +
      "<web-app xmlns=\"http://java.sun.com/xml/ns/j2ee\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
      " xsi:schemaLocation=\"http://java.sun.com/xml/ns/j2ee http://java.sun.com/xml/ns/j2ee/web-app_2_4.xsd\"\n" +
      " version=\"2.4\">\n";

  public static String WEB_APP_END = "  <listener>\n" +
    "    <listener-class>de.bandika.application.ApplicationContextListener</listener-class>\n" +
    "  </listener>\n" +
    "</web-app>";

  private static String basePath = "";
  private static Locale stdLocale = Locale.ENGLISH;
  private static HashSet<Locale> allLocales=new HashSet<Locale>();
  private static DataSource dataSource = null;
  private static DataSource userSource = null;
  private static int databaseType = DBTYPE_NONE;
  private static String appTitle = "";
  private static boolean showFirstMenuLevel = true;
  private static SimpleDateFormat dateFormat;
  private static SimpleDateFormat timeFormat;
  private static SimpleDateFormat timeFormatShort;
  private static SimpleDateFormat dateTimeFormat;
  private static int sessionTimeout = 30;
  private static int maxVersions = 0;

  private static HashMap<String, String> configs = new HashMap<String, String>();

  public static String getBasePath() {
    return basePath;
  }

  public static void setBasePath(String basePath) {
    Configuration.basePath = basePath;
  }

  public static void initialize() {
    try {
      Logger.info(null, "base path is: " + Configuration.getBasePath());
      String appFolder = Configuration.getBasePath();
      int pos = appFolder.lastIndexOf("/ROOT");
      if (pos != -1)
        appFolder = appFolder.substring(0, pos);
      pos = appFolder.lastIndexOf('/');
      if (pos != -1)
        appFolder = appFolder.substring(pos + 1);
      Logger.info(null, "app folder is: " + appFolder);
      InitialContext context = new InitialContext();
      Context envCtx = (Context) context.lookup("java:comp/env");
      BaseConfig config = (BaseConfig) envCtx.lookup("config_" + appFolder);
      String dataSourceName = config.getDataSource();
      String userSourceName = config.getUserSource();
      String dbType = config.getDbType().toLowerCase();
      Logger.info(null, "database type is: " + dbType);
      if (dbType.startsWith("postgres"))
        databaseType = Configuration.DBTYPE_POSTGRES;
      else if (dbType.startsWith("mysql"))
        databaseType = Configuration.DBTYPE_MYSQL;
      else
        databaseType = Configuration.DBTYPE_NONE;
      Logger.info(null, "data source name is: " + dataSourceName);
      Logger.info(null, "user source name is: " + userSourceName);
      dataSource = (DataSource) envCtx.lookup(dataSourceName);
      userSource = (DataSource) envCtx.lookup(userSourceName);
      load();
    } catch (Exception e) {
      Logger.error(Configuration.class, "error during initialization", e);
    }
  }

  public static Locale getStdLocale() {
    return stdLocale;
  }

  public static void setStdLocale(String localeName) {
    if (StringHelper.isNullOrEmtpy(localeName)) {
      stdLocale = Locale.ENGLISH;
      return;
    }
    try {
      stdLocale = new Locale(localeName);
    } catch (Exception e) {
      stdLocale = Locale.ENGLISH;
    }
  }

  public static HashSet<Locale> getAllLocales() {
    return allLocales;
  }

  public static int getDatabaseType() {
    return databaseType;
  }

  public static Connection getConnection() throws SQLException {
    if (dataSource == null)
      return null;
    return dataSource.getConnection();
  }

  public static Connection getUserConnection() throws SQLException {
    if (userSource == null)
      return null;
    return userSource.getConnection();
  }

  public static String getAppTitle() {
    return appTitle;
  }

  public static boolean showFirstMenuLevel() {
    return showFirstMenuLevel;
  }

  public static void setShowFirstMenuLevel(boolean showFirstMenuLevel) {
    Configuration.showFirstMenuLevel = showFirstMenuLevel;
  }

  public static boolean useLanguageBranches() {
    return getAllLocales().size() > 1 && !showFirstMenuLevel();
  }

  public static SimpleDateFormat getDateFormat() {
    return dateFormat;
  }

  public static void setDateFormat(SimpleDateFormat dateFormat) {
    Configuration.dateFormat = dateFormat;
  }

  public static SimpleDateFormat getTimeFormat() {
    return timeFormat;
  }

  public static void setTimeFormat(SimpleDateFormat timeFormat) {
    Configuration.timeFormat = timeFormat;
  }

  public static SimpleDateFormat getTimeFormatShort() {
    return timeFormatShort;
  }

  public static void setTimeFormatShort(SimpleDateFormat timeFormatShort) {
    Configuration.timeFormatShort = timeFormatShort;
  }

  public static SimpleDateFormat getDateTimeFormat() {
    return dateTimeFormat;
  }

  public static void setDateTimeFormat(SimpleDateFormat dateTimeFormat) {
    Configuration.dateTimeFormat = dateTimeFormat;
  }

  public static int getSessionTimeout() {
    return sessionTimeout;
  }

  public static int getMaxVersions() {
    return maxVersions;
  }

  public static void setMaxVersions(int maxVersions) {
    Configuration.maxVersions = maxVersions;
  }

  public static HashMap<String, String> getConfigs() {
    return configs;
  }

  public static String getConfigurationValue(String key) {
    if (!configs.containsKey(key))
      return "";
    return configs.get(key);
  }

  public static String getConfigString(String key) {
    return getConfigurationValue(key);
  }

  public static int getConfigInt(String key) {
    int val = 0;
    try {
      val = Integer.parseInt(getConfigurationValue(key));
    } catch (Exception ignore) {
    }
    return val;
  }

  public static boolean getConfigBoolean(String key) {
    boolean val = false;
    try {
      val = Boolean.parseBoolean(getConfigurationValue(key));
    } catch (Exception ignore) {
    }
    return val;
  }

  public static void load() {
    Configuration.configs = ApplicationBean.getInstance().getConfiguration();
    StringTokenizer stk = new StringTokenizer(configs.get("allLocales"), ",");
    while (stk.hasMoreTokens()) {
      Locale locale = null;
      String lang = stk.nextToken();
      try {
        locale = new Locale(lang);
        Logger.info(null, "found locale: " + getStdLocale().getLanguage());
      } catch (Exception e) {
        Logger.error(null, "no locale found for: " + lang);
      }
      allLocales.add(locale);
    }
    setStdLocale(configs.get("stdLocale"));
    Logger.info(null, "standard locale is: " + getStdLocale().getLanguage());
    try {
      setDateFormat(new SimpleDateFormat(configs.get("dateFormat")));
    } catch (Exception ignore) {
    }
    try {
      setTimeFormat(new SimpleDateFormat(configs.get("timeFormat")));
    } catch (Exception ignore) {
    }
    try {
      setTimeFormatShort(new SimpleDateFormat(configs.get("timeFormatShort")));
    } catch (Exception ignore) {
    }
    try {
      setDateTimeFormat(new SimpleDateFormat(configs.get("dateTimeFormat")));
    } catch (Exception ignore) {
    }
    try {
      setMaxVersions(Integer.parseInt(configs.get("maxVersions")));
    } catch (Exception ignore) {
    }
    appTitle = configs.get("applicationTitle");
    Logger.info(null, "application title is: " + getAppTitle());
    try {
      setShowFirstMenuLevel(Boolean.parseBoolean(configs.get("showFirstMenuLevel")));
    } catch (Exception ignore) {
    }
    Logger.info(null, "menu level is: " + showFirstMenuLevel());
  }

}
