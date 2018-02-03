/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.data.BaseData;
import de.bandika.base.log.Log;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PagePartData;
import de.bandika.cms.templateinclude.TemplateInclude;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;


public class TemplateData extends BaseData implements Serializable {

    public static final String TYPE_MASTER = "MASTER";
    public static final String TYPE_PAGE = "PAGE";
    public static final String TYPE_PART = "PART";
    public static final String TYPE_SNIPPET = "SNIPPET";

    public static final String USAGE_ALL = "all";

    protected String type = "";
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String usage = "";
    protected String code = "";

    protected List<TemplatePart> templateParts = null;
    protected List<TemplateInclude> templateIncludes = new ArrayList<>();

    public TemplateData() {
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    protected List<String> usageList = new ArrayList<>();

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
        for (String usageName : arr) {
            if (!usageName.isEmpty()) {
                usageList.add(usageName);
            }
        }
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<TemplateInclude> getTemplateIncludes() {
        return templateIncludes;
    }

    /****** parser part ****/

    public boolean parseTemplate() {
        try {
            if (templateParts == null)
                templateParts = new ArrayList<>();
            else
                templateParts.clear();
            int pos1;
            int pos2 = 0;
            boolean shortTag;
            while (true) {
                pos1 = code.indexOf(TemplateTagType.TAG_START, pos2);
                if (pos1 == -1) {
                    templateParts.add(new TemplateHtmlPart(code.substring(pos2)));
                    break;
                }
                templateParts.add(new TemplateHtmlPart(code.substring(pos2, pos1)));
                pos2 = code.indexOf('>', pos1 + 5);
                if (pos2 == -1)
                    throw new ParseException("no cms tag end", pos1);
                String startTag = code.substring(pos1, pos2);
                shortTag = false;
                if (startTag.endsWith("/")) {
                    startTag = startTag.substring(0, startTag.length() - 1);
                    shortTag = true;
                }
                TemplateTagType tagType = TemplateTagType.getTagType(startTag);
                String attributesString = startTag.substring(tagType.getStartTag().length()).trim();
                //no content
                if (shortTag) {
                    templateParts.add(getNewTemplateTagPart(tagType, "", attributesString));
                    pos2++;
                    continue;
                }
                pos2++;
                pos1 = code.indexOf(tagType.getEndTag(), pos2);
                if (pos1 == -1)
                    throw new ParseException("no cms end tag ", pos2);
                String content = code.substring(pos2, pos1).trim();
                pos2 = pos1 + tagType.getEndTag().length();
                templateParts.add(getNewTemplateTagPart(tagType, content, attributesString));
            }
        } catch (ParseException e) {
            templateParts.clear();
            Log.error("parse error for template " + getName(), e);
            return false;
        }
        return true;
    }

    protected TemplateTagPart getNewTemplateTagPart(TemplateTagType tagType, String content, String attributeString) {
        return new TemplateTagPart(tagType, content, attributeString);
    }

    public void writeTemplate(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData) throws IOException {
        for (TemplatePart templatePart : templateParts) {
            templatePart.writeTemplatePart(context, writer, request, pageData, partData);
        }
    }

}
