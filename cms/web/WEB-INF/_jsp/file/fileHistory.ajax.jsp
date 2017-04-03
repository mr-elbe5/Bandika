<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.configuration.Configuration" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.file.FileData" %>
<%@ page import="de.bandika.file.FileBean" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    FileData data = (FileData) request.getAttribute("fileData");
    List<FileData> fileVersions = FileBean.getInstance().getFileHistory(data.getId());%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<fieldset>
    <input type="hidden" name="act" value=""/>
    <table class="padded versions">
        <thead>
        <tr>
            <th style="width:20%"><%=StringUtil.getHtml("_version", locale)%>
            </th>
            <th style="width:20%"><%=StringUtil.getHtml("_changeDate", locale)%>
            </th>
            <th style="width:30%"><%=StringUtil.getHtml("_author", locale)%>
            </th>
            <th style="width:30%"></th>
        </tr>
        </thead>
        <tbody>
        <tr>
            <td><%=data.getLoadedVersion()%> (<%=StringUtil.getHtml("_current", locale)%>)</td>
            <td><%=Configuration.getInstance().getDateFormat(locale).format(data.getChangeDate())%>
            </td>
            <td><%=StringUtil.toHtml(data.getAuthorName())%>
            </td>
            <td>
                <a href="/file.srv?act=show&fileId=<%=data.getId()%>&version=<%=data.getLoadedVersion()%>" target="_blank"><%=StringUtil.getHtml("_view", locale)%>
                </a>
            </td>
        </tr>
        <% for (FileData versionData : fileVersions) {%>
        <tr>
            <td><%=versionData.getLoadedVersion()%>
            </td>
            <td><%=Configuration.getInstance().getDateFormat(locale).format(versionData.getChangeDate())%>
            </td>
            <td><%=StringUtil.toHtml(versionData.getAuthorName())%>
            </td>
            <td>
                <a href="/file.srv?act=show&fileId=<%=versionData.getId()%>&version=<%=versionData.getLoadedVersion()%>" target="_blank"><%=StringUtil.getHtml("_view", locale)%>
                </a>
                <button class="primary" onclick="linkTo('/file.srv?act=restoreHistoryFile&fileId=<%=data.getId()%>&version=<%=versionData.getLoadedVersion()%>');"><%=StringUtil.getHtml("_restore", locale)%>
                </button>
                <button class="primary" onclick="linkTo('/file.srv?act=deleteHistoryFile&fileId=<%=data.getId()%>&version=<%=versionData.getLoadedVersion()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </button>
            </td>
        </tr>
        <%}%>
        </tbody>
    </table>
</fieldset>
<div class="buttonset topspace">
    <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
</div>

