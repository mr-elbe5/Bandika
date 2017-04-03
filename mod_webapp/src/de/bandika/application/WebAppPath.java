/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika.data.Log;

import javax.servlet.ServletContext;
import java.io.File;

public class WebAppPath {

    private static String appPath = "";
    private static String appROOTPath = "";
    private static String appFolder = "";

    public static String getAppPath() {
        return appPath;
    }

    public static String getAppROOTPath() {
        return appROOTPath;
    }

    public static String getAppFolder() {
        return appFolder;
    }

    public static void initializePath(File appDir, File appROOTDir) {
        if (appDir == null  || appROOTDir==null)
            return;
        appPath = appDir.getAbsolutePath().replace('\\','/');
        Log.log("application path is: " + WebAppPath.getAppPath());
        WebAppPath.appROOTPath = appROOTDir.getAbsolutePath().replace('\\','/');
        int pos = appPath.lastIndexOf('/');
        if (pos != -1)
            appFolder = appPath.substring(pos + 1);
        Log.log("app folder is: " + appFolder);
    }

    public static File getCatalinaBaseDir() {
        return new File(System.getProperty("catalina.base")).getAbsoluteFile();
    }

    public static File getCatalinaConfDir() {
        return new File(getCatalinaBaseDir(), "conf");
    }

    public static File getCatalinaAppDir(String appBase) {
        return new File(getCatalinaBaseDir(), appBase);
    }

    public static File getCatalinaAppDir(ServletContext context) {
        return getCatalinaAppROOTDir(context).getParentFile();
    }

    public static File getCatalinaAppROOTDir(String appBase) {
        return new File(getCatalinaAppDir(appBase), "ROOT");
    }

    public static File getCatalinaAppROOTDir(ServletContext context) {
        return new File(context.getRealPath("/"));
    }
}
