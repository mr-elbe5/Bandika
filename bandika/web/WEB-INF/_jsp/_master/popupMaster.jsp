<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%
    response.setContentType("text/html;charset=UTF-8");
%>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
%>
<bandika:pageEditMode/>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=rdata.getTitle()%>
    </title>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/jasny-bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/treemenu.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/common.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/frontend.css"/>
    <script type="text/javascript" src="/_statics/script/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="/_statics/script/bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/jasny-bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/jquery.treeview.js"></script>
    <script type="text/javascript" src="/_statics/script/std.js"></script>
    <script type="text/javascript" src="/_statics/script/cms.js"></script>
</head>
<body class="frameBody">
<div>
    <jsp:include page="/WEB-INF/_jsp/_master/content.inc.jsp"/>
</div>
</body>
</html>