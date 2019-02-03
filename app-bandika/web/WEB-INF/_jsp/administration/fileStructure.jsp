<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.file.FileCache" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    FolderData folder = FolderData.getRequestedFolder(request);
    int folderId = folder == null ? 0 : folder.getId();
    List<Integer> activeIds = (folder == null) ? new ArrayList<Integer>() : folder.getParentIds();
    activeIds.add(folderId);
    request.setAttribute("activeIds",activeIds);
    folder= FileCache.getInstance().getRootFolder();
%>
            <div id="pageContent">
                <cms:message />
                <section class="treeSection">
                    <% if (SessionReader.hasAnyContentRight(request)) { %>
                    <ul class="tree filetree">
                        <%
                            if (folder!=null){
                                request.setAttribute("folderData",folder);
                        %>
                        <jsp:include page="/WEB-INF/_jsp/file/treefolder.inc.jsp" flush="true"/>
                        <%}%>
                    </ul>
                    <%}%>
                </section>
            </div>
            <script type="text/javascript">
                $('.tree').treed('fa fa-folder-open-o','fa fa-folder-o');
            </script>
            <jsp:include page="fileDragScript.inc.jsp" flush="true" />

