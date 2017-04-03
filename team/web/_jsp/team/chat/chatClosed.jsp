<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale = sdata.getLocale();
%>
<div class="chatEntry">
  <div class="chatUser"><%=StringCache.getHtml("chatHost", locale)%>:</div>
  <div class="chatText"><%=StringCache.getHtml("chatClosed", locale)%>
  </div>
</div>
<div id="closeDiv"></div>