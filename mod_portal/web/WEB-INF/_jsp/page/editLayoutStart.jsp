<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="de.bandika.data.IRights" %>
<%@ page import="de.bandika.page.PageRightsData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale=sdata.getLocale();
  PageData data = (PageData) rdata.get("pageData");
  if (data == null) {
    data = (PageData) sdata.get("pageData");
  }
  boolean editingPart = data.getEditPagePart() != null;
%>
<div class="outerEditArea">
  <form class="form-horizontal" action="/page.srv" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="act" value=""/>
  <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
  <input type="hidden" name="areaName" value=""/>
  <input type="hidden" name="partId" value="0"/>
  <input type="hidden" name="template" value=""/>
  <input type="hidden" name="partMethod" value=""/>
  <div class="btn-toolbar">
    <button class="btn btn-small btn-primary <%=editingPart ? "disabled" : ""%>" onclick="return submitAction('savePageFromContent');" <%=editingPart ? "disabled" : ""%>><%=StringCache.getHtml("webapp_save", locale)%></button>
    <% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getId(), PageRightsData.RIGHT_APPROVE)) {%>
    <button class="btn btn-small btn-primary <%=editingPart ? "disabled" : ""%>" onclick="return submitAction('saveAndPublishPageFromContent');" <%=editingPart ? "disabled" : ""%>><%=StringCache.getHtml("portal_publish", locale)%></button>
    <%}%>
    <button class="btn btn-small btn-primary <%=editingPart ? "disabled" : ""%>" onclick="return submitAction('openEditPageSettingsFromContent');" <%=editingPart ? "disabled" : ""%>><%=StringCache.getHtml("portal_pageSettings", locale)%></button>
    <button class="btn btn-small" onclick="return linkTo('/page.srv?act=stopEditing&pageId=<%=data.getId()%>');" ><%=StringCache.getHtml("webapp_cancel", locale)%></button>
  </div>
  <div class="editArea">