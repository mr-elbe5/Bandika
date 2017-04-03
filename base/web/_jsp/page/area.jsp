<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.AreaData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  PageData data = (PageData) rdata.getParam("pageData");
  String areaName = rdata.getParamString("areaName");
  if (data != null) {
    AreaData area = data.getArea(areaName);
    if (area != null) {
      for (PagePartData pdata : area.getParts()) {
        rdata.setParam("pagePartData", pdata);
        String url = pdata.getPartTemplateUrl();
        try {%>
<jsp:include page="<%=url%>" flush="true"/>
<%} catch (Exception e) {%>
<div><%=StringCache.getHtml("templateNotFound")%>:<%=url%>
</div>
<%
        }
        rdata.removeParam("pagePartData");
      }
    }
  }
%>



