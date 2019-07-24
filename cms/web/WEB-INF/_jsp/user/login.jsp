<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<!DOCTYPE html>
<%@ page trimDirectiveWhitespaces="true" %>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.configuration.Configuration" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>

<%
    String title = Configuration.getInstance().getAppTitle();
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
%>
<html lang="en">
<head>
    <meta charset="utf-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico"/>
    <link rel="stylesheet" href="/css/bootstrap.min.css"/>
    <link rel="stylesheet" href="/css/elbe5.css"/>
    <script type="text/javascript" src="/js/jquery-1.12.4.min.js"></script>
    <script type="text/javascript" src="/js/bootstrap.bundle.min.js"></script>

</head>
<body class="login">
<main id="main" role="main">
    <div class="container">
        <cms:message/>
        <section class="mainSection loginSection text-center">
            <form class="form" action="/user/login" method="post" name="loginForm" accept-charset="UTF-8">
                <img class="mb-4" src="/img/logo-dark.png" alt="<%=Configuration.getInstance().getAppTitle()%>">
                <label for="login" class="sr-only"><%=Strings._loginName.html(locale)%>
                </label>
                <input type="text" id="login" name="login" class="form-control"
                       placeholder="<%=Strings._loginName.html(locale)%>" required autofocus>
                <label for="password" class="sr-only"><%=Strings._password.html(locale)%>
                </label>
                <input type="password" id="password" name="password" class="form-control"
                       placeholder="<%=Strings._password.html(locale)%>" required>
                <button class="btn btn-outline-primary" type="submit"><%=Strings._login.html(locale)%>
                </button>
                <button class="btn btn-outline-secondary"
                        onclick="$(location).attr('href','/');"><%=Strings._cancel.html(locale)%>
                </button>
            </form>
        </section>
    </div>
</main>
<footer>
    <div class="container"><%=Strings._copyright.html(locale)%>
    </div>
</footer>
</body>
</html>
