<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.team.file.TeamFileBean" %>
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    TeamFilePartData cpdata = (TeamFilePartData) rdata.get("pagePartData");
    List<Integer> ids = rdata.getIntegerList("fid");
    TeamFileBean bean = TeamFileBean.getInstance();
    Locale locale = sdata.getLocale();
%>
<legend><%=StringFormat.toHtml(cpdata.getTitle())%>
</legend>
<bandika:controlText key="reallyDeleteFile" locale="<%=locale.getLanguage()%>"/>
<table class="table">
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
    <button class="btn btn-primary"
            onclick="return linkTo('/teamfile.srv?act=deleteFile&pageId=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>&fid=<%=StringFormat.getIntString(ids)%>');"><%=StringCache.getHtml("webapp_delete", locale)%>
    </button>
    <button class="btn"
            onclick="return linkTo('/page.srv?act=show&pageId=<%=cpdata.getPageId()%>');"><%=StringCache.getHtml("webapp_back", locale)%>
    </button>
</div>
