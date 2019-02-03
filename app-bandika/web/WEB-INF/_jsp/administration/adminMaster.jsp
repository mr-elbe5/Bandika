<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String title = RequestReader.getString(request, Statics.KEY_TITLE);
    String includeUrl= RequestReader.getString(request, Statics.KEY_JSP);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <title><%=title%></title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/_statics/css/bootstrap.min.css" />
    <link rel="stylesheet" href="/_statics/css/cms.css" />
    <link rel="stylesheet" href="/_statics/css/admin.css" />
    <script type="text/javascript" src="/_statics/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/_statics/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/_statics/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/_statics/js/ace.js"></script>
    <script type="text/javascript" src="/_statics/js/cms.js"></script>
    <script type="text/javascript" src="/_statics/js/admin.js"></script>
</head>

<body>
    <header>
        <div class="container">
            <div class="topheader row">
                <section class="col-12 sysnav">
                    <ul class="nav justify-content-end">
                        <li class="nav-item"><a class="nav-link" href="/"><%=Strings._home.html(locale)%></a></li>
                        <li class="nav-item"><a class="nav-link" href="/user.srv?act=logout"><%=Strings._logout.html(locale)%></a></li>
                    </ul>
                </section>
                <section class="col-12 logo">
                    <a href="/"><img src="/_statics/img/logo-light.png" alt="" /></a>
                </section>
            </div>
            <div class="menuheader row">
                <section class="col-12 menu">
                    <ul class="nav nav-tabs">
                        <li class="nav-item">
                            <a class="nav-link" href="/admin.srv?act=<%=AdminActions.openSystemAdministration%>"><%=Strings._systemAdministration.html(locale)%></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin.srv?act=<%=AdminActions.openContentAdministration%>"><%=Strings._contentAdministration.html(locale)%></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin.srv?act=<%=AdminActions.openPageStructure%>"><%=Strings._pageStructure.html(locale)%></a>
                        </li>
                        <li class="nav-item">
                            <a class="nav-link" href="/admin.srv?act=<%=AdminActions.openFileStructure%>"><%=Strings._fileStructure.html(locale)%></a>
                        </li>
                    </ul>
                </section>
            </div>
            <div>
                <section class="bc">
                    <nav aria-label="breadcrumb">
                        <ol class="breadcrumb">
                            <li class="breadcrumb-item"><a href="/"><%=Strings._home.html(locale)%></a></li>
                            <li class="breadcrumb-item"><a><%=StringUtil.toHtml(title)%></a></li>
                        </ol>
                    </nav>
                </section>
            </div>
        </div>
    </header>
    <main id="main" role="main">
        <div id="pageContainer" class="container">
            <jsp:include page="<%=includeUrl%>" flush="true" />
        </div>
    </main>
    <footer>
        <div class="container"><%=Strings._copyright.html(locale)%></div>
    </footer>
    <div class="modal" id="modalDialog" tabindex="-1" role="dialog">
    </div>
    <script type="text/javascript">
        function confirmDelete(){
            return confirm('<%=Strings._confirmDelete.js(locale)%>');
        }
        function confirmExecute(){
            return confirm('<%=Strings._confirmExecute.js(locale)%>');
        }
    </script>

</body>
</html>
    
