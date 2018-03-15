<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.webbase.user.LoginActions" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    String title = StringUtil.getString("appTitle");
    Locale locale = SessionReader.getSessionLocale(request);%>
<html>
<head>
    <meta charset="utf-8"/>
    <title><%=title%>
    </title>
    <link rel="shortcut icon" href="/favicon.ico">
    <link rel="stylesheet" href="/_statics/css/style.css">
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
        &nbsp;
    </section>
    <section class="headerSection">
        <section class="headerMain">
            <div class="title">
                <a class="logo" href="/"><img src="/_statics/img/logo.png" alt="<%=StringUtil.toHtml(title)%>"/></a>
            </div>
            <div class="menu">
            </div>
        </section>
    </section>
    <div id="main" class="main carbon-light">
        <jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
        <section class="mainSection login">
            <form action="/login.srv" method="post" name="loginForm" accept-charset="UTF-8">
                <input type="hidden" name="act" value="<%=LoginActions.login%>">
                <fieldset>
                    <legend><%=StringUtil.getHtml("_login", locale)%>
                    </legend>
                    <table class="padded form">
                        <tr>
                            <td>
                                <label for="login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                            <td><input type="text" id="login" name="login" value="" maxlength="30"/>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label for="password"><%=StringUtil.getHtml("_password", locale)%>&nbsp;*</label></td>
                            <td><input type="password" id="password" name="password" value="" maxlength="16"/>
                            </td>
                        </tr>
                    </table>
                </fieldset>
                <div class="buttonset topspace">
                    <button type="submit" class="primary"><%=StringUtil.getHtml("_login", locale)%>
                    </button>
                </div>
            </form>
        </section>
    </div>
    <div class="footer">
        <cms:snippet name="footer"/>
    </div>
</div>
<script type="text/javascript">
    document.loginForm.login.focus();
</script>
</body>
</html>
