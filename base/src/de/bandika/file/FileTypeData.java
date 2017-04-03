/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.BaseData;
import de.bandika._base.Logger;

public class FileTypeData extends BaseData {

  public final static String DATAKEY = "data|filetype";

  protected String name;
  protected String moduleName;
  protected String className;
  protected String contentTypePattern;
  protected boolean dimensioned;
  protected Class cls;


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getModuleName() {
    return moduleName;
  }

  public void setModuleName(String moduleName) {
    this.moduleName = moduleName;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getContentTypePattern() {
    return contentTypePattern;
  }

  public void setContentTypePattern(String contentTypePattern) {
    this.contentTypePattern = contentTypePattern;
  }

  public boolean isDimensioned() {
    return dimensioned;
  }

  public void setDimensioned(boolean dimensioned) {
    this.dimensioned = dimensioned;
  }

  public Class getCls() {
    return cls;
  }

  public void setCls() {
    if (className != null && !className.equals("")) {
      try {
        cls = Class.forName(className);
      } catch (Exception e) {
        Logger.warn(getClass(), "could not load class " + className + "for file type " + getName());
      }
    }
  }

  public LinkedFileData getFileData() {
    LinkedFileData file = null;
    try {
      file = (LinkedFileData) cls.newInstance();
      file.setType(getName());
    } catch (Exception e) {
      Logger.warn(getClass(), "could not create file of type " + getName());
    }
    return file;
  }
}