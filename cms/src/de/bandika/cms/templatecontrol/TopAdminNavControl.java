/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templatecontrol;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.tree.TreeNode;
import de.bandika.rights.Right;
import de.bandika.servlet.SessionReader;
import de.bandika.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.Locale;

public class TopAdminNavControl extends TemplateControl {

    public static String KEY = "topAdminNav";

    private static TopAdminNavControl instance = null;

    public static TopAdminNavControl getInstance() {
        if (instance == null)
            instance = new TopAdminNavControl();
        return instance;
    }

    public void appendHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String content, PageData pageData) throws IOException {
        Locale locale = SessionReader.getSessionLocale(request);
        int siteId = pageData == null ? 0 : pageData.getParentId();
        int pageId = pageData == null ? 0 : pageData.getId();
        boolean editMode = pageData != null && pageData.isEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
        writer.write("<nav><ul>");
        if (editMode & hasEditRight) {
            writer.write("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContent&pageId=" + pageId + "\">" + getHtml("_save", locale) + "</a></li>");

            if (hasApproveRight) {
                writer.write("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContentAndPublish&pageId=" + pageId + "\">" + getHtml("_publish", locale) + "</a></li>");
            }
            writer.write("<li class=\"edit\"><a href=\"/page.srv?act=stopEditing&pageId=" + pageId + "\">" + getHtml("_cancel", locale) + "</a></li>");
        } else {
            writer.write("<li class=\"admin\"><a href=\"javascript:window.print();\" title=\"" + getHtml("_print", locale) + "\"><span class=\"icn iprint\"></span></a></li>");
            if (pageId != 0 && hasEditRight) {
                writer.write("<li class=\"admin\"><a href=\"/page.srv?act=openEditPageContent&pageId=" + pageId + "\" title=\"" + getHtml("_editPage", locale) + "\"><span class=\"icn iedit\"></span></a></li>");
            }
            if (pageId != 0 && hasApproveRight) {
                if (pageData.getDraftVersion() != 0) {
                    writer.write("<li class=\"admin\"><a href=\"/page.srv?act=publishPage&pageId=" + pageId + "\" title=\"" + getHtml("_publish", locale) + "\"><span class=\"icn ipublish\"></span></a></li>");
                }
            }
            if (hasAnyEditRight) {
                writer.write("<li class=\"admin\"><a href=\"#\" onclick=\"return openTreeLayer('" + StringUtil.getHtml("_tree") + "', '" + "/tree.ajx?act=openTree&siteId=" + siteId + "&pageId=" + pageId + "');\" title=\"" + getHtml("_tree", locale) + "\"><span class=\"icn isite\"></span></a></li>");
            }
            if (hasAdminRight) {
                writer.write("<li class=\"admin\"><a href=\"/admin.srv?act=openAdministration&siteId=" + siteId + "&pageId=" + pageId + "\" title=\"" + getHtml("_administration", locale) + "\"><span class=\"icn isetting\"></span></a></li>");
            }
        }
        writer.write("</ul></nav>");
    }


}
