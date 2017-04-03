<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.page.ParagraphData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ page import="de.bandika.base.FileData" %>
<%@ page import="de.bandika.base.BaseConfig" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  RequestData rdata=HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
  PageData data = (PageData) rdata.getParam("pageData");
  rdata.setParam("editView", "0");
%>
		<%
			if (data!=null){
				for (ParagraphData pdata : data.getParagraphs()) {
          rdata.setParam("pdata", pdata);
          String url=pdata.getTemplateUrl();
          if (FileData.fileExists(BaseConfig.getBasePath()+url)){%>
        <jsp:include page="<%=url%>" flush="true"/>
        <%}else{%>
            <div><%=AdminStrings.templateNotFound%>:<%=url%></div>
        <%}
				}
				rdata.removeParam("pdata");
			}%>
</bnd:setMaster>
