/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.cms.tree.TreeCache;
import de.bandika.cms.tree.TreeNode;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class BreadcrumbControl extends TemplateInclude {

    public static final String KEY = "breadcrumb";

    private static BreadcrumbControl instance = null;

    public static BreadcrumbControl getInstance() {
        if (instance == null)
            instance = new BreadcrumbControl();
        return instance;
    }

    public boolean isDynamic(){
        return false;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        TreeCache tc = TreeCache.getInstance();
        List<Integer> activeIds = new ArrayList<>();
        if (outputData.pageData != null) {
            activeIds.addAll(outputData.pageData.getParentIds());
            activeIds.add(outputData.pageData.getId());
        }
        else {
            int homeId=TreeCache.getInstance().getLanguageRootSiteId(outputData.locale);
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
            writer.write("<li><a href=\"{1}\">{2}</a></li>",
                    bcnode.getUrl(),
                    toHtml(bcnode.getDisplayName()));
        }
        writer.write("</ul></nav>");
    }

}
