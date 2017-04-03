<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.base.cache.DataCache" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String name = RequestReader.getString(request, "cacheName");
    DataCache cache = DataCache.getCache(name);
    if (cache!=null){
%>
<h3><%=StringUtil.getString("_cache", locale)%> - <%=StringUtil.getHtml("_details", locale)%>
</h3>
<table class="padded details">
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%></label></td>
        <td><%=StringUtil.toHtml(name)%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_maxCount", locale)%></label></td>
        <td><%=cache.getMaxCount()%></td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_cacheCount", locale)%></label></td>
        <td><%=cache.getCacheCount()%></td>
    </tr>
</table>
<%}%>

