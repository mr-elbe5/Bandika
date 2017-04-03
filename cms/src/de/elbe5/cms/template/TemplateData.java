/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.template;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.DataProperties;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TemplateData extends BaseData implements Serializable {

    public static final String TYPE_NONE="";
    public static final String TYPE_MASTER="_master";
    public static final String TYPE_PAGE="_page";
    public static final String TYPE_PART="_part";

    public static final String USAGE_ALL="all";

    protected String type=TYPE_NONE;
    protected String fileName = "";
    protected String displayName = "";
    protected String description = "";
    protected String className = "";
    protected String usage = "";

    protected String code="";

    protected List<String> usageList = new ArrayList<>();

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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

    public String getUsage() {
        return usage;
    }

    public boolean hasUsage(String usage) {
        return usageList.contains(USAGE_ALL) || usageList.contains(usage);
    }

    public void setUsage(String usage) {
        this.usage = usage;
        usageList.clear();
        String[] arr = usage.split(",");
        for (String areaType : arr)
            if (!areaType.isEmpty()) usageList.add(areaType);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    protected void fillProperties(DataProperties properties, Locale locale){
        properties.setKeyHeader("_template", locale);
        properties.addKeyProperty("_name", getFileName(),locale);
        properties.addKeyProperty("_description", getDescription(),locale);
        properties.addKeyProperty("_className", getClassName(),locale);
        properties.addKeyProperty("_usage", getUsage(),locale);
    }

}