/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.application;

import javax.servlet.ServletContext;
import java.io.File;

public class ApplicationPath {

    private static String appName = "";
    private static String appPath = "";
    private static String appROOTPath = "";
    private static String appWEBINFPath = "";
    private static String appFilePath = "";
    private static File appFileDir = null;
    private static String appTemplatePath = "";
    private static File appTemplateDir = null;
    private static String appJsonFilePath = null;

    public static String getAppName() {
        return appName;
    }

    public static String getAppPath() {
        return appPath;
    }

    public static String getAppROOTPath() {
        return appROOTPath;
    }

    public static String getAppWEBINFPath() {
        return appWEBINFPath;
    }

    public static String getAppFilePath() {
        return appFilePath;
    }

    public static File getAppFileDir() {
        return appFileDir;
    }

    public static String getAppTemplatePath() {
        return appTemplatePath;
    }

    public static File getAppTemplateDir() {
        return appTemplateDir;
    }

    public static String getAppJsonFilePath() {
        return appJsonFilePath;
    }

    public static boolean initializePath(ServletContext context) {
        File appROOTDir = new File(context.getRealPath("/"));
        File appDir = appROOTDir.getParentFile();
        appPath = appDir.getAbsolutePath().replace('\\', '/');
        appName = appPath.substring(appPath.lastIndexOf('/') + 1);
        System.out.println("application name is: " + getAppName());
        System.out.println("application path is: " + getAppPath());
        appROOTPath = appROOTDir.getAbsolutePath().replace('\\', '/');
        appWEBINFPath = appROOTPath + "/WEB-INF";
        String externalFilePath = appPath + "_ext";
        if (!assertDirectory(externalFilePath))
            return false;
        appFilePath = externalFilePath + "/files";
        appFileDir = new File(appFilePath);
        if (!assertDirectory(appFileDir))
            return false;
        appTemplatePath = externalFilePath + "/templates";
        appTemplateDir = new File(appTemplatePath);
        appJsonFilePath=externalFilePath + "/data.json";
        return assertDirectory(appTemplateDir);
    }

    public static boolean assertDirectory(String path){
        File f = new File(path);
        return assertDirectory(f);
    }

    public static boolean assertDirectory(File dir){
        if (dir.exists()){
            return true;
        }
        return dir.mkdir();
    }
}
