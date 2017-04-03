/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika._base.BaseData;
import de.bandika.application.Configuration;

public class TemplateTypeData extends BaseData {

  public final static String DATAKEY = "data|templatetype";

  protected String name;
  protected String moduleName;
  protected String templatePath;
  protected int templateLevel;

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

  public String getTemplatePath() {
    return templatePath;
  }

  public String getFullTemplatePath() {
    String basePath = Configuration.getBasePath();
    if (basePath.endsWith("/"))
      basePath = basePath.substring(0, basePath.length() - 1);
    return basePath + templatePath;
  }

  public void setTemplatePath(String templatePath) {
    this.templatePath = templatePath;
  }

  public int getTemplateLevel() {
    return templateLevel;
  }

  public void setTemplateLevel(int templateLevel) {
    this.templateLevel = templateLevel;
  }
}
