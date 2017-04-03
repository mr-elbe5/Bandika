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
    int fid = rdata.getInt("fid");
    List<Integer> versions = rdata.getIntegerList("version");
    TeamFileBean bean = TeamFileBean.getInstance();
    Locale locale = sdata.getLocale();
%>
<legend><%=StringFormat.toHtml(cpdata.getTitle())%>
</legend>
<bandika:controlText key="reallyDeleteVersion" locale="<%=locale.getLanguage()%>"/>
<table class="table">
    <% for (Integer version : versions) {
        try {
            TeamFileData data = bean.getFileData(fid, version);%>
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
            onclick="return linkTo('/teamfile.srv?act=deleteHistoryFile&pageId=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>&fid=<%=fid%>&version=<%=StringFormat.getIntString(versions)%>');"><%=StringCache.getHtml("webapp_delete", locale)%>
    </button>
    <button class="btn"
            onclick="return linkTo('/teamfile.srv?act=openHistory&pageId=<%=cpdata.getPageId()%>&pid=<%=cpdata.getId()%>&fid=<%=fid%>');"><%=StringCache.getHtml("webapp_back", locale)%>
    </button>
</div>
