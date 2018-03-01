/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template.part;

import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.template.TemplateInclude;
import de.bandika.webbase.util.TagAttributes;

import java.util.HashSet;
import java.util.Set;

public abstract class TemplatePart extends TemplateInclude {

    public static Set<String> IGNORE_ATTRIBUTES = new HashSet<>();

    static{
        IGNORE_ATTRIBUTES.add("type");
        IGNORE_ATTRIBUTES.add("name");
    }

    protected String content="";
    protected TagAttributes attributes;

    public boolean isDynamic(){
        return false;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TagAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(TagAttributes attributes) {
        this.attributes = attributes;
    }

    public String getPlaceholder(){
        StringBuilder sb=new StringBuilder();
        sb.append("{<include type=\"").append(getKey()).append("\"");
        for (String key : attributes.keySet()){
            if (IGNORE_ATTRIBUTES.contains(key))
                continue;
            sb.append(" ").append(key).append("=\"").append(attributes.getString(key)).append("\"");
        }
        sb.append(" />}");
        return sb.toString();
    }

    public void completeOutputData(PageOutputData outputData){
        outputData.attributes=attributes;
        outputData.content=content;
    }

}