/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import java.text.ParseException;

public enum TagType {
    CONTENT("content"),
    PART("part"),
    FIELD("field"),
    CONTROL("control"),
    SECTION("section"),
    SNIPPET("snippet"),
    RESOURCE("res"),
    PARTID("pid"),
    CONTAINERID("cid");

    public static final String TAG_START = "<cms-";
    public static final String TAGEND_START = "</cms-";
    public static final String TAG_END = ">";

    private String startTag;
    private String endTag;

    TagType(String tagString) {
        this.startTag = TAG_START+tagString;
        this.endTag = TAGEND_START + tagString + TAG_END;
    }

    public String getStartTag() {
        return startTag;
    }

    public String getEndTag() {
        return endTag;
    }

    public static TagType getTagType(String src) throws ParseException {
        int blankPos = src.indexOf(" ");
        if (blankPos != -1)
            src = src.substring(0, blankPos);
        for (TagType tagType: TagType.values()){
            if (tagType.getStartTag().equals(src))
                return tagType;
        }
        throw new ParseException("bad cms tag: " + src, 0);
    }
}
