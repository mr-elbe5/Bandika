/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.tree;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.file.FileData;
import de.bandika.cms.page.PageData;
import de.bandika.cms.site.SiteData;
import de.bandika.rights.Right;
import de.bandika.rights.SystemZone;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class TreeHelper {

    public static TreeNode getRequestedNode(HttpServletRequest request, Locale locale) {
        int nodeId = RequestReader.getInt(request, "pageId");
        if (nodeId == 0) {
            nodeId = RequestReader.getInt(request, "fileId");
        }
        if (nodeId == 0) {
            nodeId = RequestReader.getInt(request, "siteId");
        }
        // for opening tree
        TreeCache tc = TreeCache.getInstance();
        if (nodeId != 0) {
            return tc.getNode(nodeId);
        }
        return tc.getLanguageRootSite(locale);
    }

    public static void addAdminSiteNode(PageContext context, JspWriter writer, HttpServletRequest request, SiteData data, int currentId, List<Integer> activeIds, Locale locale) throws IOException {
        if (data == null)
            return;
        boolean isOpen = data.getId() == TreeNode.ID_ROOT || activeIds.contains(data.getId()) || currentId == data.getId();
        writer.write("<li");
        if (isOpen) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn isite");
        if (currentId == data.getId()) {
            writer.write(" selected");
        }
        writer.write("\" data-siteid=\"");
        writer.write(Integer.toString(data.getId()));
        writer.write("\" draggable=\"true\" ondragstart=\"startSiteDrag(event)\" onclick=\"$('#details').load('");
        writer.write(StringUtil.toJs("/site.ajx?act=showSiteDetails&siteId=" + data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (SessionReader.hasContentRight(request, data.getId(), Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return linkTo('");
            writer.write(StringUtil.toJs("/site.srv?act=show&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editSite", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openEditSiteSettings&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn iright\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editSiteRights", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openEditSiteRights&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
                writer.write("<div class=\"icn iinherit\" onclick=\"return linkToTree('");
                writer.write(StringUtil.toJs("/site.srv?act=inheritAll&siteId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_inheritAll", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn inew\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_createSite", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openCreateSite&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newSite", locale));
            writer.write("</div>");
            if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.APPROVE)) {
                writer.write("<div class=\"icn ipublish\" onclick=\"return linkToTree('");
                writer.write(StringUtil.toJs("/site.srv?act=publishAll&siteId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_publishAll", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn icut\" onclick=\"linkToTree('");
            writer.write(StringUtil.toJs("/site.srv?act=cutSite&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            if (SessionReader.getSessionObject(request, "cutSiteId") != null) {
                writer.write("<div class=\"icn ipaste\" onclick=\"linkToTree('");
                writer.write(StringUtil.toJs("/site.srv?act=pasteSite&siteId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pasteSite", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn idelete\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_deleteSite", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openDeleteSite&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isort\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_sortSites", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openSortSites&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_sortSites", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("<ul>");
        writer.write("<li");
        if (isOpen) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn ipages\" data-siteid=\"");
        writer.write(Integer.toString(data.getId()));
        writer.write("\" >");
        writer.write(StringUtil.getHtml("_pages"));
        writer.write("</div>");
        if (SessionReader.hasContentRight(request,data.getId(),Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn inew\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_createPage", locale) + "', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openCreatePage&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newPage", locale));
            writer.write("</div>");
            if (SessionReader.getSessionObject(request, "cutPageId") != null) {
                writer.write("<div class=\"icn ipaste\" onclick=\"linkToTree('");
                writer.write(StringUtil.toJs("/site.srv?act=pastePage&siteId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pastePage", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn isort\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_sortPages", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openSortPages&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_sortPages", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("<ul>");
        for (PageData child : data.getPages()) {
            if (SessionReader.hasContentRight(request, child.getId(), Right.READ)) {
                addAdminPageNode(context, writer, request, child, currentId, locale);
            }
        }
        writer.write("</ul></li>");
        writer.write("<li");
        if (isOpen) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn ifiles\" data-siteid=\"");
        writer.write(Integer.toString(data.getId()));
        writer.write("\" >");
        writer.write(StringUtil.getHtml("_files"));
        writer.write("</div>");
        if (SessionReader.hasContentRight(request,data.getId(),Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn inew\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_createFile", locale) + "', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openCreateFile&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newFile", locale));
            writer.write("</div>");
            if (SessionReader.getSessionObject(request, "cutFileId") != null) {
                writer.write("<div class=\"icn ipaste\" onclick=\"linkToTree('");
                writer.write(StringUtil.toJs("/site.srv?act=pasteFile&siteId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pasteFile", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn isort\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_sortFiles", locale) + "', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openSortFiles&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_sortFiles", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("<ul>");
        for (FileData child : data.getFiles()) {
            if (SessionReader.hasContentRight(request, child.getId(), Right.READ)) {
                addAdminFileNode(context, writer, request, child, currentId, locale);
            }
        }
        writer.write("</ul></li>");
        for (SiteData child : data.getSites()) {
            if (SessionReader.hasContentRight(request, child.getId(), Right.READ)) {
                addAdminSiteNode(context, writer, request, child, currentId, activeIds, locale);
            }
        }
        writer.write("</ul>");
        writer.write("</li>");
    }

    public static void addAdminPageNode(PageContext context, JspWriter writer, HttpServletRequest request, PageData data, int currentId, Locale locale) throws IOException {
        writer.write("<li>");
        writer.write("<div class=\"contextSource icn ipage");
        if (currentId == data.getId()) {
            writer.write(" selected");
        }
        writer.write("\" data-pageid=\"");
        writer.write(Integer.toString(data.getId()));
        writer.write("\" draggable=\"true\" ondragstart=\"startPageDrag(event)\" onclick=\"$('#details').load('");
        writer.write(StringUtil.toJs("/page.ajx?act=showPageDetails&pageId=" + data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        if (data.isDefaultPage()) {
            writer.write(" (");
            writer.write(StringUtil.getHtml("_default", locale));
            writer.write(")");
        }
        writer.write("</div>");
        if (SessionReader.hasContentRight(request, data.getId(), Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return linkTo('");
            writer.write(StringUtil.toJs("/page.srv?act=show&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editPageSettings", locale) + "', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openEditPageSettings&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            if (data.getDraftVersion() != 0 && SessionReader.hasContentRight(request, data.getId(), Right.APPROVE)) {
                writer.write("<div class=\"icn ipublish\" onclick=\"return linkToTree('");
                writer.write(StringUtil.toJs("/page.srv?act=publishPage&fromAdmin=true&pageId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_publish", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn iright\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editPageRights", locale) + "', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openEditPageRights&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ihistory\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_history", locale) + "', '");
            writer.write(StringUtil.toJs("page.ajx?act=openPageHistory&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_history", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icopy\" onclick=\"linkToTree('");
            writer.write(StringUtil.toJs("/page.srv?act=clonePage&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_clone", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icut\" onclick=\"linkToTree('");
            writer.write(StringUtil.toJs("/page.srv?act=cutPage&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idelete\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_deletePage", locale) + "', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openDeletePage&pageId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</li>");
    }

    public static void addAdminFileNode(PageContext context, JspWriter writer, HttpServletRequest request, FileData data, int currentId, Locale locale) throws IOException {
        writer.print("<li>");
        writer.write("<div class=\"contextSource icn ");
        if (data.isImage()) {
            writer.write("iimage");
        } else {
            writer.write("ifile");
        }
        if (currentId == data.getId()) {
            writer.write(" selected");
        }
        writer.write("\" data-fileid=\"");
        writer.write(Integer.toString(data.getId()));
        writer.write("\" draggable=\"true\" ondragstart=\"startFileDrag(event)\" onclick=\"$('#details').load('");
        writer.write(StringUtil.toJs("/file.ajx?act=showFileDetails&fileId=" + data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (SessionReader.hasContentRight(request, data.getId(), Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return openTo('");
            writer.write(StringUtil.toJs(data.getUrl()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editFileSettings", locale) + "', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openEditFileSettings&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idownload\" onclick=\"return linkTo('/file.srv?act=download&fileId=" + data.getId() + "');\">");
            writer.write(StringUtil.getHtml("_download", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ireplace\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_replaceFile", locale) + "', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openReplaceFile&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_replace", locale));
            writer.write("</div>");
            if (data.getDraftVersion() != 0 && SessionReader.hasContentRight(request, data.getId(), Right.APPROVE)) {
                writer.write("<div class=\"icn ipublish\" onclick=\"return linkToTree('");
                writer.write(StringUtil.toJs("/file.srv?act=publishFile&fromAdmin=true&fileId=" + data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_publish", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn iright\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_editFileRights", locale) + "', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openEditFileRights&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ihistory\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_history", locale) + "', '");
            writer.write(StringUtil.toJs("file.ajx?act=openFileHistory&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_history", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icopy\" onclick=\"linkToTree('");
            writer.write(StringUtil.toJs("/file.srv?act=cloneFile&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_clone", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icut\" onclick=\"linkToTree('");
            writer.write(StringUtil.toJs("/file.srv?act=cutFile&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idelete\" onclick=\"return openLayerDialog('" + StringUtil.getHtml("_deleteFile", locale) + "', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openDeleteFile&fileId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</li>");
    }

    public static void addBrowserSiteNode(PageContext context, JspWriter writer, HttpServletRequest request, SiteData data, int currentId, List<Integer> activeIds, String functionName, Locale locale) throws IOException {
        writer.print("<li");
        if (data.getId() == TreeNode.ID_ROOT || activeIds.contains(data.getId())) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn isite");
        if (currentId == data.getId()) {
            writer.write(" selected");
        }
        writer.write("\" onclick=\"$('#browserView').load('");
        writer.write(StringUtil.toJs("/field.srv?act=" + functionName + "&siteId=" + data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (SessionReader.hasContentRight(request, data.getId(), Right.EDIT)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn ifile\" onclick=\"return openBrowserLayerDialog('" + StringUtil.getHtml("_createFile", locale) + "', '");
            writer.write(StringUtil.toJs("/field.ajx?act=openCreateImageInBrowser&siteId=" + data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newFile", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        if (data.getSites().size() + data.getPages().size() + data.getFiles().size() > 0) {
            writer.write("<ul>");
            for (SiteData child : data.getSites()) {
                if (SessionReader.hasContentRight(request, child.getId(), Right.READ)) {
                    addBrowserSiteNode(context, writer, request, child, currentId, activeIds, functionName, locale);
                }
            }
            writer.write("</ul>");
        }
        writer.write("</li>");
    }
}
