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
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.base.cache.FileCache" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String name = RequestReader.getString(request, "cacheName");
    FileCache cache = FileCache.getCache(name);
    if (cache != null) {
%>
<table class="details">
    <tr>
        <td><label><%=StringUtil.getHtml("_name", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(name)%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_maxCount", locale)%>
        </label></td>
        <td><%=cache.getMaxCount()%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_maxSize", locale)%>
        </label></td>
        <td><%=cache.getMaxSize()%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_cacheCount", locale)%>
        </label></td>
        <td><%=cache.getCacheCount()%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_cacheSize", locale)%>
        </label></td>
        <td><%=cache.getCacheSize()%>
        </td>
    </tr>
</table>
<%}%>

