<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.file.FileData" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<Integer> activeIds = (List<Integer>) request.getAttribute("activeIds");
    assert activeIds !=null;
    int folderId = RequestReader.getInt(request, "folderId");
    int fileId = RequestReader.getInt(request, "fileId");
    FolderData folder = (FolderData) request.getAttribute("folderData");
    assert folder !=null;
    boolean isOpen = folder.getId() == FolderData.ID_ROOT || activeIds.contains(folder.getId()) || folderId == folder.getId();
%>
<% if (SessionReader.hasContentRight(request, folder.getId(), Right.READ)) {%>
<li class="<%=isOpen ? "open" : ""%>">
    <span class="dropdown-toggle folderdrag <%=folderId == folder.getId() ? " selected" : ""%>" id="<%=folder.getId()%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(folder.getId())%>">
        <%=folder.getName()%>
    </span>
    <div class="dropdown-menu">
        <%if (SessionReader.hasContentRight(request, folder.getId(), Right.EDIT)) {%>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file.ajx?act=<%=FileActions.openEditFolder%>&folderId=<%=folder.getId()%>');"><%=Strings._edit.html(locale)%></a>
        <a class="dropdown-item" href="/file.srv?act=<%=FileActions.inheritRights%>&folderId=<%=folder.getId()%>"><%=Strings._inheritAll.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/file.srv?act=<%=FileActions.deleteFolder%>&folderId=<%=folder.getId()%>');"><%=Strings._delete.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file.ajx?act=<%=FileActions.openCreateFolder%>&parentId=<%=folder.getId()%>');"><%=Strings._newFolder.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file.ajx?act=<%=FileActions.openCreateFile%>&folderId=<%=folder.getId()%>');"><%=Strings._newFile.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file.ajx?act=<%=FileActions.openDropFiles%>&folderId=<%=folder.getId()%>');"><%=Strings._addFiles.html(locale)%></a>
        <%}%>
    </div>
    <ul>
    <% if (!folder.getSubFolders().isEmpty() || !folder.getFiles().isEmpty()){%>
        <% for (FileData file : folder.getFiles()){
        %>
        <li>
            <span id="<%=file.getId()%>" class="dropdown-toggle filedrag <%=fileId == file.getId() ? " selected" : ""%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(file.getId())%>">
                <i class="fa <%=file.isImage()?"fa-image" : "fa-file-o"%>">&nbsp;</i> <%=file.getName()%>
            </span>
            <div class="dropdown-menu">
            <a class="dropdown-item" href="/file.srv?act=<%=FileActions.show%>&fileId=<%=file.getId()%>" target="_blank"><%=Strings._view.html(locale)%></a>
            <a class="dropdown-item" href="/file.srv?act=<%=FileActions.download%>&fileId=<%=file.getId()%>"><%=Strings._download.html(locale)%></a>
            <a class="dropdown-item" href="" onclick="return openModalDialog('/file.ajx?act=<%=FileActions.openEditFile%>&fileId=<%=file.getId()%>');"><%=Strings._edit.html(locale)%></a>
            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/file.srv?act=<%=FileActions.deleteFile%>&fileId=<%=file.getId()%>');"><%=Strings._delete.html(locale)%></a>
            </div>
        </li>
        <%}
        for (FolderData subFolder : folder.getSubFolders()){
            request.setAttribute("folderData",subFolder); %>
        <jsp:include  page="/WEB-INF/_jsp/file/treefolder.inc.jsp" flush="true"/>
        <%}
        request.setAttribute("folderData", folder);%>
    <%}%>
    </ul>
</li>
<%}%>
