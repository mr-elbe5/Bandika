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
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.team.file.TeamFileBean" %>
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    TeamFilePartData cpdata = (TeamFilePartData) rdata.get("pagePartData");
    List<TeamFileData> files = TeamFileBean.getInstance().getFileList(cpdata.getId(), sdata.getUserId());
    int fid = rdata.getInt("fid");
    Locale locale = sdata.getLocale();
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
    <input type="hidden" name="pageId" value="<%=cpdata.getPageId()%>"/>
    <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
    <legend><%=StringFormat.toHtml(cpdata.getTitle())%>
    </legend>
    <bandika:table id="fileTable" checkId="fid" formName="teamfileform"
                       headerKeys="team_name,team_version,team_owner,team_author,team_checkedoutby"
                       locale="<%=locale.getLanguage()%>">
        <% for (TeamFileData data : files) {%>
        <tr>
            <td><input type="checkbox" name="fid" value="<%=data.getId()%>" <%=fid == data.getId() ? "checked" : ""%>/>
            </td>
            <td>
                <a href="/teamfile.srv?act=show&fid=<%=data.getId()%>&version=<%=data.getVersion()%>&pid=<%=cpdata.getId()%>&pageId=<%=cpdata.getPageId()%>"
                   target="_blank"><%=StringFormat.toHtml(data.getShortName())%>
                </a></td>
            <td><%=data.getVersion()%>
            </td>
            <td><%=StringFormat.toHtml(data.getOwnerName())%>
            </td>
            <td><%=StringFormat.toHtml(data.getAuthorName())%>
            </td>
            <td><%=StringFormat.toHtml(data.getCheckoutName())%>
            </td>
        </tr>
        <%}%>
    </bandika:table>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return submitFileAction('openCreateFile');"><%=StringCache.getHtml("webapp_new", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('checkoutFile');"><%=StringCache.getHtml("team_checkout", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openEditFile');"><%=StringCache.getHtml("team_edit", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('undoCheckoutFile');"><%=StringCache.getHtml("team_undoCheckout", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('checkinFile');"><%=StringCache.getHtml("team_checkin", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openFileHistory');"><%=StringCache.getHtml("team_previousVersions", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openDeleteFile');"><%=StringCache.getHtml("webapp_delete", locale)%>
        </button>
    </div>
</form>

