<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ page import="de.elbe5.file.FolderData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    int activeFolderId = rdata.getInt("folderId");
    int activeFileId = rdata.getInt("fileId");
    FolderData folder = (FolderData) rdata.get("folderData");
    assert folder != null;
%><% if (rdata.hasContentRight(folder.getId(), Right.READ)) {%>
<li class="open">
    <div class="treeline">
        <span class="folderdrag <%=activeFolderId == folder.getId() ? " selected" : ""%>" id="<%=folder.getId()%>" data-dragid="<%=Integer.toString(folder.getId())%>">
            <%=folder.getName()%>
        </span>
        <%if (rdata.hasContentRight(folder.getId(), Right.EDIT)) {%>
        <div class="icons">
            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/file/openEditFolder/<%=folder.getId()%>');" title="<%=Strings.html("_edit",locale)%>"> </a>
            <a class="icon fa fa-long-arrow-down" href="/ctrl/file/inheritFolderRights/<%=folder.getId()%>" title="<%=Strings.html("_inheritAll",locale)%>"> </a>
            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/file/deleteFolder/<%=folder.getId()%>');" title="<%=Strings.html("_delete",locale)%>"> </a>
            <a class="icon fa fa-folder-o" href="" onclick="return openModalDialog('/ctrl/file/openCreateFolder?parentId=<%=folder.getId()%>');" title="<%=Strings.html("_newFolder",locale)%>"> </a>
            <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/file/openCreateFile?folderId=<%=folder.getId()%>');" title="<%=Strings.html("_newFile",locale)%>"> </a>
            <a class="icon fa fa-upload" href="" onclick="return openModalDialog('/ctrl/file/openDropFiles/<%=folder.getId()%>');" title="<%=Strings.html("_addFiles",locale)%>"> </a>
        </div>
    </div>
    <%}%>
    <ul>
        <% if (!folder.getSubFolders().isEmpty() || !folder.getFiles().isEmpty()) {%><% for (FileData file : folder.getFiles()) {%>
        <li>
            <div class="treeline">
                <span id="<%=file.getId()%>" class="filedrag <%=activeFileId == file.getId() ? " selected" : ""%>" data-dragid="<%=Integer.toString(file.getId())%>">
                    <% if (file.isImage()) {%>
                    <img src="/ctrl/file/showPreview/<%=file.getId()%>" alt="<%=StringUtil.toHtml(file.getName())%>"/>
                    <%} else {%>
                    <img src="/static-content/img/document-50.png" alt="<%=StringUtil.toHtml(file.getName())%>"/>
                    <%}%> <%=file.getDisplayName()%> (<%=file.getName()%>)
                </span>
                <div class="icons">
                    <% if (file.isImage()) {%>
                    <a class="icon fa fa-eye" href="/ctrl/file/show/<%=file.getId()%>" target="_blank" title="<%=Strings.html("_view",locale)%>"> </a>
                    <%}%>
                    <a class="icon fa fa-download" href="/ctrl/file/download/<%=file.getId()%>" title="<%=Strings.html("_download",locale)%>"> </a>
                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/file/openEditFile/<%=file.getId()%>');" title="<%=Strings.html("_edit",locale)%>"> </a>
                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/file/deleteFile/<%=file.getId()%>');" title="<%=Strings.html("_delete",locale)%>"> </a>
                </div>

            </div>
        </li>
        <%
            }
            for (FolderData subFolder : folder.getSubFolders()) {
                rdata.put("folderData", subFolder);
        %>
        <jsp:include page="/WEB-INF/_jsp/file/fileTreeFolder.inc.jsp" flush="true"/>
        <%
            }
            rdata.put("folderData", folder);
        %><%}%>
    </ul>
</li>
<%}%>
