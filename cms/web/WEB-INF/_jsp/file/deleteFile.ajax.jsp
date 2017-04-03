<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.file.FileData" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.tree.TreeCache" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    int fileId = RequestReader.getInt(request, "fileId");
    TreeCache tc = TreeCache.getInstance();
    FileData data = tc.getFile(fileId);%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<div class="info">
    <div class="formText"><%=StringUtil.getHtml("_reallyDeleteFile", locale)%>
    </div>
    <% if (data.hasPreview()) {%>
    <div>
        <img src="/file.srv?act=showPreview&fileId=<%=data.getId()%>" border='0' alt=""/>
    </div>
    <%}%>
    <div><%=data.getName()%>
    </div>
</div>
<div class="buttonset topspace">
    <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
    <button class="primary" onclick="post2ModalDialog('/file.ajx', {act: 'deleteFile', fileId: '<%=fileId%>'});"><%=StringUtil.getHtml("_delete", locale)%>
    </button>
</div>
