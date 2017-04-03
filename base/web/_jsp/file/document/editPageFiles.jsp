<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.file.FileFilterData" %>
<%@ page import="de.bandika.file.LinkedFileData" %>
<%@ page import="de.bandika.file.FileBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
  FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
  ArrayList<LinkedFileData> files = FileBean.getInstance().getCurrentFilesOfPage(filterData, false);
%>
<form class="form-horizontal" action="/_file" method="post" name="form" accept-charset="UTF-8">
  <div class="well">
    <input type="hidden" name="method" value=""/>
    <input type="hidden" name="id" value="<%=filterData.getPageId()%>"/>
    <input type="hidden" name="type" value="<%=filterData.getType()%>"/>
    <legend><%=rdata.getTitle()%>
    </legend>
    <bandika:dataTable id="fileTable" checkId="fid" formName="form" sort="true" paging="true" headerKeys="name,thumbnail">
      <% for (LinkedFileData data : files) { %>
      <tr>
        <td><input type="checkbox" name="fid" value="<%=data.getId()%>"/></td>
        <td>
          <a href="/_file?method=openEditFile&fid=<%=data.getId()%>&id=<%=filterData.getPageId()%>&type=<%=data.getType()%>"><%=FormatHelper.toHtml(data.getShortName())%>
          </a></td>
        <td valign="middle">
          <a href="#" onClick="window.open('/_file?method=show&fid=<%=data.getId()%>','FileViewer','width=<%=data.getWidth() + 20%>,height=<%=data.getHeight() + 50%>');return false;">
            <% if (data.hasThumbnail()) {%>
            <img src="/_file?method=showThumbnail&fid=<%=data.getId()%>"
                 width="<%=data.getThumbnail().getWidth()%>" height="<%=data.getThumbnail().getHeight()%>"
                 border='0' alt="" id="img<%=data.getId()%>"/><%} else {%><%=data.getShortName()%><%}%></a></td>
      </tr>
      <%}%>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitMethod('openEditFile');"><%=StringCache.getHtml("edit")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteFile');"><%=StringCache.getHtml("delete")%>
    </button>
  </div>
</form>
