<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%
    response.setContentType("text/html;charset=UTF-8");
%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.application.AppConfiguration" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="de.bandika.data.IRights" %>
<%@ page import="de.bandika.page.PageRightsData" %>
<%@ page import="de.bandika.application.GeneralRightsProvider" %>
<%@ page import="de.bandika.application.GeneralRightsData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    String title = rdata.getTitle();
    if (StringFormat.isNullOrEmtpy(title))
        title = StringCache.getString("portal_administration", sdata.getLocale());
    Locale locale=sdata.getLocale();
%>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=title%>
    </title>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/jasny-bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/treemenu.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/common.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/backend.css"/>
    <script type="text/javascript" src="/_statics/script/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="/_statics/script/bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/jasny-bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/jquery.treeview.js"></script>
    <script type="text/javascript" src="/_statics/script/std.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="span6">
        </div>
        <div class="span6">
            <ul class="nav nav-pills mini-pills" style="float:right;">
                <li><a href="/"><%=StringCache.getHtml("portal_home",locale)%>
                </a></li>
                <li><a href="/user.srv?act=logout"><%=StringCache.getHtml("webuser_logout",locale)%>
                </a></li>
            </ul>
        </div>
    </div>
    <div class="navbar">
        <div class="navbar-inner">
            <a class="brand" href="#"><%=AppConfiguration.getInstance().getApplicationName(sdata.getLocale())%>
            </a>
            <ul class="nav nav-pills">
                <li><a><%=StringFormat.toHtml(title)%>
                </a></li>
            </ul>
        </div>
    </div>
    <div class="row">
        <div class="span3">
            <jsp:include page="/WEB-INF/_jsp/_master/treemenu.inc.jsp"/>
            <div class="well">
                <ul class="nav nav-list">
                    <% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, IRights.ID_GENERAL, PageRightsData.RIGHT_EDIT)){%>
                    <li><a href="/document.srv?act=openEditDocuments"><%=StringCache.getHtml("editDocuments",locale)%></a></li>
                    <li><a href="/image.srv?act=openEditImages"><%=StringCache.getHtml("editImages",locale)%></a></li>
                    <li><a href="/template.srv?act=openEditMasterTemplates"><%=StringCache.getHtml("editMasterTemplates",locale)%></a></li>
                    <li><a href="/template.srv?act=openEditLayoutTemplates"><%=StringCache.getHtml("editPageTemplates",locale)%></a></li>
                    <li><a href="/template.srv?act=openEditPartTemplates"><%=StringCache.getHtml("editPartTemplates",locale)%></a></li>
                    <li><a href="/page.srv?act=openEditSharedParts"><%=StringCache.getHtml("editSharedParts",locale)%></a></li>
                    <%}
                        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL, GeneralRightsData.RIGHT_APPLICATION_ADMIN)) {
                    %>
                    <li><a href="/application.srv?act=openEditConfiguration"><%=StringCache.getHtml("editConfiguration",locale)%></a></li>
                    <li><a href="/application.srv?act=openCaches"><%=StringCache.getHtml("editCaches",locale)%></a></li>
                    <li><a href="/timer.srv?act=openEditTimerTasks"><%=StringCache.getHtml("editTimers",locale)%></a></li>
                    <li><a href="/cluster.srv?act=openViewCluster"><%=StringCache.getHtml("editCluster",locale)%></a></li>
                    <%}
                        if (sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL, GeneralRightsData.RIGHT_USER_ADMIN)) {
                    %>
                    <li><a href="/user.srv?act=openEditUsers"><%=StringCache.getHtml("editUsers",locale)%></a></li>
                    <li><a href="/user.srv?act=openEditGroups"><%=StringCache.getHtml("editGroups",locale)%></a></li>
                    <%}%>
                </ul>
            </div>
        </div>
        <div class="span9">
            <jsp:include page="/WEB-INF/_jsp/_master/content.inc.jsp"/>
        </div>
    </div>
</div>
</body>
</html>
