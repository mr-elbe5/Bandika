/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.net.SocketException;
import java.util.HashMap;

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

	public static Logger getInstance() {
		if (instance == null)
			instance = new Logger();
		return instance;
	}

	public static void setInstance(Logger instance) {
		Logger.instance = instance;
	}

	public static void setClassLevel(Class cls, int level) {
		classes.put(cls, level);
	}

	public static int getClassLogLevel(Class cls, int std) {
		Integer i = classes.get(cls);
		if (i != null)
			return (Math.max(i.intValue(), std));
		return std;
	}

	protected Logger() {
	}

	protected void printLog(int logLevel, Class cls, String str) {
		StringBuffer buffer = new StringBuffer();
		if (cls != null) {
			buffer.append("[");
			buffer.append(cls.getName());
			buffer.append("]");
		}
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

	protected boolean isValidThrowable(Class cls, Throwable t) {
		if (t != null && t instanceof SocketException) {
			printLog(WARN, cls, "SocketException after response: " + t.getMessage());
			return false;
		}
		return true;
	}

	public static void log(Class cls, String message, Throwable t, RequestData rdata, SessionData sdata) {
		getInstance().printLog(cls, message, t, rdata, sdata);
	}

	public static void fatal(Class cls, String message, Throwable t) {
		getInstance().printLog(FATAL, cls, message, t);
	}

	public static void fatal(Class cls, String message) {
		getInstance().printLog(FATAL, cls, message);
	}

	public static void error(Class cls, String message, Throwable t) {
		int logLevel = getClassLogLevel(cls, ERROR);
		if (logLevel < LOG_LEVEL)
			return;
		getInstance().printLog(logLevel, cls, message, t);
	}

	public static void error(Class cls, String message) {
		int logLevel = getClassLogLevel(cls, ERROR);
		if (logLevel < LOG_LEVEL)
			return;
		getInstance().printLog(logLevel, cls, message);
	}

	public static void warn(Class cls, String message) {
		int logLevel = getClassLogLevel(cls, WARN);
		if (logLevel < LOG_LEVEL)
			return;
		getInstance().printLog(logLevel, cls, message);
	}

	public static void info(Class cls, String message) {
		int logLevel = getClassLogLevel(cls, INFO);
		if (logLevel < LOG_LEVEL)
			return;
		getInstance().printLog(logLevel, cls, message);
	}

	public static void debug(Class cls, String message) {
		int logLevel = getClassLogLevel(cls, DEBUG);
		if (logLevel < LOG_LEVEL)
			return;
		getInstance().printLog(logLevel, cls, message);
	}

}