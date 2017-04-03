/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.module;

import de.bandika._base.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipEntry;
import java.io.ByteArrayInputStream;
import java.io.IOException;

public class ModuleData extends BaseData {

  public static String DATAKEY = "data|module";

  public static final String BASE_MODULE_NAME = "base";

  public static final String WEBXML_SERVLETS_START = "<!--module-servlets-start-->";
  public static final String WEBXML_SERVLETS_END = "<!--module-servlets-end-->";
  public static final String WEBXML_SERVLET_MAPPINGS_START = "<!--module-servlet-mappings-start-->";
  public static final String WEBXML_SERVLET_MAPPINGS_END = "<!--module-servlet-mappings-end-->";

  protected String name;
  protected String authorName;
  protected String dependencies = "";
  protected String properties = "";
  protected String headIncludeFile = null;
  protected ArrayList<String> directories = new ArrayList<String>();
  protected ArrayList<String> installFiles = new ArrayList<String>();
  protected HashMap<String, byte[]> files = new HashMap<String, byte[]>();
  protected String installLog = "";

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getAuthorName() {
    return authorName;
  }

  public void setAuthorName(String authorName) {
    this.authorName = authorName;
  }

  public String getDependencies() {
    return dependencies;
  }

  public void setDependencies(String dependencies) {
    this.dependencies = dependencies == null ? "" : dependencies;
  }

  public String getProperties() {
    return properties;
  }

  public void setProperties(String properties) {
    this.properties = properties == null ? "" : properties;
  }

  public String getHeadIncludeFile() {
    return headIncludeFile;
  }

  public void setHeadIncludeFile(String headIncludeFile) {
    this.headIncludeFile = headIncludeFile == null ? "" : headIncludeFile;
  }

  public ArrayList<String> getDirectories() {
    return directories;
  }

  public ArrayList<String> getInstallFiles() {
    return installFiles;
  }

  public String getInstallFilesAsString() {
    StringBuilder sb = new StringBuilder();
    for (String name : installFiles) {
      if (sb.length() > 0)
        sb.append(';');
      sb.append(name);
    }
    return sb.toString();
  }

  public void setInstallFilesFromString(String s) {
    installFiles.clear();
    if (s == null)
      return;
    StringTokenizer stk = new StringTokenizer(s, ";");
    while (stk.hasMoreTokens())
      installFiles.add(stk.nextToken());
  }

  public HashMap<String, byte[]> getFiles() {
    return files;
  }

  public String getInstallLog() {
    return installLog;
  }

  public void setInstallLog(String installLog) {
    this.installLog = installLog;
  }

  public boolean readPackage(byte[] importPackage) {
    if (importPackage == null)
      return false;
    directories.clear();
    files.clear();
    try {
      ByteArrayInputStream in = new ByteArrayInputStream(importPackage);
      ZipInputStream zin = new ZipInputStream(in);
      ZipEntry entry;
      while ((entry = zin.getNextEntry()) != null) {
        readEntry(entry, zin);
        zin.closeEntry();
      }
      zin.close();
    } catch (IOException ioe) {
      return false;
    }
    return true;
  }

  protected void readEntry(ZipEntry entry, ZipInputStream zin) throws IOException {
    String name = entry.getName();
    if (entry.isDirectory()) {
      directories.add(name);
    } else {
      byte[] bytes = ZipHelper.getFile(zin);
      installFiles.add(name);
      files.put(name, bytes);
    }
  }

  public boolean checkDependencies(ModuleData oldData) {
    setBeingCreated(oldData == null);
    String dependenciesString = getDependencies();
    if (StringHelper.isNullOrEmtpy(dependenciesString))
      return true;
    String[] dependencies = dependenciesString.split(",");
    for (String dep : dependencies) {
      if (ModuleCache.getInstance().getModule(dep) == null)
        return false;
    }
    return true;
  }

}