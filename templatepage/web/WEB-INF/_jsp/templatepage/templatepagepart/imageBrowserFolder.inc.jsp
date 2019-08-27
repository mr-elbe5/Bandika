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
    FolderData folder = (FolderData) rdata.get("folderData");
    assert folder != null;
%><% if (rdata.hasContentRight(folder.getId(), Right.READ)) {%>
<li class="open">
    <a id="folder_<%=folder.getId()%>"><%=folder.getName()%>
    </a>
    <ul>
        <% if (!folder.getSubFolders().isEmpty() || !folder.getFiles().isEmpty()) {%><% for (FileData file : folder.getFiles()) {%>
        <li>
            <div class="treeline">
                <a id="<%=file.getId()%>" href="" onclick="return ckImgCallback('/ctrl/file/show/<%=file.getId()%>');">
                    <% if (file.isImage()) {%>
                    <img src="/ctrl/file/showPreview/<%=file.getId()%>" alt="<%=StringUtil.toHtml(file.getName())%>"/>
                    <%} else {%>
                    <img src="/static-content/img/document-50.png" alt="<%=StringUtil.toHtml(file.getName())%>"/>
                    <%}%> <%=file.getName()%>
                </a>
                <a class="fa fa-eye" title="<%=Strings.html("_view",locale)%>" href="/ctrl/file/show/<%=file.getId()%>" target="_blank"> </a>
            </div>
        </li>
        <%
            }
            for (FolderData subFolder : folder.getSubFolders()) {
                rdata.put("folderData", subFolder);
        %>
        <jsp:include page="/WEB-INF/_jsp/templatepage/templatepagepart/imageBrowserFolder.inc.jsp" flush="true"/>
        <%
            }
            rdata.put("folderData", folder);
        %><%}%>
    </ul>
</li>
<%}%>
