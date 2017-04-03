<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.file.FileBean" %>
<%@ page import="de.bandika.file.LinkedFileData" %>
<%@ page import="de.bandika.file.FileFilterData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
  ArrayList<Integer> ids = rdata.getParamIntegerList("fid");
  String type = rdata.getParamString("type");
  FileBean bean = FileBean.getInstance();
%>
<div class="well">
  <bandika:controlText key="reallyDeleteFile"/>
  <% for (Integer id : ids) {
    try {
      LinkedFileData data = bean.getFileData(id);
      if (data.hasThumbnail()) {%>
  <div>
    <div>
      <img src="/_file?method=showThumbnail&fid=<%=data.getId()%>" width="<%=data.getThumbnail().getWidth()%>" height="<%=data.getThumbnail().getHeight()%>" border='0' alt="" id="img<%=data.getId()%>"/>
    </div>
    <%}%>
    <div><%=data.getShortName()%>
    </div>
    <%
      } catch (Exception ignored) {
      }
    %>
  </div>
  <%}%>
</div>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/_file?method=deleteFile&fid=<%=StringHelper.getIntString(ids)%>&type=<%=type%>');"><%=StringCache.getHtml("delete")%>
  </button>
  <button class="btn" onclick="return linkTo('/_file?method=reopenDefaultPage&id=<%=filterData.getPageId()%>');"><%=StringCache.getHtml("back")%>
  </button>
</div>
