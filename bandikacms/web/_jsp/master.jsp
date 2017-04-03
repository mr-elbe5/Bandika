<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

--%>
<%RequestHelper.setDocAndContentType(out, response);%>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.RequestData" %>
<%
	RequestData rdata= RequestHelper.getRequestData(request);
  RequestError error = (RequestError) rdata.getParam("error");
%>
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
  <head>
    <meta http-equiv="Cache-Control" content="no-cache">
    <meta http-equiv="Pragma\" content="no-cache">
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
    <title><%=rdata.getTitle()%></title>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/std.css">
    <link rel="stylesheet" type="text/css" href="/_statics/styles/admin.css">
    <link rel="stylesheet" type="text/css" href="/_statics/styles/mainmenu.css">
    <link rel="stylesheet" type="text/css" href="/_statics/styles/template.css">
    <link rel="stylesheet" type="text/css" href="/_statics/styles/ckeditor.css">
    <script type="text/javascript" src="/_statics/script/jquery.js" ></script>
	  <script type="text/javascript" src="/_statics/script/jquery.treeview.js"></script>
    <script type="text/javascript" src="/_statics/script/std.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
  </head>
  <body>
    <div id="fullPageLayer" class="fullPageLayer"></div>
    <div id="fullPageLayerBackground" class="fullPageLayerBackground"></div>
    <div class="wrapper">
      <jsp:include page="/_jsp/header.jsp"/>
      <jsp:include page="/_jsp/breadcrumb.jsp"/>
      <% if (error != null) {%>
        <div class="toperror"><%=FormatHelper.toHtml(error.getErrorString(AppConfig.getInstance().getLocale()))%>
      </div>
      <%}%>
      <jsp:include page="/_jsp/menu.jsp"/>
      <div class="page">
        <div class="pageHeader">&nbsp;</div>
	      <div class="pageContent">
          <%try{%>
	        <jsp:include page="<%=rdata.getCurrentJsp()%>"/>
          <%}catch (Exception e){%>
            Jsp error:&nbsp;"<%=e.getMessage()%>"
          <%}%>
	      </div>
	      <div class="pageFooter">&nbsp;</div>
      </div>
    </div>
  </body>
</html>