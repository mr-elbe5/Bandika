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
<%@ page import = "de.elbe5.cms.tree.*" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.webserver.tree.TreeNode" %>
<%@ page import="de.elbe5.webserver.tree.TreeNodeRightsData" %>
<%@ page import="de.elbe5.webserver.tree.TreeRightsProvider" %>
<%String title = RequestHelper.getTitle(request);
    if (StringUtil.isNullOrEmtpy(title)) title = StringUtil.getString("_administration", SessionHelper.getSessionLocale(request));
    Locale locale = SessionHelper.getSessionLocale(request);
    TreeNode node = CmsTreeHelper.getRequestedNode(request, locale);
    CmsTreeCache tc = CmsTreeCache.getInstance();
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
    </head>
    <body class="carbon">
        <div id = "viewport">
            <section class = "headerSection">
                <div class = "sectionInner flexBox">
                    <div class = "headerleft flexItemTwo">
                        <a class = "logo"><img src = "/_statics/img/logo.png" alt = "<%=Configuration.getInstance().getAppTitle()%>"/></a>
                        <h1><%=Configuration.getInstance().getAppTitle()%>
                        </h1>
                    </div>
                    <div class = "headerright flexItemTwo">
                        <a href = "/"><%=StringUtil.getHtml("_home", locale)%>
                    </a> | <a href = "/login.srv?act=logout"><%=StringUtil.getHtml("_logout", locale)%>
                    </a>
                    </div>
                </div>
            </section>
            <jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
            <section class = "mainSection flexBox">
                <section class = "contentSection flexItemTwo">
                    <div class = "sectionInner">
                        <div class = "treeHeader">
                            <%=StringUtil.getString("_administration", SessionHelper.getSessionLocale(request))%>
                        </div>
                        <ul id = "settings" class = "treeRoot">
                            <jsp:include page = "/WEB-INF/_jsp/application/adminactions.inc.jsp"/>
                            <jsp:include page = "/WEB-INF/_jsp/application/adminsettings.inc.jsp"/>
                            <jsp:include page = "/WEB-INF/_jsp/application/adminusers.inc.jsp"/>
                            <jsp:include page = "/WEB-INF/_jsp/application/admintemplates.inc.jsp"/>
                            <jsp:include page = "/WEB-INF/_jsp/application/adminpages.inc.jsp"/>
                        </ul>
                    </div>
                    <div class = "sectionInner">
                        <% if (SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHT_EDIT)) { %>
                        <div class = "treeHeader">
                            <%=StringUtil.getString("_structure", SessionHelper.getSessionLocale(request))%>
                        </div>
                        <ul id = "structure" class = "treeRoot">
                            <%CmsTreeViewHelper.addAdminSiteNode(tc.getRootSite(), node.getId(), node.getParentIds(), request, locale, out);%>
                        </ul>
                        <%}%>
                    </div>
                </section>
                <aside class = "asideSection flexItemOne">
                    <div class = "sectionInner">
                        <div id = "properties">
                            <fieldset>
                                <legend><%=StringUtil.getHtml("_properties", locale)%>
                                </legend>
                            </fieldset>
                        </div>
                    </div>
                </aside>
            </section>
            <section class = "footerSection">
                <div class = "sectionInner">
                    &copy; 2015 Elbe 5
                </div>
            </section>
        </div>
        <script type = "text/javascript">
            $("#settings").treeview({
                persist: "location", collapsed: true, unique: false
            });
            $("#structure").treeview({
                persist: "location", collapsed: true, unique: false
            });
            $(".contentSection").initContextTreeForm();
        </script>
        <jsp:include page = "/WEB-INF/_jsp/_masterinclude/layer.inc.jsp"/>
    </body>
</html>
