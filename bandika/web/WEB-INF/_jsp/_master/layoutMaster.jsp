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
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="java.io.IOException" %>
<%@ page import="de.bandika.application.AppConfiguration" %>
<%@ page import="de.bandika.page.PageRightsData" %>
<%@ page import="de.bandika.application.GeneralRightsProvider" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale = sdata.getLocale();
    MenuData homePage = MenuCache.getInstance().getHomePage(locale);
    List<Locale> otherLocales= MenuCache.getInstance().getOtherLocales(locale);
    int pageId = rdata.getInt("pageId");
    List<MenuData> list = MenuCache.getInstance().getBreadcrumbList(pageId);
    List<Integer> parentIds = MenuCache.getInstance().getParentIds(pageId);
    parentIds.add(pageId);
    boolean hasBackendRight = sdata.hasRight(GeneralRightsProvider.RIGHTS_TYPE_GENERAL) || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, PageRightsData.RIGHT_EDIT);
%>
<bandika:pageEditMode/>
<html lang="en">
<head>
    <meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
    <title><%=rdata.getTitle()%>
    </title>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/jasny-bootstrap.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/ckeditor.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/common.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/lucene.css"/>
    <link rel="stylesheet" type="text/css" href="/_statics/styles/frontend.css"/>
    <script type="text/javascript" src="/_statics/script/jquery-1.10.2.min.js"></script>
    <script type="text/javascript" src="/_statics/ckeditor/ckeditor.js"></script>
    <script type="text/javascript" src="/_statics/script/bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/jasny-bootstrap.js"></script>
    <script type="text/javascript" src="/_statics/script/editorConfig.js"></script>
    <script type="text/javascript" src="/_statics/script/std.js"></script>
    <script type="text/javascript" src="/_statics/script/cms.js"></script>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="span6">
            <% if (hasBackendRight || sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE,pageId, PageRightsData.RIGHT_EDIT)) {%>
            <ul class="nav nav-pills mini-pills">
                <% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE,pageId, PageRightsData.RIGHT_EDIT)) {%>
                <li><a href="#" onclick="$('#selectLayout').modal();return false;"><%=StringCache.getHtml("portal_newPage",locale)%>
                </a></li>
                <li><a href="/page.srv?act=openEditPageContent&pageId=<%=pageId%>"><%=StringCache.getHtml("portal_editPage",locale)%>
                </a></li>
                <li><a href="/page.srv?act=openPageSettings&pageId=<%=pageId%>"><%=StringCache.getHtml("portal_pageSettings",locale)%>
                </a></li>
                <% }
                    if (hasBackendRight) {%>
                <li><a href="/application.srv?act=openAdministration"><%=StringCache.getHtml("portal_administration",locale)%>
                </a></li>
                <%}%>
            </ul>
            <%}%>
        </div>
        <div class="span6">
            <ul class="nav nav-pills mini-pills" style="float:right;">
                <% if (homePage != null) {%>
                <li><a href="<%=homePage.getUrl()%>"><%=StringCache.getHtml("portal_home", locale)%>
                </a></li>
                <%}%>
                <% for (Locale loc : otherLocales) {%>
                <li><a href="/page.srv?act=changeLocale&language=<%=loc.getLanguage()%>"><%=StringFormat.toHtml(loc.getDisplayName(sdata.getLocale()))%>
                </a></li>
                <%}%>
                <li><a href="/lucenesearch.srv?act=openSearch"><%=StringCache.getHtml("lucene_search",locale)%>
                </a></li>
                <% if (sdata.isLoggedIn()) {%>
                <li><a href="/user.srv?act=logout"><%=StringCache.getHtml("webuser_logout", locale)%>
                </a></li>
                <li><a href="/user.srv?act=openChangeProfile"><%=StringCache.getHtml("webuser_profile", locale)%>
                </a></li>
                <%} else {%>
                <li><a href="/user.srv?act=openLogin"><%=StringCache.getHtml("webuser_login", locale)%>
                </a></li>
                <%}%>
            </ul>
        </div>
    </div>

    <div class="navbar">
        <div class="navbar-inner">
            <a class="brand" href="<%=homePage==null ? "/" : homePage.getUrl()%>"><%=AppConfiguration.getInstance().getApplicationName(sdata.getLocale())%>
            </a>
            <ul class="nav">
                <%
                    if (homePage != null)
                        addChildren(homePage, pageId, parentIds, sdata, out);
                %>
            </ul>
        </div>
    </div>
    <ul class="breadcrumb">
        <%
            if (list != null) {
                for (int i = 0; i < list.size(); i++) {
                    MenuData node = list.get(i);
        %>
        <li><%if (i > 0) {%><span class="divider">&gt;</span><%}%><a
                href="<%=node.getUrl()%>"><%=StringFormat.toHtml(node.getName())%>
        </a></li>
        <%
                }
            }
        %>
    </ul>
    <div class="row page">
        <div class="span12">
            <jsp:include page="/WEB-INF/_jsp/_master/content.inc.jsp"/>
        </div>
    </div>
</div>
<script type="text/javascript">
    $('.dropdown-toggle').dropdown();
</script>
</body>
</html>

<%!
    protected static void addChildren(MenuData data, int currentId, List<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
        List<MenuData> children = data.getChildren();
        if (children != null) {
            for (MenuData child : children) {
                if (!child.isVisibleForUser(sdata))
                    continue;
                boolean hasSubChildren = child.getChildren().size() > 0;
                if (hasSubChildren) {
                    writer.println("<li class=\"dropdown\">");
                    writer.print(String.format("<a class=\"dropdown-toggle %s\" data-toggle=\"dropdown\" href=\"%s\">",
                            child.getId() == currentId || parentIds.contains(child.getId()) ? "active" : "",
                            child.getUrl()));
                    writer.print(StringFormat.toHtml(child.getName()));
                    writer.print("<b class=\"caret\"></b>");
                    writer.println("</a>");
                    writer.println("<ul class=\"dropdown-menu\">");
                    addSubChildren(child, 1, currentId, parentIds, sdata, writer);
                    writer.println("</ul>");
                } else {
                    writer.println("<li>");
                    writer.print(String.format("<a class=\"%s\" href=\"%s\">",
                            child.getId() == currentId || parentIds.contains(child.getId()) ? "active" : "",
                            child.getUrl()));
                    writer.print(StringFormat.toHtml(child.getName()));
                    writer.println("</a>");
                }
                writer.println("</li>");
            }
        }
    }

    protected static void addSubChildren(MenuData data, int level, int currentId, List<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
        List<MenuData> children = data.getChildren();
        if (children != null) {
            for (MenuData child : children) {
                if (!child.isVisibleForUser(sdata))
                    continue;
                writer.print("<li><a href=\"");
                writer.print(child.getUrl());
                if (child.getId() == currentId || parentIds.contains(child.getId()))
                    writer.print("\" class=\"active\">");
                else
                    writer.print("\">");
                writer.print(StringFormat.toHtml(child.getName()));
                writer.print("</a>");
                if (child.getChildren().size() > 0) {
                    writer.println("<ul>");
                    addSubChildren(child, level + 1, currentId, parentIds, sdata, writer);
                    writer.println("</ul>");
                }
                writer.println("</li>");
            }
        }
    }

%>