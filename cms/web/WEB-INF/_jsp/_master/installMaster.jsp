<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%
    String title = StringUtil.getString("appTitle");
%>
<html>
<head>
    <meta charset="utf-8"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" href="/_statics/css/style.css">
    <script type="text/javascript" src="/_statics/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/_statics/js/jquery.treeview.js"></script>
    <script type="text/javascript" src="/_statics/js/base.js"></script>
    <script type="text/javascript" src="/_statics/js/cms.js"></script>
</head>
<body class="carbon">
<div class="viewport">
    <section class="topNavSection">&nbsp;</section>
    <section class="headerSection">
        <section class="headerMain">
            <div class="title">
                <a class="logo"><img src="/_statics/img/logo.png" alt="<%=StringUtil.toHtml(title)%>"/></a>
            </div>
        </section>
    </section>
    <div id="main" class="main carbon-light">
        <jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
        <section class="mainSection flexRow">
            <section class="contentSection flexItem two padded">
                <jsp:include page="/WEB-INF/_jsp/_master/content.inc.jsp"/>
            </section>
            <aside class="asideSection flexItemOne padded">
                <jsp:include page="/WEB-INF/_jsp/application/databaseStatus.jsp"/>
            </aside>
        </section>
    </div>
    <div class="footer">
        &copy; 2017 Bandika
    </div>
</div>
</body>
</html>
