<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale=sdata.getLocale();
  int partId = rdata.getInt("partId");
%>

<div class="well">
  <bandika:controlText key="portal_reallyDeletePart"/>
</div>
<div class="btn-toolbar">
  <button class="btn btn-primary" onclick="return linkTo('/page.srv?act=deleteSharedPart&partId=<%=partId%>');"><%=StringCache.getHtml("webapp_delete", locale)%>
  </button>
  <button class="btn" onclick="return linkTo('/page.srv?act=openEditSharedParts&partId=<%=partId%>');"><%=StringCache.getHtml("webapp_cancel", locale)%>
  </button>
</div>
