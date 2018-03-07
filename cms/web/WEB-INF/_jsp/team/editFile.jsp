<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.team.TeamFileData" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.cms.team.TeamFileActions" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    TeamFileData fileData = (TeamFileData) SessionReader.getSessionObject(request, "fileData");
    assert fileData!=null;
    String tableId="table"+partId;
    Locale locale = SessionReader.getSessionLocale(request);
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<form action="/teamfile.ajx" method="post" id="teamfileform" name="teamfileform" accept-charset="UTF-8" enctype="multipart/form-data">
    <fieldset>
        <input type="hidden" name="act" value="checkinFile"/>
        <input type="hidden" name="partId" value="<%=partId%>"/>
        <input type="hidden" name="fileId" value="<%=fileData.getId()%>"/>
        <table class="padded form" id="<%=tableId%>">
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_fileName", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=StringUtil.toHtml(fileData.getShortName())%>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_owner", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=StringUtil.toHtml(fileData.getOwnerName())%>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_author", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=StringUtil.toHtml(fileData.getAuthorName())%>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_checkedoutby", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=StringUtil.toHtml(fileData.getCheckoutName())%>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_changeDate", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=StringUtil.toHtmlDateTime(fileData.getChangeDate(),locale)%>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("team_size", locale)%>
                    </label></td>
                <td>
                    <div>
                        <%=String.valueOf(fileData.getSize() / 1024)%>&nbsp;kB
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="file"><%=StringUtil.getHtml("team_file", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="file" id="file" name="file" />
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("team_name", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="text" id="name" name="name" value="<%=StringUtil.toHtml(fileData.getDisplayName())%>" maxlength="255"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="notes"><%=StringUtil.getHtml("team_notes", locale)%>
                    </label></td>
                <td>
                    <div>
                        <textarea id="notes" name="notes"><%=StringUtil.toHtml(fileData.getNotes())%></textarea>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button class="primary" type="submit"><%=StringUtil.getHtml("_save", locale)%>
        </button>
        <button onclick="return sendFileAction('<%=TeamFileActions.showList%>');"><%=StringUtil.getHtml("_cancel", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#teamfileform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serializeFiles();
        postMulti2Target('/teamfile.ajx', params, $('#<%=tableId%>').closest('.teamdocs'));
    });
    function sendFileAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/teamfile.ajx', params, $('#<%=tableId%>').closest('.teamdocs'));
        return false;
    }
</script>
