<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    CmsTreeCache tc = CmsTreeCache.getInstance();
    List<Locale> otherLocales = tc.getOtherLocales(locale);
    SiteData homeSite = tc.getLanguageRootSite(locale);
    if (homeSite != null) {%>
<a href = "<%=homeSite.getUrl()%>"><%=StringUtil.getHtml("_home", locale)%>
</a> | <%
    }
    for (Locale loc : otherLocales) {
%>
<li>
    <a href = "/default.srv?act=changeLocale&language=<%=loc.getLanguage()%>"><%=StringUtil.toHtml(loc.getDisplayName(SessionHelper.getSessionLocale(request)))%>
    </a> | <%}
    if (SessionHelper.isLoggedIn(request)) {%> <a href = "/login.srv?act=logout"><%=StringUtil.getHtml("_logout", locale)%>
</a> | <a href = "/login.srv?act=openChangeProfile"><%=StringUtil.getHtml("_profile", locale)%>
</a> <% } else {%> <a href = "/login.srv?act=openLogin"><%=StringUtil.getHtml("_login", locale)%>
</a> <%}%>
