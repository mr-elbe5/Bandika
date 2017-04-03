<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getParamInt("id");
  ArrayList<Integer> versions = rdata.getParamIntegerList("version");
  PageData pageData = PageBean.getInstance().getPage(id);
%>
<div class="well">
  <legend><%=StringCache.getHtml("previousVersions")%>
  </legend>
  <bandika:controlText key="reallyDeleteVersion"/>
  <div>
    <% for (Integer version : versions) {%>
    <%=pageData.getName()%>, <%=StringCache.getHtml("version")%> <%=version%>
    <%}%>
  </div>
</div>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_page?method=deleteHistoryPage&id=<%=id%>&version=<%=StringHelper.getIntString(versions)%>');"><%=StringCache.getHtml("delete")%>
  </button>
  <button class="btn" onclick="return linkTo('/_page?method=openPageHistory&id=<%=id%>');"><%=StringCache.getHtml("back")%>
  </button>
</div>
