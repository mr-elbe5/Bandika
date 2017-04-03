<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%
  response.setContentType("text/html;charset=UTF-8");
%>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.Configuration" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  String title = rdata.getTitle();
  if (StringHelper.isNullOrEmtpy(title))
    title = StringCache.getString("administration");
%>
<html lang="en">
<head>
  <meta http-equiv="Cache-Control" content="no-cache"/>
  <meta http-equiv="Pragma" content="no-cache"/>
  <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
  <title><%=title%>
  </title>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/bootstrap.css"/>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/jasny-bootstrap.css"/>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/treemenu.css"/>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/DT_bootstrap.css"/>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/common.css"/>
  <link rel="stylesheet" type="text/css" href="/_statics/styles/backend.css"/>
  <script type="text/javascript" src="/_statics/script/jquery-1.8.0.min.js"></script>
  <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
  <script type="text/javascript" src="/_statics/script/jquery.dataTables.js"></script>
  <script type="text/javascript" src="/_statics/script/bootstrap.js"></script>
  <script type="text/javascript" src="/_statics/script/jasny-bootstrap.js"></script>
  <script type="text/javascript" src="/_statics/script/DT_bootstrap.js"></script>
  <script type="text/javascript" src="/_statics/script/jquery.treeview.js"></script>
  <script type="text/javascript" src="/_statics/script/std.js"></script>
  <bandika:headInclude/>
</head>
<body>
<div class="container">
  <div class="row">
    <div class="span6">
    </div>
    <div class="span6">
      <jsp:include page="/_jsp/menu/adminservicemenu.jsp"/>
    </div>
  </div>
  <div class="navbar">
    <div class="navbar-inner">
      <a class="brand" href="#"><%=Configuration.getAppTitle()%>
      </a>
      <ul class="nav nav-pills">
        <li><a><%=FormatHelper.toHtml(title)%>
        </a></li>
      </ul>
    </div>
  </div>
  <div class="row">
    <div class="span3">
      <jsp:include page="/_jsp/menu/treemenu.jsp"/>
    </div>
    <div class="span9">
      <jsp:include page="/_jsp/_master/include/content.inc.jsp"/>
    </div>
  </div>
</div>
</body>
</html>
