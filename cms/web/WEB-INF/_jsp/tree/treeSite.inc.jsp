<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.tree.TreeNode" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.site.SiteData" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%@ page import="de.elbe5.webbase.rights.SystemZone" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.file.FileData" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<Integer> activeIds = (List<Integer>) request.getAttribute("activeIds");
    assert activeIds !=null;
    int nodeId = activeIds.get(activeIds.size()-1);
    SiteData siteData = (SiteData) request.getAttribute("siteData");
    assert siteData !=null;
    boolean isOpen = siteData.getId() == TreeNode.ID_ROOT || activeIds.contains(siteData.getId()) || nodeId == siteData.getId();
%>

<li class="<%=isOpen ? "open" : ""%>">
    <div class="contextSource icn isite <%=nodeId == siteData.getId() ? "selected" : ""%>" data-siteid="<%=Integer.toString(siteData.getId())%>" data-dragid="<%=Integer.toString(siteData.getId())%>"
         onclick="$('#details').load('/site.ajx?act=showSiteDetails&siteId=<%=siteData.getId()%>');">
        <%=siteData.getDisplayName()%>
    </div>
    <% if (SessionReader.hasContentRight(request, siteData.getId(), Right.EDIT)) {%>
    <div class="contextMenu">
        <div class="icn iweb" onclick="return linkTo('/site.srv?act=show&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_view", locale)%>
        </div>
        <div class="icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editSite", locale)%>', '/site.ajx?act=openEditSiteSettings&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_settings", locale)%>
        </div>
        <div class="icn ilock" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editSiteRights", locale)%>', '/site.ajx?act=openEditSiteRights&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_rights", locale)%>
        </div>
        <% if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {%>
        <div class="icn iinherit" onclick="return linkToTree('/site.srv?act=inheritAll&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_inheritAll", locale)%>
        </div>
        <%}%>
        <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_createSite", locale)%>', '/site.ajx?act=openCreateSite&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_newSite", locale)%>
        </div>
        <% if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.APPROVE)) {%>
        <div class="icn ipublish" onclick="return linkToTree('/site.srv?act=publishAll&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_publishAll", locale)%>
        </div>
        <%}%>
        <div class="icn icut" onclick="return linkToTree('/site.srv?act=cutSite&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_cut", locale)%>
        </div>
        <% if (SessionReader.getSessionObject(request, "cutSiteId") != null) {%>
        <div class="icn ipaste" onclick="return linkToTree('/site.srv?act=pasteSite&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_pasteSite", locale)%>
        </div>
        <%}%>
        <div class="icn idelete" onclick="if (confirmDelete()) return linkToTree('/site.ajx?act=deleteSite&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_delete", locale)%>
        </div>
        <div class="icn isort" onclick="return openLayerDialog('<%=StringUtil.getHtml("_sortSites", locale)%>', '/site.ajx?act=openSortSites&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_sortSites", locale)%>
        </div>
    </div>
    <%}%>
    <ul>
        <li class="<%=isOpen ? "open" : ""%>">
            <div class="contextSource icn ipages" data-siteid="<%=siteData.getId()%>">
                <%=StringUtil.getHtml("_pages")%>
            </div>
            <%if (SessionReader.hasContentRight(request, siteData.getId(), Right.EDIT)) {%>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_createPage", locale)%>', '/page.ajx?act=openCreatePage&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_newPage", locale)%>
                </div>
                <%if (SessionReader.getSessionObject(request, "cutPageId") != null) {%>
                <div class="icn ipaste" onclick="return linkToTree('/site.srv?act=pastePage&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_pastePage", locale)%>
                </div>
                <%}%>
                <div class="icn isort" onclick="return openLayerDialog('<%=StringUtil.getHtml("_sortPages", locale)%>', '/site.ajx?act=openSortPages&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_sortPages", locale)%>
                </div>
            </div>
            <%}%>
            <ul>
                <% for (PageData pageData : siteData.getPages()) {
                    if (SessionReader.hasContentRight(request, pageData.getId(), Right.READ)) {%>
                <li>
                    <div class="contextSource icn ipage <%=nodeId == pageData.getId() ? " selected" : ""%> <%=pageData.hasUnpublishedDraft() ? "unpublished" : "published"%>"
                         data-dragid="<%=Integer.toString(pageData.getId())%>" onclick="$('#details').load('/page.ajx?act=showPageDetails&pageId=<%=pageData.getId()%>');">
                        <%=pageData.getDisplayName()%>
                    <% if (pageData.isDefaultPage()) {%>(<%=StringUtil.getHtml("_default", locale)%>)<%}%>
                    </div>
                    <%if (SessionReader.hasContentRight(request, pageData.getId(), Right.EDIT)) {%>
                    <div class="contextMenu">
                        <div class="icn iweb" onclick="return linkTo('/page.srv?act=show&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_view", locale)%>
                        </div>
                        <div class="icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editPageSettings", locale)%>', '/page.ajx?act=openEditPageSettings&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_settings", locale)%>
                        </div>
                    <%if (SessionReader.hasContentRight(request, pageData.getId(), Right.APPROVE) && pageData.hasUnpublishedDraft()) {%>
                        <div class="icn ipublish" onclick="return linkToTree('/page.srv?act=publishPage&fromAdmin=true&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_publish", locale)%>
                        </div>
                    <%}%>
                        <div class="icn ilock" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editPageRights", locale)%>', '/page.ajx?act=openEditPageRights&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_rights", locale)%>
                        </div>
                        <div class="icn icopy" onclick="return linkToTree('/page.srv?act=clonePage&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_clone", locale)%>
                        </div>
                        <div class="icn icut" onclick="return linkToTree('/page.srv?act=cutPage&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_cut", locale)%>
                        </div>
                        <div class="icn idelete" onclick="if (confirmDelete()) return linkToTree('/page.srv?act=deletePage&pageId=<%=pageData.getId()%>');">
                            <%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                    <%}%>
                </li>
                <%}
                }%>
            </ul>
        </li>
        <li class="<%=isOpen ? "open" : ""%>">
            <div class="contextSource icn ifiles" data-siteid="<%=siteData.getId()%>">
                <%=StringUtil.getHtml("_files")%>
            </div>
            <%if (SessionReader.hasContentRight(request, siteData.getId(), Right.EDIT)) {%>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_createFile", locale)%>', '/file.ajx?act=openCreateFile&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_newPage", locale)%>
                </div>
                <%if (SessionReader.getSessionObject(request, "cutFileId") != null) {%>
                <div class="icn ipaste" onclick="return linkToTree('/site.srv?act=pasteFile&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_pasteFile", locale)%>
                </div>
                <%}%>
                <div class="icn isort" onclick="return openLayerDialog('<%=StringUtil.getHtml("_sortFiles", locale)%>', '/site.ajx?act=openSortFiles&siteId=<%=siteData.getId()%>');">
                    <%=StringUtil.getHtml("_sortFiles", locale)%>
                </div>
            </div>
            <%}%>
            <ul>
                <% for (FileData fileData : siteData.getFiles()) {
                    if (SessionReader.hasContentRight(request, fileData.getId(), Right.READ)) {%>
                <li>
                    <div class="contextSource icn ifile <%=nodeId == fileData.getId() ? " selected" : ""%>"
                         data-dragid="<%=Integer.toString(fileData.getId())%>" onclick="$('#details').load('/file.ajx?act=showFileDetails&fileId=<%=fileData.getId()%>');">
                        <%=fileData.getDisplayName()%>
                    </div>
                    <%if (SessionReader.hasContentRight(request, fileData.getId(), Right.EDIT)) {%>
                    <div class="contextMenu">
                        <div class="icn iweb" onclick="return linkTo('/file.srv?act=show&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_view", locale)%>
                        </div>
                        <div class="icn isetting" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editFileSettings", locale)%>', '/file.ajx?act=openEditFileSettings&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_settings", locale)%>
                        </div>
                        <div class="icn ilock" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editFileRights", locale)%>', '/file.ajx?act=openEditFileRights&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_rights", locale)%>
                        </div>
                        <div class="icn icopy" onclick="return linkToTree('/file.srv?act=cloneFile&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_clone", locale)%>
                        </div>
                        <div class="icn icut" onclick="return linkToTree('/file.srv?act=cutFile&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_cut", locale)%>
                        </div>
                        <div class="icn idelete" onclick="if (confirmDelete()) return linkToTree('/file.srv?act=deleteFile&fileId=<%=fileData.getId()%>');">
                            <%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                    <%}%>
                </li>
                <%}
                }%>
            </ul>
        </li>
        <%for (SiteData subSite : siteData.getSites()) {
            if (SessionReader.hasContentRight(request, subSite.getId(), Right.READ)) {
                request.setAttribute("siteData",subSite); %>
            <jsp:include  page="/WEB-INF/_jsp/tree/treeSite.inc.jsp" flush="true"/>
        <%}
        }%>
        <%request.setAttribute("siteData",siteData);%>
    </ul>
</li>