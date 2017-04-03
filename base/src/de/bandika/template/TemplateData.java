/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika._base.BaseData;
import de.bandika._base.DataHelper;
import de.bandika._base.StringHelper;

import java.util.ArrayList;
import java.util.StringTokenizer;

public class TemplateData extends BaseData {

  public static String DATAKEY = "data|template";

  protected String name = "";
  protected String typeName = "";
  protected String matchTypes = "";
  protected String description = "";
  protected String className = "";
  protected String code=null;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getTypeName() {
    return typeName;
  }

  public void setTypeName(String typeName) {
    this.typeName = typeName;
  }

  public String getMatchTypes() {
    return matchTypes;
  }

  public ArrayList<String> getMatchTypeList() {
    ArrayList<String> arr = new ArrayList<String>();
    StringTokenizer stk = new StringTokenizer(matchTypes, ",");
    while (stk.hasMoreTokens())
      arr.add(stk.nextToken());
    return arr;
  }

  public void setMatchTypes(String matchTypes) {
    this.matchTypes = matchTypes;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public boolean isComplete() {
    return DataHelper.isComplete(name) && DataHelper.isComplete(typeName) && !(isBeingCreated() && StringHelper.isNullOrEmtpy(code));
  }
}