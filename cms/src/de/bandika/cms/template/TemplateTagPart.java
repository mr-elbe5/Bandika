/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class TemplateTagPart extends TemplatePart {

    TagType tagType;
    protected String content="";
    protected Map<String, String> attributes=new HashMap<>();

    public TemplateTagPart(TagType tagType, String content, String attributeString){
        this.tagType=tagType;
        this.content=content;
        setAttributes(attributeString);
    }

    protected void setAttributes(String src) {
        attributes.clear();
        boolean inString = false;
        char ch;
        int lastBlank = 0;
        for (int i = 0; i < src.length(); i++) {
            ch = src.charAt(i);
            if (ch == '\"')
                inString = !inString;
            if ((ch == ' ' && !inString) || i == src.length() - 1) {
                String attributesString = (i == src.length() - 1 ? src.substring(lastBlank) : src.substring(lastBlank, i));
                int pos = attributesString.indexOf('=');
                if (pos != -1) {
                    String key = attributesString.substring(0, pos).trim();
                    String value = attributesString.substring(pos + 1).trim();
                    if (value.startsWith("\""))
                        value = value.substring(1);
                    if (value.endsWith("\""))
                        value = value.substring(0, value.length() - 1);
                    attributes.put(key, value.trim());
                }
                lastBlank = i;
            }
        }
    }

    public String getStringAttribute(String key) {
        String value = attributes.get(key);
        if (value != null)
            return value;
        return "";
    }

    public int getIntAttribute(String key) {
        int value = -1;
        try {
            value = Integer.parseInt(attributes.get(key));
        } catch (Exception ignore) {
        }
        return value;
    }

    public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData) throws IOException {
        writer.write(content);
    }

}
