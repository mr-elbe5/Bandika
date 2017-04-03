/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.io.*;
import java.util.ArrayList;

/**
 * Class FileHelper is a helper data class for handling files. <br>
 * Usage:
 */
public class FileHelper {

  public static boolean fileExists(String path) {
    File f = new File(path);
    return f.exists();
  }

  public static boolean writeFile(String path, byte[] bytes) {
    try {
      File f = new File(path);
      if (f.exists() && !f.delete())
        return false;
      if (!f.createNewFile())
        return false;
      FileOutputStream fout = new FileOutputStream(f);
      fout.write(bytes);
      fout.flush();
      fout.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public static boolean writeFile(String path, String content) {
    try {
      File f = new File(path);
      if (f.exists() && !f.delete())
        return false;
      if (!f.createNewFile())
        return false;
      FileWriter fout = new FileWriter(f);
      fout.write(content);
      fout.flush();
      fout.close();
    } catch (IOException e) {
      return false;
    }
    return true;
  }

  public static String readTextFile(String path) {
    StringBuilder sb = new StringBuilder();
    try {
      File f = new File(path);
      if (!f.exists())
        return "";
      FileReader freader = new FileReader(f);
      char[] chars = new char[4096];
      int len = 4096;
      while (len > 0) {
        len = freader.read(chars, 0, 4096);
        if (len > 0)
          sb.append(chars, 0, len);
      }
      freader.close();
    } catch (IOException e) {
      return "";
    }
    return sb.toString();
  }

  public static byte[] readBinaryFile(String path) {
    ByteArrayOutputStream sout = new ByteArrayOutputStream();
    try {
      File f = new File(path);
      if (!f.exists())
        return null;
      FileInputStream fin = new FileInputStream(f);
      byte[] bytes = new byte[4096];
      int len = 4096;
      while (len > 0) {
        len = fin.read(bytes, 0, 4096);
        if (len > 0)
          sout.write(bytes, 0, len);
      }
      sout.flush();
      fin.close();
    } catch (IOException e) {
      return null;
    }
    return sout.toByteArray();
  }

  public static boolean ensureFolder(String path) {
    File f = new File(path);
    if (f.exists())
      return f.isDirectory();
    f.mkdirs();
    return true;
  }

  public static ArrayList<String> getSubFolders(String path) {
    ArrayList<String> folders = new ArrayList<String>();
    File f = new File(path);
    for (File file : f.listFiles()) {
      if (file.isDirectory())
        folders.add(path + '/' + file.getName());
    }
    return folders;
  }

  public static void removeFolder(String path) {
    File f = new File(path);
    if (f.exists())
      f.delete();
  }

}