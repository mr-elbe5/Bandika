<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.cms.team.TeamFilePartData" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.cms.team.TeamFileBean" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.team.TeamFileData" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    PageData data=(PageData) request.getAttribute("pageData");
    TeamFilePartData cpdata = (TeamFilePartData) request.getAttribute("pagePartData");
    int fid = RequestReader.getInt(request,"fid");
    List<Integer> versions = RequestReader.getIntegerList(request,"version");
    TeamFileBean bean = TeamFileBean.getInstance();
    Locale locale = SessionReader.getSessionLocale(request);
%>
<legend><%=StringUtil.toHtml(cpdata.getTitle())%>
</legend>
<bandika:controlText key="reallyDeleteVersion" locale="<%=locale.getLanguage()%>"/>
<table class="table">
    <% for (Integer version : versions) {
        try {
            TeamFileData fileData = bean.getFileData(fid, version);%>
    <tr>
        <td><%=fileData.getShortName()%>
        </td>
    </tr>
    <%
            } catch (Exception ignored) {
            }
        }%>
</table>
<div class="btn-toolbar">
    <button class="btn btn-primary"
            onclick="return linkTo('/teamfile.srv?act=deleteHistoryFile&pageId=<%=data.getId()%>&pid=<%=cpdata.getId()%>&fid=<%=fid%>&version=<%=StringUtil.getIntString(versions)%>');"><%=StringUtil.getHtml("webapp_delete", locale)%>
    </button>
    <button class="btn"
            onclick="return linkTo('/teamfile.srv?act=openHistory&pageId=<%=data.getId()%>&pid=<%=cpdata.getId()%>&fid=<%=fid%>');"><%=StringUtil.getHtml("webapp_back", locale)%>
    </button>
</div>
