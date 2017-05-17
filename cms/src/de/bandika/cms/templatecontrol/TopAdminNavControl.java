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
import de.bandika.cms.template.TemplateAttributes;
import de.bandika.cms.tree.TreeNode;
import de.bandika.rights.Right;
import de.bandika.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
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

    public void appendHtml(StringBuilder sb, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) {
        Locale locale = SessionReader.getSessionLocale(request);
        int siteId = pageData == null ? 0 : pageData.getParentId();
        int pageId = pageData == null ? 0 : pageData.getId();
        boolean editMode = pageData != null && pageData.isEditMode();
        boolean hasAnyEditRight = SessionReader.hasAnyContentRight(request);
        boolean hasEditRight = SessionReader.hasContentRight(request, pageId, Right.EDIT);
        boolean hasAdminRight = SessionReader.hasAnyElevatedSystemRight(request) || SessionReader.hasContentRight(request, TreeNode.ID_ALL, Right.EDIT);
        boolean hasApproveRight = SessionReader.hasContentRight(request, pageId, Right.APPROVE);
        sb.append("<nav><ul>");
        if (editMode & hasEditRight) {
            sb.append("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContent&pageId=").append(pageId).append("\">").append(getHtml("_save", locale)).append("</a></li>");

            if (hasApproveRight) {
                sb.append("<li class=\"editControl\"><a href=\"/page.srv?act=savePageContentAndPublish&pageId=").append(pageId).append("\">").append(getHtml("_publish", locale)).append("</a></li>");
            }
            sb.append("<li class=\"edit\"><a href=\"/page.srv?act=stopEditing&pageId=").append(pageId).append("\">").append(getHtml("_cancel", locale)).append("</a></li>");
        } else {
            sb.append("<li class=\"admin\"><a href=\"javascript:window.print();\" title=\"").append(getHtml("_print", locale)).append("\"><span class=\"icn iprint\"></span></a></li>");
            if (pageId != 0 && hasEditRight) {
                sb.append("<li class=\"admin\"><a href=\"/page.srv?act=openEditPageContent&pageId=").append(pageId).append("\" title=\"").append(getHtml("_editPage", locale)).append("\"><span class=\"icn iedit\"></span></a></li>");
            }
            if (pageId != 0 && hasApproveRight) {
                if (pageData.getDraftVersion() != 0) {
                    sb.append("<li class=\"admin\"><a href=\"/page.srv?act=publishPage&pageId=").append(pageId).append("\" title=\"").append(getHtml("_publish", locale)).append("\"><span class=\"icn ipublish\"></span></a></li>");
                }
            }
            if (hasAnyEditRight) {
                sb.append("<li class=\"admin\"><a href=\"#\" onclick=\"return openTreeLayer('").append(StringUtil.getHtml("_tree")).append("', '").append("/tree.ajx?act=openTree&siteId=").append(siteId).append("&pageId=").append(pageId).append("');\" title=\"").append(getHtml("_tree", locale)).append("\"><span class=\"icn isite\"></span></a></li>");
            }
            if (hasAdminRight) {
                sb.append("<li class=\"admin\"><a href=\"/admin.srv?act=openAdministration&siteId=").append(siteId).append("&pageId=").append(pageId).append("\" title=\"").append(getHtml("_administration", locale)).append("\"><span class=\"icn isetting\"></span></a></li>");
            }
        }
        sb.append("</ul></nav>");
    }

    public void appendHtml(JspWriter writer, TemplateAttributes attributes, String content, PageData pageData, HttpServletRequest request) throws IOException {
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
