<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    String title = rdata.getString(Statics.KEY_TITLE);
    String includeUrl = rdata.getString(Statics.KEY_JSP);
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/static-content/css/app.css"/>
    <script type="text/javascript" src="/static-content/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/static-content/js/bootstrap.tree.js"></script>
    <script type="text/javascript" src="/static-content/js/ace.js"></script>
    <script type="text/javascript" src="/static-content/js/cms.js"></script>
</head>

<body>
<header>
    <div class="container">
        <div class="top row">
            <section class="col-12 sysnav">
                <ul class="nav justify-content-end">
                    <li class="nav-item"><a class="nav-link" href="/"><%=Strings._home.html(locale)%>
                    </a></li>
                    <li class="nav-item"><a class="nav-link" href="/ctrl/user/logout"><%=Strings._logout.html(locale)%>
                    </a></li>
                </ul>
            </section>
        </div>
        <div class="menu row">
            <section class="col-12 menu">
                <nav class="navbar navbar-expand-lg navbar-light">
                    <a class="navbar-brand" href="/"><img src="/static-content/img/logo-light.png" alt=""/></a>
                    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent" aria-expanded="false" aria-label="Toggle navigation">
                        <span class="fa fa-2x fa-bars"></span>
                    </button>
                    <div class="collapse navbar-collapse" id="navbarSupportedContent">
                        <ul class="navbar-nav mr-auto">
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="/ctrl/admin/openSystemAdministration"><%=Strings._systemAdministration.html(locale)%>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="/ctrl/template/openTemplateAdministration"><%=Strings._templateAdministration.html(locale)%>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/ctrl/page/openPageAdministration"><%=Strings._pageAdministration.html(locale)%>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link"
                                   href="/ctrl/templatepage/openPartAdministration"><%=Strings._partAdministration.html(locale)%>
                                </a>
                            </li>
                            <li class="nav-item">
                                <a class="nav-link" href="/ctrl/file/openFileAdministration"><%=Strings._fileAdministration.html(locale)%>
                                </a>
                            </li>
                        </ul>
                    </div>
                </nav>
            </section>
        </div>
        <div>
            <section class="bc">
                <nav aria-label="breadcrumb">
                    <ol class="breadcrumb">
                        <li class="breadcrumb-item"><a href="/"><%=Strings._home.html(locale)%>
                        </a></li>
                        <li class="breadcrumb-item"><a><%=StringUtil.toHtml(title)%>
                        </a></li>
                    </ol>
                </nav>
            </section>
        </div>
    </div>
</header>
<main id="main" role="main">
    <div id="pageContainer" class="container">
        <jsp:include page="<%=includeUrl%>" flush="true"/>
    </div>
</main>
<footer>
    <div class="container"><%=Strings._copyright.html(locale)%>
    </div>
</footer>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog">
</div>
<script type="text/javascript">
    function confirmDelete() {
        return confirm('<%=Strings._confirmDelete.js(locale)%>');
    }

    function confirmExecute() {
        return confirm('<%=Strings._confirmExecute.js(locale)%>');
    }
</script>

</body>
</html>
    
