/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.tree;

import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.webserver.tree.TreeNode;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class CmsTreeViewHelper {

    public static void addAdminSiteNode(SiteData data, int currentId, List<Integer> activeIds, HttpServletRequest request, Locale locale, JspWriter writer) throws IOException {
        writer.print("<li");
        if (data.getId() == TreeNode.ROOT_ID || activeIds.contains(data.getId()) || currentId==data.getId()) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn isite");
        if (currentId==data.getId())
            writer.write(" selected");
        writer.write("\" onclick=\"$('#properties').load('");
        writer.write(StringUtil.toJs("/site.ajx?act=showSiteProperties&siteId="+data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (data.isEditableForBackendUser(request)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return linkTo('");
            writer.write(StringUtil.toJs("/site.srv?act=show&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editSite",locale)+"', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openEditSiteSettings&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn iright\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editSiteRights",locale)+"', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openEditSiteRights&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isite\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_createSite",locale)+"', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openCreateSite&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newSite", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ipage\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_createPage",locale)+"', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openCreatePage&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newPage", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ifile\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_createFile",locale)+"', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openCreateFile&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newFile", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icut\" onclick=\"linkTo('");
            writer.write(StringUtil.toJs("/site.srv?act=cutSite&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            if (SessionHelper.getSessionObject(request, "cutSiteId")!=null){
                writer.write("<div class=\"icn ipaste\" onclick=\"linkTo('");
                writer.write(StringUtil.toJs("/site.srv?act=pasteSite&siteId="+data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pasteSite", locale));
                writer.write("</div>");
            }
            if (SessionHelper.getSessionObject(request, "cutPageId")!=null){
                writer.write("<div class=\"icn ipaste\" onclick=\"linkTo('");
                writer.write(StringUtil.toJs("/site.srv?act=pastePage&siteId="+data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pastePage", locale));
                writer.write("</div>");
            }
            if (SessionHelper.getSessionObject(request, "cutFileId")!=null){
                writer.write("<div class=\"icn ipaste\" onclick=\"linkTo('");
                writer.write(StringUtil.toJs("/site.srv?act=pasteFile&siteId="+data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_pasteFile", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn idelete\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_deleteSite",locale)+"', '");
            writer.write(StringUtil.toJs("/site.ajx?act=openDeleteSite&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        if (data.getSites().size()+data.getPages().size()+data.getFiles().size() > 0) {
            writer.write("<ul>");
            for (SiteData child : data.getSites()) {
                if (!child.isVisibleForBackendUser(request)) continue;
                addAdminSiteNode(child, currentId, activeIds, request, locale, writer);
            }
            for (PageData child : data.getPages()) {
                if (!child.isVisibleForBackendUser(request)) continue;
                addAdminPageNode(child, currentId, request, locale, writer);
            }
            for (FileData child : data.getFiles()) {
                if (!child.isVisibleForBackendUser(request)) continue;
                addAdminFileNode(child, currentId, request, locale, writer);
            }
            writer.write("</ul>");
        }
        writer.write("</li>");
    }

    public static void addAdminPageNode(PageData data, int currentId, HttpServletRequest request, Locale locale, JspWriter writer) throws IOException {
        writer.print("<li>");
        writer.write("<div class=\"contextSource icn ipage");
        if (currentId==data.getId())
            writer.write(" selected");
        writer.write("\" onclick=\"$('#properties').load('");
        writer.write(StringUtil.toJs("/page.ajx?act=showPageProperties&pageId="+data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        if (data.isDefaultPage()){
            writer.write(" (");
            writer.write(StringUtil.getHtml("_default", locale));
            writer.write(")");
        }
        writer.write("</div>");
        if (data.isEditableForBackendUser(request)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return linkTo('");
            writer.write(StringUtil.toJs("/page.srv?act=show&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editPageSettings",locale)+"', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openEditPageSettings&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            if (data.getDraftVersion() != 0){
                writer.write("<div class=\"icn ipublish\" onclick=\"return linkTo('");
                writer.write(StringUtil.toJs("/page.srv?act=publishPage&fromAdmin=true&pageId="+data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_publish", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn iright\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editPageRights",locale)+"', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openEditPageRights&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ihistory\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_history",locale)+"', '");
            writer.write(StringUtil.toJs("page.ajx?act=openPageHistory&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_history", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icut\" onclick=\"linkTo('");
            writer.write(StringUtil.toJs("/page.srv?act=cutPage&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idelete\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_deletePage",locale)+"', '");
            writer.write(StringUtil.toJs("/page.ajx?act=openDeletePage&pageId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</li>");
    }

    public static void addAdminFileNode(FileData data, int currentId, HttpServletRequest request, Locale locale, JspWriter writer) throws IOException {
        writer.print("<li>");
        writer.write("<div class=\"contextSource icn ");
        if (data.isImage())
            writer.write("iimage");
        else
            writer.write("ifile");
        if (currentId==data.getId())
            writer.write(" selected");
        writer.write("\" onclick=\"$('#properties').load('");
        writer.write(StringUtil.toJs("/file.ajx?act=showFileProperties&fileId="+data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (data.isEditableForBackendUser(request)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn iweb\" onclick=\"return openTo('");
            writer.write(StringUtil.toJs("/file.srv?act=show&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_view", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn isetting\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editFileSettings",locale)+"', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openEditFileSettings&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_settings", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idownload\" onclick=\"return linkTo('/file.srv?act=download&fileId="+data.getId()+"');\">");
            writer.write(StringUtil.getHtml("_download", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ireplace\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_replaceFile",locale)+"', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openReplaceFile&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_replace", locale));
            writer.write("</div>");
            if (data.getDraftVersion() != 0){
                writer.write("<div class=\"icn ipublish\" onclick=\"return linkTo('");
                writer.write(StringUtil.toJs("/file.srv?act=publishFile&fromAdmin=true&fileId="+data.getId()));
                writer.write("');\">");
                writer.write(StringUtil.getHtml("_publish", locale));
                writer.write("</div>");
            }
            writer.write("<div class=\"icn iright\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_editFileRights",locale)+"', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openEditFileRights&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_rights", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn ihistory\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_history",locale)+"', '");
            writer.write(StringUtil.toJs("file.ajx?act=openFileHistory&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_history", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn icut\" onclick=\"linkTo('");
            writer.write(StringUtil.toJs("/file.srv?act=cutFile&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_cut", locale));
            writer.write("</div>");
            writer.write("<div class=\"icn idelete\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_deleteFile",locale)+"', '");
            writer.write(StringUtil.toJs("/file.ajx?act=openDeleteFile&fileId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_delete", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        writer.write("</li>");
    }

    public static void addBrowserSiteNode(SiteData data, int currentId, List<Integer> activeIds, String functionName, HttpServletRequest request, Locale locale, JspWriter writer) throws IOException {
        writer.print("<li");
        if (data.getId() == TreeNode.ROOT_ID || activeIds.contains(data.getId())) {
            writer.write(" class=\"open\"");
        }
        writer.write(">");
        writer.write("<div class=\"contextSource icn isite");
        if (currentId==data.getId())
            writer.write(" selected");
        writer.write("\" onclick=\"$('#browserView').load('");
        writer.write(StringUtil.toJs("/field.srv?act="+functionName+"&siteId="+data.getId()));
        writer.write("')\">");
        writer.write(data.getDisplayName());
        writer.write("</div>");
        if (data.isEditableForBackendUser(request)) {
            writer.write("<div class=\"contextMenu\">");
            writer.write("<div class=\"icn ifile\" onclick=\"return openModalLayerDialog('"+StringUtil.getHtml("_createFile",locale)+"', '");
            writer.write(StringUtil.toJs("/field.ajx?act=openCreateBrowsedImage&siteId="+data.getId()));
            writer.write("');\">");
            writer.write(StringUtil.getHtml("_newFile", locale));
            writer.write("</div>");
            writer.write("</div>");
        }
        if (data.getSites().size()+data.getPages().size()+data.getFiles().size() > 0) {
            writer.write("<ul>");
            for (SiteData child : data.getSites()) {
                if (!child.isVisibleForBackendUser(request)) continue;
                addBrowserSiteNode(child, currentId, activeIds, functionName, request, locale, writer);
            }
            writer.write("</ul>");
        }
        writer.write("</li>");
    }
}