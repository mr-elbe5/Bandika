<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.rights.Right" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
%>
<li>
    <div class="contextSource icn iaction"><%=StringUtil.getHtml("_actions", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iaction" onclick="return openLayerDialog('<%=StringUtil.getHtml("_executeDatabaseScript",locale)%>', '/admin.ajx?act=openExecuteDatabaseScript');"><%=StringUtil.getHtml("_executeDatabaseScript", locale)%>
        </div>
        <div class="icn idownload" onclick="return linkTo('/admin.srv?act=getLocalBackup');"><%=StringUtil.getHtml("_getLocalBackup", locale)%>
        </div>
        <div class="icn isync" onclick="linkTo('/admin.srv?act=reinitialize');"><%=StringUtil.getHtml("_reinitialize", locale)%>
        </div>
        <div class="icn isync" onclick="linkTo('/admin.srv?act=restart');"><%=StringUtil.getHtml("_restart", locale)%>
        </div>
    </div>
</li>
<%}%>
