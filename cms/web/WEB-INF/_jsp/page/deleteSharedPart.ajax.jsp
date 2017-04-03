<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    int partId = RequestHelper.getInt(request, "partId");
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<div class = "info">
    <div class = "formText"><%=StringUtil.getHtml("_reallyDeletePart", locale)%>
    </div>
</div>
<div class = "buttonset topspace">
    <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
    <button class = "primary" onclick = "post2ModalDialog('/pagepart.ajx',{act:'deleteSharedPart',partId:'<%=partId%>'});"><%=StringUtil.getHtml("_delete", locale)%>
    </button>
</div>
