/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.search;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.site.SiteData;
import de.bandika.cms.tree.TreeCache;

import java.util.Locale;

public class SiteSearchData extends ContentSearchData {

    public static final String TYPE = "site";

    public String getIconSpan(Locale locale) {
        return "<span class=\"searchInfo\"><a class=\"icn isite\" title=\"" + StringUtil.getHtml("_site", locale) + "\"></a></span>";
    }

    public String getInfoSpan(Locale locale) {
        return "<span class=\"icn iinfo searchInfo\" href=\"\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_site", locale) + "', '/search.ajx?act=showSiteSearchDetails&siteId=" + getId() + "');\">&nbsp;</a></span>";
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
        authorName = doc.get("authorName");
        SiteData siteData = TreeCache.getInstance().getSite(getId());
        if (siteData != null)
            setUrl(siteData.getUrl());
    }

}

