<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.team.blog.TeamBlogPartData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    boolean editMode = rdata.getInt("editMode", 0) == 1;
    boolean partEditMode = rdata.getInt("partEditMode", 0) == 1;
    TeamBlogPartData cpdata = (TeamBlogPartData) rdata.get("pagePartData");
%>
<div class="well teamblog">
    <%if (partEditMode) {%>
    <input type="text" name="title" value="<%=StringFormat.toHtml(cpdata.getTitle())%>"/>
    <%} else if (editMode) {%>
    <legend><%=StringFormat.toHtml(cpdata.getTitle())%>
    </legend>
    <%
    } else {
        int partId = rdata.getInt("pid");
        int viewMode = TeamBlogPartData.MODE_LIST;
        if (partId == cpdata.getId())
            viewMode = rdata.getInt("viewMode");
        switch (viewMode) {
            case TeamBlogPartData.MODE_EDIT: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/blog/editEntry.jsp"/>
    <%
        }
        break;
        case TeamBlogPartData.MODE_DELETE: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/blog/deleteEntry.jsp"/>
    <%
        }
        break;
        default: {
    %>
    <jsp:include page="/WEB-INF/_jsp/team/blog/listEntries.jsp"/>
    <%
            }
            break;
        }
    %>
    <%}%>
</div>
