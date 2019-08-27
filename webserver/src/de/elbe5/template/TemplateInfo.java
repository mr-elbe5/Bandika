/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.template;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TemplateInfo {

    private String type;
    private String key;
    private List<String> tagLibs = new ArrayList<>();

    public TemplateInfo(String type, String key, String[] tagLibs) {
        this.type = type;
        this.key=key;
        if (tagLibs!=null){
            Collections.addAll(this.tagLibs, tagLibs);
        }

    }

    public String getType() {
        return type;
    }

    public String getKey() {
        return key;
    }

    public String getTagLibsString() {
        StringBuilder sb=new StringBuilder();
        for (String tagLib : tagLibs){
            sb.append("<%@ taglib uri=\"/WEB-INF/").append(tagLib).append("tags.tld\" prefix=\"").append(tagLib).append("\" %>\n");
        }
        return sb.toString();
    }
}
