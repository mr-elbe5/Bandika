<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.cms.application.InstallerActions" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.database.DbConnector" %>
<%@ page import="de.elbe5.cms.database.DbCreator" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.configuration.Configuration" %>
<%@ page import="de.elbe5.cms.application.Initializer" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String title = Configuration.getInstance().getAppTitle();
    boolean dbOk = DbConnector.getInstance().isInitialized() && DbCreator.getInstance().isDatabaseCreated();
    boolean pwdOk = Initializer.getInstance().isDatabasePrepared();
    boolean ready = dbOk && pwdOk;
%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />
    <title><%=title%></title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/_statics/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/_statics/css/cms.css"/>
    <link rel="stylesheet" href="/_statics/css/admin.css"/>
    <script type="text/javascript" src="/_statics/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/_statics/js/bootstrap.bundle.min.js"></script>
    <script type="text/javascript" src="/_statics/js/cms.js"></script>
    <script type="text/javascript" src="/_statics/js/admin.js"></script>
</head>
<body>
<header>
    <div class="container">
        <div class="row">
            <section class="col-12 sysnav">&nbsp;</section>
        </div>
        <div class="row header">
            <section class="col-12 logo">
                <a href="#"><img src="/_statics/img/logo-light.png" style="width:80px" alt=""/></a>
            </section>
        </div>
    </div>
</header>
<div style="margin-bottom:5rem;">&nbsp;</div>
<main id="main" role="main">
    <div class="container">
        <cms:message/>
        <section class="mainSection installSection">
            <% if (dbOk && !pwdOk) {%>
            <form class="form" action="/installer.srv" method="post" name="form" accept-charset="UTF-8">
                <input type="hidden" name="act" value="<%=InstallerActions.setSystemPassword%>"/>
                <h1 class="h3 mb-3 font-weight-normal"><%=Strings._installation.html(locale)%>
                </h1>
                <p><%=Strings._dbAlmostReadyHint.html(locale)%>
                </p>
                <p><%=Strings._systemPasswordHint.html(locale)%>
                </p>
                <p>&nbsp;</p>
                <div class="form-group row">
                    <label for="systemPwd" class="col-sm-3 col-form-label"><%=Strings._systemPwd.html(locale)%>
                    </label>
                    <div class="col-sm-9">
                        <input type="password" id="systemPwd" name="systemPwd" class="form-control" required autofocus>
                    </div>
                </div>
                <div class="form-group row">
                    <label for="systemPwd2"
                           class="col-sm-3 col-form-label"><%=Strings._retypePassword.html(locale)%>
                    </label>
                    <div class="col-sm-9">
                        <input type="password" id="systemPwd2" name="systemPwd2" class="form-control" required>
                    </div>
                </div>
                <button class="btn btn-primary" type="submit"><%=Strings._ok.html(locale)%>
                </button>
            </form>
            <%} else {%>
            <div class="form">
                <h5><%=ready ? Strings._ready.html(locale) : Strings._error.html(locale)%>
                </h5>
                <p><%=ready ? Strings._dbReadyHint.html(locale) : Strings._dbErrorHint.html(locale)%>
                </p>
                <% if (ready) {%>
                <button class="btn btn-primary"
                        onclick="linkTo('/admin.srv?act=<%=AdminActions.openSystemAdministration%>')"><%=Strings._ok.html(locale)%>
                </button>
                <%}%>
            </div>
            <%}%>
        </section>
    </div>
</main>
<footer class="footer">
    <div class="container">
        <div class="col-12"> &copy; 2018 Elbe 5 CMS</div>
    </div>
</footer>
<div class="modal" id="modalDialog" tabindex="-1" role="dialog">
</div>
</body>
</html>


