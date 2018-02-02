/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.file.FileData;
import de.bandika.cms.tree.TreeCache;

import java.util.Locale;

public class FileSearchData extends ContentSearchData {

    public static final String TYPE = "file";

    public String getIconSpan(Locale locale) {
        return "<span class=\"icn ifile\" title=\"" + StringUtil.getHtml("_file", locale) + "\"></span>";
    }

    public String getInfoSpan(Locale locale) {
        return "<span class=\"searchInfo\"><a class=\"icn iinfo\" href=\"\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_file", locale) + "', '/search.ajx?act=showFileSearchDetails&fileId=" + getId() + "');\">&nbsp;</a></span>";
    }

    public String getNameSpan(Locale locale) {
        if (getUrl().isEmpty())
            return "<span>" + getNameContext() + "</span>";
        return "<span><a href=\"" + getUrl() + "\" target=\"_blank\" title=\"" + StringUtil.getHtml("_show", locale) + "\">" + getNameContext() + "</a></span>";
    }

    public String getType() {
        return TYPE;
    }

    public void evaluateDoc() {
        if (doc == null)
            return;
        id = Integer.parseInt(doc.get("id"));
        name = doc.get("name");
        description = doc.get("description");
        keywords = doc.get("keywords");
        authorName = doc.get("authorName");
        content = doc.get("content");
        FileData fileData = TreeCache.getInstance().getFile(getId());
        if (fileData != null)
            setUrl(fileData.getUrl());
    }
}
