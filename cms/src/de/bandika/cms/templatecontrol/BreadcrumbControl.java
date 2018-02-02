/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.cms.page.PageData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;
import de.bandika.webbase.servlet.SessionReader;
import de.bandika.webbase.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbControl extends TemplateControl {

    public static final String KEY = "breadcrumb";

    private static BreadcrumbControl instance = null;

    public static BreadcrumbControl getInstance() {
        if (instance == null)
            instance = new BreadcrumbControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        TreeCache tc = TreeCache.getInstance();
        List<Integer> activeIds = new ArrayList<>();
        if (pageData != null) {
            activeIds.addAll(pageData.getParentIds());
            activeIds.add(pageData.getId());
        }
        else {
            int homeId=TreeCache.getInstance().getLanguageRootSiteId(SessionReader.getSessionLocale(request));
            if (homeId!=0) {
                activeIds.add(TreeNode.ID_ROOT);
                activeIds.add(homeId);
            }
        }
        writer.write("<nav class=\"breadcrumb\"><ul>");
        for (int i = 1; i < activeIds.size(); i++) {
            TreeNode bcnode = tc.getNode(activeIds.get(i));
            if (bcnode == null || ((bcnode instanceof PageData) && ((PageData) bcnode).isDefaultPage())) {
                continue;
            }
            writer.write("<li><a href=\"" + bcnode.getUrl() + "\">" + toHtml(bcnode.getDisplayName()) + "</a></li>");
        }
        writer.write("</ul></nav>");
    }

}
