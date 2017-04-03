/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.HashMap;
import java.net.SocketException;

/**
 * Class Logger is the central Logger singleton. <br>
 * Usage:
 */
public class Logger {

  public static final int FATAL = 5;
  public static final int ERROR = 4;
  public static final int WARN = 3;
  public static final int INFO = 2;
  public static final int DEBUG = 1;
  protected static int LOG_LEVEL = INFO;
  public static boolean PRINT_STACK_TRACE = true;

  protected static Logger instance = null;

  protected static HashMap<Class, Integer> classes = new HashMap<Class, Integer>();

  /**
   * Method getInstance returns the instance of this Logger object.
   *
   * @return the instance (type Logger) of this Logger object.
   */
  public static Logger getInstance() {
    if (instance == null)
      instance = new Logger();
    return instance;
  }

  /**
   * Method setInstance sets the instance of this Logger object.
   *
   * @param instance the instance of this Logger object.
   */
  public static void setInstance(Logger instance) {
    Logger.instance = instance;
  }

  /**
   * Method setClassLevel
   *
   * @param cls   of type Class
   * @param level of type int
   */
  public static void setClassLevel(Class cls, int level) {
    classes.put(cls, level);
  }

  /**
   * Method getClassLogLevel
   *
   * @param cls of type Class
   * @param std of type int
   * @return int
   */
  public static int getClassLogLevel(Class cls, int std) {
    Integer i = classes.get(cls);
    if (i != null)
      return (Math.max(i.intValue(), std));
    return std;
  }

  /**
   * Constructor Logger creates a new Logger instance.
   */
  protected Logger() {
  }

  /**
   * Method printLog
   *
   * @param logLevel of type int
   * @param cls      of type Class
   * @param str      of type String
   */
  protected void printLog(int logLevel, Class cls, String str) {
    StringBuffer buffer = new StringBuffer("[");
    buffer.append(cls.getName());
    buffer.append("]");
    switch (logLevel) {
      case FATAL:
        buffer.append(" FATAL: ");
        break;
      case ERROR:
        buffer.append(" ERROR: ");
        break;
      case WARN:
        buffer.append(" WARN: ");
        break;
      case INFO:
        buffer.append(" INFO: ");
        break;
      case DEBUG:
        buffer.append(" DEBUG: ");
        break;
      default:
        buffer.append(": ");
        break;
    }
    buffer.append(str);
    System.out.println(buffer.toString());
  }

  /**
   * Method printLog
   *
   * @param logLevel of type int
   * @param cls      of type Class
   * @param str      of type String
   * @param t        of type Throwable
   */
  protected void printLog(int logLevel, Class cls, String str, Throwable t) {
    if (!isValidThrowable(cls, t))
      return;
    printLog(logLevel, cls, str);
    System.out.println(t.getClass().getName() + " : " + t.getMessage());
    if (PRINT_STACK_TRACE)
      t.printStackTrace(System.out);
    t = t.getCause();
    if (t != null)
      printLog(logLevel, cls, "  Caused by:", t);
  }

  /**
   * Method printLog
   *
   * @param cls     of type Class
   * @param message of type String
   * @param t       of type Throwable
   * @param rdata   of type RequestData
   * @param sdata   of type SessionData
   */
  protected void printLog(Class cls, String message, Throwable t, RequestData rdata, SessionData sdata) {
    if (!isValidThrowable(cls, t))
      return;
    StringBuffer buffer = new StringBuffer();
    if (rdata != null) {
      buffer.append(rdata.toString());
    }
    if (message != null) {
      buffer.append(message);
    }
    message = buffer.toString();
    if (t == null)
      printLog(DEBUG, cls, message);
    else
      printLog(DEBUG, cls, message, t);
  }

  /**
   * Method isValidThrowable
   *
   * @param cls of type Class
   * @param t   of type Throwable
   * @return boolean
   */
  protected boolean isValidThrowable(Class cls, Throwable t) {
    if (t != null && t instanceof SocketException) {
      printLog(WARN, cls, "SocketException after response: " + t.getMessage());
      return false;
    }
    return true;
  }

  /**
   * Method log
   *
   * @param cls     of type Class
   * @param message of type String
   * @param t       of type Throwable
   * @param rdata   of type RequestData
   * @param sdata   of type SessionData
   */
  public static void log(Class cls, String message, Throwable t, RequestData rdata, SessionData sdata) {
    getInstance().printLog(cls, message, t, rdata, sdata);
  }

  /**
   * Method fatal
   *
   * @param cls     of type Class
   * @param message of type String
   * @param t       of type Throwable
   */
  public static void fatal(Class cls, String message, Throwable t) {
    getInstance().printLog(FATAL, cls, message, t);
  }

  /**
   * Method fatal
   *
   * @param cls     of type Class
   * @param message of type String
   */
  public static void fatal(Class cls, String message) {
    getInstance().printLog(FATAL, cls, message);
  }

  /**
   * Method error
   *
   * @param cls     of type Class
   * @param message of type String
   * @param t       of type Throwable
   */
  public static void error(Class cls, String message, Throwable t) {
    int logLevel = getClassLogLevel(cls, ERROR);
    if (logLevel < LOG_LEVEL)
      return;
    getInstance().printLog(logLevel, cls, message, t);
  }

  /**
   * Method error
   *
   * @param cls     of type Class
   * @param message of type String
   */
  public static void error(Class cls, String message) {
    int logLevel = getClassLogLevel(cls, ERROR);
    if (logLevel < LOG_LEVEL)
      return;
    getInstance().printLog(logLevel, cls, message);
  }

  /**
   * Method warn
   *
   * @param cls     of type Class
   * @param message of type String
   */
  public static void warn(Class cls, String message) {
    int logLevel = getClassLogLevel(cls, WARN);
    if (logLevel < LOG_LEVEL)
      return;
    getInstance().printLog(logLevel, cls, message);
  }

  /**
   * Method info
   *
   * @param cls     of type Class
   * @param message of type String
   */
  public static void info(Class cls, String message) {
    int logLevel = getClassLogLevel(cls, INFO);
    if (logLevel < LOG_LEVEL)
      return;
    getInstance().printLog(logLevel, cls, message);
  }

  /**
   * Method debug
   *
   * @param cls     of type Class
   * @param message of type String
   */
  public static void debug(Class cls, String message) {
    int logLevel = getClassLogLevel(cls, DEBUG);
    if (logLevel < LOG_LEVEL)
      return;
    getInstance().printLog(logLevel, cls, message);
  }

}