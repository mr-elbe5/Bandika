<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.cms.team.TeamBlogPartData" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    boolean editMode = RequestReader.getInt(request,"editMode", 0) == 1;
    boolean partEditMode = RequestReader.getInt(request, "partEditMode", 0) == 1;
    PageData data=(PageData) request.getAttribute("pageData");
    TeamBlogPartData cpdata = (TeamBlogPartData) request.getAttribute("pagePartData");
%>
<div class="well teamblog">
    <%if (partEditMode) {%>
    <input type="text" name="title" value="<%=StringUtil.toHtml(cpdata.getTitle())%>"/>
    <%} else if (editMode) {%>
    <legend><%=StringUtil.toHtml(cpdata.getTitle())%>
    </legend>
    <%
    } else {
        int partId = RequestReader.getInt(request,"pid");
        int viewMode = TeamBlogPartData.MODE_LIST;
        if (partId == cpdata.getId())
            viewMode = RequestReader.getInt(request,"viewMode");
        switch (viewMode) {
            case TeamBlogPartData.MODE_EDIT: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/editEntry.jsp"/>
    <%
        }
        break;
        case TeamBlogPartData.MODE_DELETE: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/deleteEntry.jsp"/>
    <%
        }
        break;
        default: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/listEntries.jsp"/>
    <%
            }
            break;
        }
    %>
    <%}%>
</div>
