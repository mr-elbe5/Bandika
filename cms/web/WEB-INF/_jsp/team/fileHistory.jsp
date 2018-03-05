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
<%@ page import="de.bandika.cms.team.TeamFileData" %>
<%@ page import="de.bandika.cms.team.TeamFileBean" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    PageData data=(PageData) request.getAttribute("pageData");
    TeamFilePartData cpdata = (TeamFilePartData) request.getAttribute("pagePartData");
    int fileId = RequestReader.getInt(request,"fid");
    List<TeamFileData> files = TeamFileBean.getInstance().getFileHistory(fileId);
    Locale locale = SessionReader.getSessionLocale(request);
%>
<script type="text/javascript">
    function submitFileAction(action) {
        document.teamfileform.act.value = action;
        document.teamfileform.submit();
        return false;
    }
</script>
<form class="form-horizontal" action="/teamfile.srv" method="post" name="teamfileform" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>
    <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
    <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
    <input type="hidden" name="fid" value="<%=fileId%>"/>
    <legend><%=StringUtil.toHtml(cpdata.getTitle())%>
    </legend>
    <bandika:table id="fileTable" checkId="version" formName="teamfileform"
                       headerKeys="team_name,team_version,team_owner,team_author"
                       locale="<%=locale.getLanguage()%>">
        <%
            for (TeamFileData fileData : files) {%>
        <tr>
            <td><input type="checkbox" name="version" value="<%=fileData.getVersion()%>"/></td>
            <td>
                <a href="/teamfile.srv?act=show&fid=<%=data.getId()%>&version=<%=fileData.getVersion()%>&pid=<%=cpdata.getId()%>&pageId=<%=data.getId()%>"
                   target="_blank"><%=StringUtil.toHtml(fileData.getShortName())%>
                </a></td>
            <td><%=fileData.getVersion()%>
            </td>
            <td><%=StringUtil.toHtml(fileData.getOwnerName())%>
            </td>
            <td><%=StringUtil.toHtml(fileData.getAuthorName())%>
            </td>
        </tr>
        <%}%>
    </bandika:table>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return submitFileAction('restoreHistoryFile');"><%=StringUtil.getHtml("team_restore", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openDeleteHistoryFile');"><%=StringUtil.getHtml("webapp_delete", locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=show&pageId=<%=data.getId()%>&fid=<%=fileId%>');"><%=StringUtil.getHtml("webapp_back", locale)%>
        </button>
    </div>
</form>

