/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.template;

import de.bandika.data.StatefulBaseData;
import de.bandika.data.StringFormat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LayoutTemplateData extends StatefulBaseData {

    protected String name = "";
    protected String description = "";
    protected String className = "";
    protected String code = null;

    protected Map<String,List<String>> partTemplates=new HashMap<>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Map<String, List<String>> getPartTemplates() {
        return partTemplates;
    }

    public void setPartTemplates(Map<String, List<String>> partTemplates) {
        this.partTemplates = partTemplates;
    }

    public boolean isComplete() {
        return isComplete(name) && !(isNew() && StringFormat.isNullOrEmtpy(code));
    }
}