<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.timer.TimerController" %>
<%@ page import="de.bandika.cms.timer.TimerTask" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String timerName = RequestReader.getString(request, "timerName");
    TimerTask data = TimerController.getInstance().getTaskCopy(timerName);
%>
<table class="details">
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getName())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_displayName", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getDisplayName())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_active", locale)%>
        </label></td>
        <td><%=data.isActive() ? "X" : "-"%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_registerExecution", locale)%>
        </label></td>
        <td><%=data.registerExecution() ? "X" : "-"%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_intervalType", locale)%>
        </label></td>
        <td><%=data.getInterval().name()%>
        </td>
    </tr>
</table>


