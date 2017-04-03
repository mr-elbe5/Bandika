<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.Configuration" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%
    String title = RequestHelper.getTitle(request);
    if (StringUtil.isNullOrEmtpy(title)) title = StringUtil.getString("_login", SessionHelper.getSessionLocale(request));
%>
<html lang = "en">
    <head>
        <meta charset = "utf-8"/>
        <title><%=title%>
        </title>
        <link rel = "shortcut icon" href = "/favicon.ico">
        <link rel = "stylesheet" href = "/_statics/css/base.css">
        <link rel = "stylesheet" href = "/_statics/css/treeview.css"/>
        <link rel = "stylesheet" href = "/_statics/css/cms.css">
        <script type = "text/javascript" src = "/_statics/js/jquery-1.10.2.min.js"></script>
        <script type = "text/javascript" src = "/_statics/js/jquery.treeview.js"></script>
        <script type = "text/javascript" src = "/_statics/js/base.js"></script>
        <script type = "text/javascript" src = "/_statics/js/cms.js"></script>
    </head>
    <body class="carbon">
        <div id = "viewport">
            <section class = "headerSection">
                <div class = "sectionInner flexbox">
                    <div class = "headerleft flexItemTwo">
                        <a class = "navbar-logo"><img src = "/_statics/img/logo.png" alt = "<%=Configuration.getInstance().getAppTitle()%>"/></a>
                        <h1><%=Configuration.getInstance().getAppTitle()%>
                        </h1>
                    </div>
                </div>
            </section>
            <jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
            <section class = "mainSection">
                <div class = "sectionInner loginSection">
                    <jsp:include page = "/WEB-INF/_jsp/_masterinclude/content.inc.jsp"/>
                </div>
            </section>
            <section class = "footerSection">
                <div class = "sectionInner">
                    &copy; 2015 Elbe 5
                </div>
            </section>
        </div>
    </body>
</html>
