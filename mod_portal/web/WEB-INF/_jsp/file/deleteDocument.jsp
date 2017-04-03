<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.file.DocumentBean" %>
<%@ page import="de.bandika.file.DocumentData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    int pageId=rdata.getInt("pageId");
    List<Integer> ids = rdata.getIntegerList("fid");
    DocumentBean bean = DocumentBean.getInstance();
%>
<div class="well">
    <bandika:controlText key="reallyDeleteFile"/>
    <% for (Integer id : ids) {
        DocumentData data = bean.getDocumentData(id);%>
        <div><%=data.getFileName()%></div>
    <%}%>
</div>
<div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="return linkTo('/document.srv?act=deleteDocument&fid=<%=StringFormat.getIntString(ids)%>');"><%=StringCache.getHtml("webapp_delete",locale)%>
    </button>
    <button class="btn"
            onclick="return linkTo('/document.srv?act=reopenDefaultPage&pageId=<%=pageId%>');"><%=StringCache.getHtml("webapp_back",locale)%>
    </button>
</div>
