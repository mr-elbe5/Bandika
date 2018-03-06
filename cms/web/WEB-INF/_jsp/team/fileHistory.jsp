<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.cms.team.TeamFileData" %>
<%@ page import="de.bandika.cms.team.TeamFileBean" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    int fileId = RequestReader.getInt(request,"fileId");
    List<TeamFileData> files = TeamFileBean.getInstance().getFileHistory(fileId);
    Locale locale = SessionReader.getSessionLocale(request);
    String tableId="table"+partId;
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<fieldset>
    <table id="<%=tableId%>" class="padded form">
        <tr><th width="15%"><%=StringUtil.getHtml("team_name",locale)%></th>
            <th width="5%"><%=StringUtil.getHtml("team_version",locale)%></th>
            <th width="15%"><%=StringUtil.getHtml("team_owner",locale)%></th>
            <th width="15%"><%=StringUtil.getHtml("team_author",locale)%></th>
            <th width="50%"></th>
        </tr>
        <% for (TeamFileData fileData : files) {%>
        <tr>
            <td>
                <a href="/teamfile.srv?act=showFile&fileId=<%=fileData.getId()%>&version=<%=fileData.getVersion()%>"
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
            <td class="pullRight">
                <a class="icn irestore" title="<%=StringUtil.getHtml("team_restore", locale)%>" href="" onclick="return sendFileAction('restoreHistoryFile',<%=fileData.getId()%>,<%=fileData.getVersion()%>);">&nbsp;</a>
                <a class="icn idelete" title="<%=StringUtil.getHtml("_delete", locale)%>" href="" onclick="return sendFileAction('deleteHistoryFile',<%=fileData.getId()%>,<%=fileData.getVersion()%>);">&nbsp;</a>
            </td>
        </tr>
        <%}%>
    </table>
</fieldset>
<div class="buttonset topspace">
    <button onclick="return sendFileAction('showList',0,0);"><%=StringUtil.getHtml("_back", locale)%>
    </button>
</div>
<script type="text/javascript">
    function sendFileAction(action, fileId, version) {
        var params = {act:action,partId: <%=partId%>,fileId:fileId,version:version};
        post2Target('/teamfile.ajx', params, $('#<%=tableId%>').closest('.teamdocs'));
        return false;
    }
</script>
