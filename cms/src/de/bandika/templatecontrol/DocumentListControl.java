/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.file.FileData;
import de.bandika.page.PageData;
import de.bandika.rights.Right;
import de.bandika.servlet.RightsReader;
import de.bandika.servlet.SessionReader;
import de.bandika.site.SiteData;
import de.bandika.template.TemplateAttributes;
import de.bandika.tree.TreeCache;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Locale;

public class DocumentListControl extends TemplateControl {

    public static String KEY = "documents";

    private static DocumentListControl instance = null;

    public static DocumentListControl getInstance() {
        if (instance == null)
            instance = new DocumentListControl();
        return instance;
    }

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request){
        int siteId=pageData.getParentId();
        SiteData site = TreeCache.getInstance().getSite(siteId);
        Locale locale=SessionReader.getSessionLocale(request);
        List<FileData> files=site.getFiles();
        for (FileData file : files){
            if (!file.isAnonymous() && !RightsReader.hasContentRight(request, file.getId(), Right.READ))
                continue;
            sb.append("<div class=\"documentListLine icn ifile\"><a href=\"").append(file.getUrl()).append("\" target=\"_blank\" title=\"")
                    .append(StringUtil.getHtml("_show", locale)).append("\">").append(StringUtil.toHtml(file.getDisplayName()))
                    .append("</a></div>");
        }
    }

}
