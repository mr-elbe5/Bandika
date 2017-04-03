<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getCurrentPageId();
  String areaName = rdata.getParamString("areaName");
  String matchTypes = rdata.getParamString("areaMatchTypes");
  int partId = rdata.getParamInt("partId");
  ArrayList<TemplateData> templates = TemplateCache.getInstance().getMatchingTemplates("part", matchTypes);
%>
<div class="layerContent">
  <table class="table">
    <colgroup>
      <col width="30%">
      <col width="70%">
    </colgroup>
    <thead>
    <tr>
      <th><%=StringCache.getHtml("name")%>
      </th>
      <th><%=StringCache.getHtml("description")%>
      </th>
    </tr>
    </thead>
    <tbody>
    <% for (TemplateData tdata : templates) {%>
    <tr>
      <td>
        <a href="/_page?method=addPagePart&id=<%=id%>&partId=<%=partId%>&areaName=<%=areaName%>&template=<%=tdata.getName()%>"><%=FormatHelper.toHtml(tdata.getName())%>
        </a></td>
      <td><%=FormatHelper.toHtml(tdata.getDescription())%>
      </td>
    </tr>
    <%}%>
    </tbody>
  </table>
</div>
