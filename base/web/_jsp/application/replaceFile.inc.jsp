<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="java.io.File" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  String path = rdata.getParamString("path");
  File file = new File(path);
%>
<div class="layerContent">
  <form class="form-horizontal" action="/_application" method="post" name="replaceform" accept-charset="UTF-8" enctype="multipart/form-data">
    <div class="well">
      <input type="hidden" name="method" value="replaceFile"/>
      <input type="hidden" name="path" value="<%=path%>"/>
      <bandika:controlGroup labelKey="name" padded="true"><%=file.getName()%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="file" name="file" mandatory="true">
        <bandika:fileUpload name="file"/>
      </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.replaceform.submit();return false;"><%=StringCache.getHtml("replace")%>
      </button>
    </div>
  </form>
</div>


