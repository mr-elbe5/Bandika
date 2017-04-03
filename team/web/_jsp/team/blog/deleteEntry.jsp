<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.team.file.TeamFileBean" %>
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  TeamFilePartData cpdata = (TeamFilePartData) rdata.getParam("pagePartData");
  ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
  TeamFileBean bean = TeamFileBean.getInstance();
  Locale locale = sdata.getLocale();
%>
<legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
</legend>
<bandika:controlText key="reallyDeleteFile" locale="<%=locale.getLanguage()%>"/>
<table>
  <% for (Integer id : ids) {
    try {
      TeamFileData data = bean.getFileDataForUser(id, sdata.getUserId());%>
  <tr>
    <td><%=data.getShortName()%>
    </td>
  </tr>
  <%
      } catch (Exception ignored) {
      }
    }%>
</table>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_teamfile?method=deleteFile&id=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>&fid=<%=StringHelper.getIntString(ids)%>');"><%=StringCache.getHtml("delete", locale)%>
  </button>
  <button class="btn" onclick="return linkTo('/_page?method=show&id=<%=cpdata.getPageId()%>');"><%=StringCache.getHtml("back", locale)%>
  </button>
</div>
