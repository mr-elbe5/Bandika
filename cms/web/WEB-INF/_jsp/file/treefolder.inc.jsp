<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>

<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.file.FileData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata= RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    int activeFolderId = rdata.getInt("folderId");
    int activeFileId = rdata.getInt("fileId");
    FolderData folder = (FolderData) rdata.get("folderData");
    assert folder !=null;
%>
<% if (rdata.hasContentRight(folder.getId(), Right.READ)) {%>
<li class="open">
    <span class="dropdown-toggle folderdrag <%=activeFolderId == folder.getId() ? " selected" : ""%>" id="<%=folder.getId()%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(folder.getId())%>">
        <%=folder.getName()%>
    </span>
    <div class="dropdown-menu">
        <%if (rdata.hasContentRight(folder.getId(), Right.EDIT)) {%>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file/openEditFolder/<%=folder.getId()%>');"><%=Strings._edit.html(locale)%></a>
        <a class="dropdown-item" href="/file/inheritFolderRights/<%=folder.getId()%>"><%=Strings._inheritAll.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/file/deleteFolder/<%=folder.getId()%>');"><%=Strings._delete.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file/openCreateFolder?parentId=<%=folder.getId()%>');"><%=Strings._newFolder.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file/openCreateFile?folderId=<%=folder.getId()%>');"><%=Strings._newFile.html(locale)%></a>
        <a class="dropdown-item" href="" onclick="return openModalDialog('/file/openDropFiles/<%=folder.getId()%>');"><%=Strings._addFiles.html(locale)%></a>
        <%}%>
    </div>
    <ul>
    <% if (!folder.getSubFolders().isEmpty() || !folder.getFiles().isEmpty()){%>
        <% for (FileData file : folder.getFiles()){
        %>
        <li>
            <span id="<%=file.getId()%>" class="dropdown-toggle filedrag <%=activeFileId == file.getId() ? " selected" : ""%>" data-toggle="dropdown" data-dragid="<%=Integer.toString(file.getId())%>">
                <i class="fa <%=file.isImage()?"fa-image" : "fa-file-o"%>">&nbsp;</i> <%=file.getName()%>
            </span>
            <div class="dropdown-menu">
            <a class="dropdown-item" href="/file/show/<%=file.getId()%>" target="_blank"><%=Strings._view.html(locale)%></a>
            <a class="dropdown-item" href="/file/download/<%=file.getId()%>"><%=Strings._download.html(locale)%></a>
            <a class="dropdown-item" href="" onclick="return openModalDialog('/file/openEditFile/<%=file.getId()%>');"><%=Strings._edit.html(locale)%></a>
            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/file/deleteFile/<%=file.getId()%>');"><%=Strings._delete.html(locale)%></a>
            </div>
        </li>
        <%}
        for (FolderData subFolder : folder.getSubFolders()){
            rdata.put("folderData",subFolder); %>
        <jsp:include  page="/WEB-INF/_jsp/file/treefolder.inc.jsp" flush="true"/>
        <%}
        rdata.put("folderData", folder);%>
    <%}%>
    </ul>
</li>
<%}%>
