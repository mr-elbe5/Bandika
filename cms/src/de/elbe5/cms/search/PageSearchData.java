/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.search;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.tree.TreeCache;

import java.util.Locale;

public class PageSearchData extends ContentSearchData {

    public static final String TYPE = "page";

    public String getIconSpan(Locale locale) {
        return "<span class=\"icn ipage\" title=\"" + StringUtil.getHtml("_page", locale) + "\"></span>";
    }

    public String getInfoSpan(Locale locale) {
        return "<span class=\"searchInfo\"><a class=\"icn iinfo\" href=\"\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_page", locale) + "', '/search.ajx?act=showPageSearchDetails&pageId=" + getId() + "');\">&nbsp;</a></span>";
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
        PageData pageData = TreeCache.getInstance().getPage(getId());
        if (pageData != null)
            setUrl(pageData.getUrl());
    }

}

