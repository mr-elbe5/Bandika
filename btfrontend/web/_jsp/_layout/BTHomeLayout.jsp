<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  PageData data = (PageData) rdata.getParam("pageData");
  if (data == null) {
    data = (PageData) sdata.getParam("pageData");
  }
%>
<bandika:layout id="<%=data.getId()%>">
  <div class="row">
    <div class="span12 stage"><bandika:area name="stage" matchTypes="stage"/></div>
  </div>
  <div class="row">
    <div class="span9 mainteaser"><bandika:area name="mainteaser" matchTypes="teaser"/></div>
    <div class="span3 related"><bandika:area name="related" matchTypes="related"/></div>
  </div>
</bandika:layout>



