<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.reusable.ReusableViewerPartData" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.reusable.ReusablePartBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  boolean editMode = rdata.getParamInt("partEditMode", 0) == 1;
  ReusableViewerPartData pdata = (ReusableViewerPartData) rdata.getParam("pagePartData");
  int targetPartId = pdata.getTargetPartId();
%>
<div>
  <%
    if (editMode) {
      String matchTypes = rdata.getParamString("areaMatchTypes");
      ArrayList<PagePartData> parts = ReusablePartBean.getInstance().getMatchingReusablePageParts(matchTypes);
  %>
  <div class="well">
    <bandika:dataTable id="partTable" sort="true" paging="true" headerKeys=",name,template">
      <% for (PagePartData part : parts) {%>
      <tr>
        <td>
          <input type="radio" name="targetPartId" value="<%=part.getId()%>" <%=(part.getId() == pdata.getTargetPartId()) ? "checked=\"checked\"" : ""%>
        </td>
        <td><%=FormatHelper.toHtml(part.getName())%>
        </td>
        <td><%=FormatHelper.toHtml(part.getPartTemplate())%>
        </td>
      </tr>
      <%}%>
    </bandika:dataTable>
  </div>
  <%
  } else {
    if (targetPartId != 0) {
      PagePartData targetPart = ReusablePartBean.getInstance().getPagePart(targetPartId);
      if (targetPart != null) {
        String url = targetPart.getPartTemplateUrl();
        rdata.setParam("pagePartData", targetPart);
        try {
  %>
  <jsp:include page="<%=url%>" flush="true"/>
  <%} catch (Exception e) {%>
  <div><%=StringCache.getHtml("templateNotFound")%>:<%=url%>
  </div>
  <%
          }
          rdata.setParam("pagePartData", pdata);
        }
      }
    }
  %>
</div>