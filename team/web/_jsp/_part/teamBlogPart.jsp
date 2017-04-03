<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.team.blog.TeamBlogPartData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  boolean editMode = rdata.getParamInt("editMode", 0) == 1;
  boolean partEditMode = rdata.getParamInt("partEditMode", 0) == 1;
  TeamBlogPartData cpdata = (TeamBlogPartData) rdata.getParam("pagePartData");
  Locale locale = sdata.getLocale();
%>
<div class="well teamblog">
  <%if (partEditMode) {%>
  <input type="text" name="title" value="<%=FormatHelper.toHtml(cpdata.getTitle())%>"/>
  <%} else if (editMode) {%>
  <legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
  </legend>
  <%
  } else {
    int partId = rdata.getParamInt("pid");
    int viewMode = TeamBlogPartData.MODE_LIST;
    if (partId == cpdata.getId())
      viewMode = rdata.getParamInt("viewMode");
    switch (viewMode) {
      case TeamBlogPartData.MODE_EDIT: {
  %>
  <jsp:include page="/_jsp/team/blog/editEntry.jsp"/>
  <%
    }
    break;
    case TeamBlogPartData.MODE_DELETE: {
  %>
  <jsp:include page="/_jsp/team/blog/deleteEntry.jsp"/>
  <%
    }
    break;
    default: {
  %>
  <jsp:include page="/_jsp/team/blog/listEntries.jsp"/>
  <%
      }
      break;
    }
  %>
  <%}%>
</div>
