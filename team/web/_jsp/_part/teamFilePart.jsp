<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  boolean editMode = rdata.getParamInt("editMode", 0) == 1;
  boolean partEditMode = rdata.getParamInt("partEditMode", 0) == 1;
  TeamFilePartData cpdata = (TeamFilePartData) rdata.getParam("pagePartData");
  Locale locale = sdata.getLocale();
%>
<div class="well teamfile">
  <%if (partEditMode) {%>
  <input type="text" name="title" value="<%=FormatHelper.toHtml(cpdata.getTitle())%>"/>
  <%} else if (editMode) {%>
  <legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
  </legend>
  <%
  } else {
    int partId = rdata.getParamInt("pid");
    int viewMode = TeamFilePartData.MODE_LIST;
    if (partId == cpdata.getId())
      viewMode = rdata.getParamInt("viewMode");
    switch (viewMode) {
      case TeamFilePartData.MODE_EDIT: {
  %>
  <jsp:include page="/_jsp/team/file/editFile.jsp"/>
  <%
    }
    break;
    case TeamFilePartData.MODE_HISTORY: {
  %>
  <jsp:include page="/_jsp/team/file/fileHistory.jsp"/>
  <%
    }
    break;
    case TeamFilePartData.MODE_HISTORY_DELETE: {
  %>
  <jsp:include page="/_jsp/team/file/deleteHistoryFile.jsp"/>
  <%
    }
    break;
    case TeamFilePartData.MODE_DELETE: {
  %>
  <jsp:include page="/_jsp/team/file/deleteFile.jsp"/>
  <%
    }
    break;
    default: {
  %>
  <jsp:include page="/_jsp/team/file/listFiles.jsp"/>
  <%
      }
      break;
    }
  %>
  <%}%>
</div>
