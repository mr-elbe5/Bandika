<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.team.TeamFilePartData" %>
<%@ page import="de.bandika.cms.team.TeamFileData" %>
<%@ page import="de.bandika.cms.team.TeamFileBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    PageData data=(PageData) request.getAttribute("pageData");
    TeamFilePartData cpdata = (TeamFilePartData) request.getAttribute("pagePartData");
    List<TeamFileData> files = TeamFileBean.getInstance().getFileList(cpdata.getId(), SessionReader.getLoginId(request));
    int fid = RequestReader.getInt(request,"fid");
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
    <legend><%=StringUtil.toHtml(cpdata.getTitle())%>
    </legend>
    <bandika:table id="fileTable" checkId="fid" formName="teamfileform"
                       headerKeys="team_name,team_version,team_owner,team_author,team_checkedoutby"
                       locale="<%=locale.getLanguage()%>">
        <% for (TeamFileData fileData : files) {%>
        <tr>
            <td><input type="checkbox" name="fid" value="<%=fileData.getId()%>" <%=fid == fileData.getId() ? "checked" : ""%>/>
            </td>
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
            <td><%=StringUtil.toHtml(fileData.getCheckoutName())%>
            </td>
        </tr>
        <%}%>
    </bandika:table>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return submitFileAction('openCreateFile');"><%=StringUtil.getHtml("webapp_new", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('checkoutFile');"><%=StringUtil.getHtml("team_checkout", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openEditFile');"><%=StringUtil.getHtml("team_edit", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('undoCheckoutFile');"><%=StringUtil.getHtml("team_undoCheckout", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('checkinFile');"><%=StringUtil.getHtml("team_checkin", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openFileHistory');"><%=StringUtil.getHtml("team_previousVersions", locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitFileAction('openDeleteFile');"><%=StringUtil.getHtml("webapp_delete", locale)%>
        </button>
    </div>
</form>

