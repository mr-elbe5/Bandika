/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.resources;

import de.net25.base.SizedData;

import java.io.*;

/**
 * Class FileData is a data class holding file data (name, bytes, content type). <br>
 * Usage:
 */
public class FileData extends SizedData {

  protected String name = null;
  protected String contentType = null;
  protected byte[] bytes = null;

  /**
   * Method getName returns the name of this FileData object.
   *
   * @return the name (type String) of this FileData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method getExtension returns the extension of this FileData object.
   *
   * @return the extension (type String) of this FileData object.
   */
  public String getExtension() {
    if (name == null)
      return null;
    int pos = name.lastIndexOf(".");
    if (pos == -1)
      return null;
    return name.substring(pos + 1).toLowerCase();
  }

  /**
   * Method setName sets the name of this FileData object.
   *
   * @param name the name of this FileData object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getContentType returns the contentType of this FileData object.
   *
   * @return the contentType (type String) of this FileData object.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Method setContentType sets the contentType of this FileData object.
   *
   * @param contentType the contentType of this FileData object.
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Method getBytes returns the bytes of this FileData object.
   *
   * @return the bytes (type byte[]) of this FileData object.
   */
  public byte[] getBytes() {
    return bytes;
  }

  /**
   * Method setBytes sets the bytes of this FileData object.
   *
   * @param bytes the bytes of this FileData object.
   */
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  /**
   * Method writeFile
   *
   * @param fdata    of type FileData
   * @param basePath of type String
   * @return boolean
   */
  public static boolean writeFile(FileData fdata, String basePath) {
    if (!basePath.endsWith("/"))
      basePath = basePath + "/";
    try {
      File f = new File(basePath + fdata.getName());
      if (f.exists())
        f.delete();
      f.createNewFile();
      FileOutputStream fout = new FileOutputStream(f);
      fout.write(fdata.getBytes());
      fout.flush();
      fout.close();
    }
    catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Method readFile
   *
   * @param fdata    of type FileData
   * @param basePath of type String
   * @return boolean
   */
  public static boolean readFile(FileData fdata, String basePath) {
    if (!basePath.endsWith("/"))
      basePath = basePath + "/";
    try {
      File f = new File(basePath + fdata.getName());
      if (!f.exists())
        return false;
      FileInputStream fin = new FileInputStream(f);
      ByteArrayOutputStream bout = new ByteArrayOutputStream();
      byte[] bytes = new byte[4096];
      int len = 4096;
      while (len > 0) {
        len = fin.read(bytes, 0, 4096);
        if (len > 0)
          bout.write(bytes, 0, len);
      }
      fin.close();
      fdata.setBytes(bout.toByteArray());
    }
    catch (IOException e) {
      return false;
    }
    return true;
  }

  /**
   * Method deleteFile
   *
   * @param fdata    of type FileData
   * @param basePath of type String
   */
  public static void deleteFile(FileData fdata, String basePath) {
    if (!basePath.endsWith("/"))
      basePath = basePath + "/";
    File f = new File(basePath + fdata.getName());
    if (f.exists())
      f.delete();
  }

}
