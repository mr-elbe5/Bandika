<%--
  Elbe 5 CMS  - A Java based modular Content Management System including Content Management and other features
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%--TEMPLATE==Default Master==Default Master Page==none==all--%>
<!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.configuration.Configuration" %>
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
    <body class="carbon">
        <div id = "viewport">
            <section class = "headerSection">
                <div class = "sectionInner flexBox">
                    <div class = "headerleft flexItemOne">
                        <a class = "logo"><img src = "/_statics/img/logo.png" alt = "<%=RequestHelper.getTitle(request)%>"/></a>
                        <h1><%=Configuration.getInstance().getAppTitle()%></h1>
                    </div>
                    <div class = "headermid flexItemThree">
                        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/adminNav.inc.jsp"/>
                    </div>
                    <div class = "headerright flexItemOne">
                        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/layoutNav.inc.jsp"/>
                    </div>
                </div>
                <nav>
                    <jsp:include page = "/WEB-INF/_jsp/_masterinclude/mainMenu.inc.jsp"/>
                    <jsp:include page = "/WEB-INF/_jsp/_masterinclude/breadcrumb.inc.jsp"/>
                </nav>
            </section>
            <jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
            <jsp:include page = "/WEB-INF/_jsp/_masterinclude/content.inc.jsp"/>
            <section class = "footerSection">
                <div class = "sectionInner">
                    &copy; 2015 Elbe 5
                </div>
            </section>
        </div>
        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/layer.inc.jsp"/>
    </body>
</html>
