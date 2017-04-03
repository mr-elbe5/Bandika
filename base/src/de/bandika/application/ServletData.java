/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.application;

import de.bandika._base.BaseData;

public class ServletData extends BaseData {

  public final static String DATAKEY = "data|servlet";

  protected String name;
  protected String className;
  protected String pattern;
  protected int startUp = 0;

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getClassName() {
    return className;
  }

  public void setClassName(String className) {
    this.className = className;
  }

  public String getPattern() {
    return pattern;
  }

  public void setPattern(String pattern) {
    this.pattern = pattern;
  }

  public int getStartUp() {
    return startUp;
  }

  public void setStartUp(int startUp) {
    this.startUp = startUp;
  }

  public String getServletString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<servlet><servlet-name>")
      .append(getName())
      .append("</servlet-name><servlet-class>")
      .append(getClassName())
      .append("</servlet-class>");
    if (startUp > 0)
      sb.append("<load-on-startup>").append(startUp).append("</load-on-startup>");
    sb.append("</servlet>\n");
    return sb.toString();
  }

  public String getServletMappingString() {
    StringBuilder sb = new StringBuilder();
    sb.append("<servlet-mapping><servlet-name>")
      .append(getName())
      .append("</servlet-name><url-pattern>")
      .append(getPattern())
      .append("</url-pattern></servlet-mapping>\n");
    return sb.toString();
  }
}