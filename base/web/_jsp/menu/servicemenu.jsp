<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.application.Configuration" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="java.util.Locale" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
  MenuData rootPage = MenuCache.getInstance().getRootPage();
  int id = rdata.getCurrentPageId();
  MenuData homePage = MenuCache.getInstance().getHomePage(id);
  boolean useLanguageBranches=Configuration.useLanguageBranches();
  Locale locale = sdata.getLocale();
%>
<ul class="nav nav-pills mini-pills" style="float:right;">
  <% if (homePage!=null) {%>
    <li><a href = "<%=homePage.getUrl()%>"><%=StringCache.getHtml("link|home", locale)%></a></li>
  <%}
    if (!Configuration.showFirstMenuLevel() && rootPage!=null && rootPage.getChildren().size()>1){
    for (MenuData levelData : rootPage.getChildren()){
      if (levelData.equals(homePage))
        continue;%>
    <li><a href="<%=levelData.getUrl()%>"><%=useLanguageBranches ? StringCache.getHtml(levelData.getLocale().getLanguage()) : FormatHelper.toHtml(levelData.getName())%></a></li>
  <%}}%>
  <% if (sdata.isLoggedIn()){%>
  <li><a href="/_user?method=logout"><%=StringCache.getHtml("link|logout",locale)%>
  </a></li>
  <li><a href="/_user?method=openChangeProfile"><%=StringCache.getHtml("link|profile", locale)%>
  </a></li>
  <%}else{%>
  <li><a href="/_user?method=openLogin"><%=StringCache.getHtml("link|login", locale)%>
  </a></li>
  <%}%>
</ul>

