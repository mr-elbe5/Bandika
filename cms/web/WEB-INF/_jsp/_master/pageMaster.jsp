<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.templatecontrol.BreadcrumbControl" %>
<%@ page import="de.bandika.cms.templatecontrol.MainMenuControl" %>
<%@ page import="de.bandika.cms.templatecontrol.TopAdminNavControl" %>
<%@ page import="de.bandika.cms.templatecontrol.TopNavControl" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%String title = StringUtil.getString("appTitle");%>
<html>
<head>
    <meta charset="utf-8"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" href="/_statics/css/base.css">
    <link rel="stylesheet" href="/_statics/css/fonts.css">
    <link rel="stylesheet" href="/_statics/css/cms.css">
    <link rel="stylesheet" href="/_statics/css/layout.css">
    <script type="text/javascript" src="/_statics/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/_statics/js/jquery.treeview.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/adapters/jquery.js"></script>
    <script type="text/javascript" src="/_statics/js/base.js"></script>
    <script type="text/javascript" src="/_statics/js/cms.js"></script>
</head>
<body class="carbon">
<div class="viewport">
    <section class="topNavSection">
        <div class="left">
            <cms:control name="<%=TopAdminNavControl.KEY%>"/>
        </div>
        <div class="right">
            <cms:control name="<%=TopNavControl.KEY%>"/>
        </div>
    </section>
    <section class="headerSection">
        <section class="headerMain">
            <div class="title">
                <a class="logo" href="/"><img src="/_statics/img/logo.png" alt="<%=StringUtil.toHtml(title)%>"/></a>
            </div>
            <div class="menu">
                <cms:control name="<%=MainMenuControl.KEY%>"/>
            </div>
        </section>
        <section class="searchSection flexRow">
            <cms:control name="<%=BreadcrumbControl.KEY%>"/> <cms:snippet name="searchBox"/>
        </section>
    </section>
    <div id="main" class="main carbon-light">
        <jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
        <jsp:include page="/WEB-INF/_jsp/_master/content.inc.jsp"/>
    </div>
    <div class="footer">
        <cms:snippet name="footer"/>
    </div>
</div>
<jsp:include page="/WEB-INF/_jsp/_master/treeLayer.inc.jsp"/>
<jsp:include page="/WEB-INF/_jsp/_master/dialogLayer.inc.jsp"/>
</body>
</html>
