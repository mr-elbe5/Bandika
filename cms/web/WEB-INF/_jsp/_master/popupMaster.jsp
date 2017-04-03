<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<html lang = "en">
    <head>
        <meta charset = "utf-8"/>
        <title><%=RequestHelper.getTitle(request)%>
        </title>
        <link rel = "shortcut icon" href = "/favicon.ico">
        <link rel = "stylesheet" href = "/_statics/css/base.css">
        <link rel = "stylesheet" href = "/_statics/css/treeview.css"/>
        <link rel = "stylesheet" href = "/_statics/css/ckeditor.css"/>
        <link rel = "stylesheet" href = "/_statics/css/cms.css">
        <script type = "text/javascript" src = "/_statics/js/jquery-1.10.2.min.js"></script>
        <script type = "text/javascript" src = "/_statics/js/jquery.treeview.js"></script>
        <script type = "text/javascript" src = "/_statics/ckeditor/ckeditor.js"></script>
        <script type = "text/javascript" src = "/_statics/js/base.js"></script>
        <script type = "text/javascript" src = "/_statics/js/cms.js"></script>
    </head>
    <body class="popup">
        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/content.inc.jsp"/>
        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/layer.inc.jsp"/>
    </body>
</html>