<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.file.LinkedFileData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.file.FileFilterData" %>
<%@ page import="de.bandika.file.FileTypeCache" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
  LinkedFileData data = (LinkedFileData) sdata.getParam("file");
  boolean dimensioned = FileTypeCache.getInstance().isDimensioned(data.getType());
%>
<form class="form-horizontal" action="/_file" method="post" name="form" accept-charset="UTF-8" enctype="multipart/form-data">
  <input type="hidden" name="method" value="saveFile"/>
  <input type="hidden" name="id" value="<%=filterData.getPageId()%>"/>
  <input type="hidden" name="fid" value="<%=data.getId()%>"/>
  <input type="hidden" name="type" value="<%=data.getType()%>"/>

  <div class="well">
    <legend><%=rdata.getTitle()%>
    </legend>
    <% if (!data.isBeingCreated()) {
      String dimensionString = dimensioned ? data.getWidth() + "&nbsp;x&nbsp;" + data.getHeight() : "";%>
    <bandika:controlGroup labelKey="<%=data.getType()%>">
      <% if (data.hasThumbnail()) {%><img src="/_file?method=showThumbnail&fid=<%=data.getId()%>" alt=""><%}%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="fileName" padded="true"><%=FormatHelper.toHtml(data.getShortName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="author" padded="true"><%=FormatHelper.toHtml(data.getAuthorName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="changeDate" padded="true"><%=FormatHelper.toHtmlDateTime(data.getChangeDate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="size" padded="true"><%=String.valueOf(data.getSize() / 1024)%>&nbsp;kB</bandika:controlGroup>
    <% if (dimensioned) {%>
    <bandika:controlGroup labelKey="dimension" padded="true"><%=dimensionString%>
    </bandika:controlGroup>
    <%}%>
    <bandika:controlGroup labelKey="assignedPage" padded="true"><%=data.isExclusive() ? String.valueOf(data.getPageId()) : "-"%>
    </bandika:controlGroup>
    <%}%>
    <bandika:controlGroup labelKey="file" name="file" mandatory="true">
      <bandika:fileUpload name="file"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="name" name="name" mandatory="false">
      <input class="input-block-level" type="text" id="name" name="name" value="<%=FormatHelper.toHtml(data.getName())%>" maxlength="255"/>
    </bandika:controlGroup>
    <% if (dimensioned) {%>
    <bandika:controlGroup labelKey="changeWidth" name="imgWidth" mandatory="false">
      <input class="input-block-level" type="text" id="imgWidth" name="imgWidth" value="<%=Integer.toString(data.getWidth())%>" maxlength="10"/>
    </bandika:controlGroup>
    <%
      }
      if (filterData.getPageId() != 0) {
    %>
    <bandika:controlGroup labelKey="exclusive" name="exclusive">
      <input class="input-block-level" type="checkbox" id="exclusive" name="exclusive" value="1" <%=data.isExclusive() ? "checked" : ""%>/>
    </bandika:controlGroup>
    <%}%>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn" onclick="return linkTo('/_file?method=reopenDefaultPage&id=<%=filterData.getPageId()%>');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>

