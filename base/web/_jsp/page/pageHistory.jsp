<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.application.Configuration" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  PageData data = (PageData) rdata.getParam("pageData");
  ArrayList<PageData> pageVersions = PageBean.getInstance().getPageHistory(data.getId());
%>
<form class="form-horizontal" action="/_page" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>

  <div class="well">
    <legend><%=FormatHelper.toHtml(data.getName())%>
    </legend>
    <bandika:dataTable id="pageTable" checkId="version" formName="form" sort="true" paging="true" headerKeys="version,changeDate,author">
      <% for (PageData versionData : pageVersions) {%>
      <tr>
        <td><input type="checkbox" name="version" value="<%=versionData.getVersion()%>"/></td>
        <td>
          <a href="/_page?method=show&id=<%=versionData.getId()%>&version=<%=versionData.getVersion()%>" target="_blank"><%=versionData.getVersion()%>
          </a></td>
        <td><%=Configuration.getDateFormat().format(versionData.getChangeDate())%>
        </td>
        <td><%=FormatHelper.toHtml(versionData.getAuthorName())%>
        </td>
      </tr>
      <%}%>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitMethod('restoreHistoryPage');"><%=StringCache.getHtml("restore")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteHistoryPage');"><%=StringCache.getHtml("delete")%>
    </button>
    <button onclick="return linkTo('/_page?method=openPageSettings&id=<%=data.getId()%>');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>

