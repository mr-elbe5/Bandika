/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.base.data.BaseData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.servlet.SessionReader;
import de.bandika.cms.templatecontrol.TemplateControl;
import de.bandika.cms.templatecontrol.TemplateControls;

import javax.servlet.http.HttpServletRequest;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class TemplateData extends BaseData implements Serializable {

    public static final String USAGE_ALL = "all";

    public enum TagType {
        CONTENT("<cms-content", "</cms-content>"),
        PART("<cms-part", "</cms-part>"),
        FIELD("<cms-field", "</cms-field>"),
        CONTROL("<cms-control", "</cms-control>"),
        SECTION("<cms-section", "</cms-section>"),
        SNIPPET("<cms-snippet", "</cms-snippet>"),
        RESOURCE("<cms-res", "</cms-res>"),
        PARTID("<cms-pid", "</cms-pid>"),
        CONTAINERID("<cms-cid", "</cms-cid>");

        private String startTag;
        private String endTag;

        TagType(String startTag, String endTag) {
            this.startTag = startTag;
            this.endTag = endTag;
        }

        public String getStartTag() {
            return startTag;
        }

        public String getEndTag() {
            return endTag;
        }
    }

    public static final String TAG_START = "<cms-";

    public static final String PLACEHOLDER_CONTAINERID = "<cms-cid/>";
    public static final String PLACEHOLDER_PARTS = "<cms-parts/>";

    protected TemplateType type = TemplateType.NONE;
    // can be null!
    protected TemplateDataType dataType = null;
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String usage = "";
    protected String code = "";

    public TemplateData(TemplateDataType dataType){
        this.dataType = dataType;
    }

    public TemplateType getType() {
        return type;
    }

    public TemplateDataType getDataType() {
        return dataType;
    }

    public String getDataTypeName() {
        return dataType==null ? "" : dataType.name();
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

    /****** parser part ****/

    protected TagType getTagType(String src) throws ParseException {
        int blankPos = src.indexOf(" ");
        if (blankPos != -1)
            src = src.substring(0, blankPos);
        switch (src) {
            case "content":
                return TagType.CONTENT;
            case "part":
                return TagType.PART;
            case "field":
                return TagType.FIELD;
            case "control":
                return TagType.CONTROL;
            case "section":
                return TagType.SECTION;
            case "snippet":
                return TagType.SNIPPET;
            case "res":
                return TagType.RESOURCE;
            case "pid":
                return TagType.PARTID;
        }
        throw new ParseException("bad cms tag: " + src, 0);
    }

    public void fillTemplate(StringBuilder sb, PageData pageData, PagePartData partData, HttpServletRequest request) throws ParseException {
        String src = getCode();
        fillTemplate(src, sb, pageData, partData, request);
    }

    public void fillTemplate(String src, StringBuilder sb, PageData pageData, PagePartData partData, HttpServletRequest request) throws ParseException {
        int pos1;
        int pos2 = 0;
        boolean shortTag;
        while (true) {
            pos1 = src.indexOf(TAG_START, pos2);
            if (pos1 == -1) {
                sb.append(src.substring(pos2));
                break;
            }
            sb.append(src.substring(pos2, pos1));
            pos2 = src.indexOf('>', pos1 + 5);
            if (pos2 == -1)
                throw new ParseException("no cms tag end", pos1);
            String startTag = src.substring(pos1, pos2);
            shortTag = false;
            if (startTag.endsWith("/")) {
                startTag = startTag.substring(0, startTag.length() - 1);
                shortTag = true;
            }
            TagType tagType = getTagType(startTag.substring(5));
            TemplateAttributes attributes = new TemplateAttributes(startTag.substring(tagType.getStartTag().length()).trim());
            //no content
            if (shortTag) {
                appendTagReplacement(sb, tagType, attributes, "", pageData, partData, request);
                pos2++;
                continue;
            }
            pos2++;
            pos1 = src.indexOf(tagType.getEndTag(), pos2);
            if (pos1 == -1)
                throw new ParseException("no cms end tag ", pos2);
            String content = src.substring(pos2, pos1).trim();
            pos2 = pos1 + tagType.getEndTag().length();
            appendTagReplacement(sb, tagType, attributes, content, pageData, partData, request);
        }
    }

    protected boolean appendTagReplacement(StringBuilder sb, TagType tagType, TemplateAttributes attributes, String content, PageData pageData, PagePartData partData, HttpServletRequest request) {
        switch (tagType) {
            case CONTROL:
                TemplateControl control = TemplateControls.getControl(attributes.get("type"));
                if (control != null)
                    control.appendHtml(sb, attributes, content, pageData, request);
                return true;
            case SNIPPET:
                TemplateData snippet = TemplateCache.getInstance().getTemplate(TemplateType.SNIPPET, attributes.getString("name"));
                if (snippet != null) {
                    try {
                        snippet.fillTemplate(sb, pageData, null, request);
                    } catch (Exception e) {
                        Log.error("error in snippet template", e);
                    }
                }
                return true;
            case RESOURCE:
                sb.append(StringUtil.getHtml(attributes.get("key"), SessionReader.getSessionLocale(request)));
                return true;
        }
        return false;
    }

}
