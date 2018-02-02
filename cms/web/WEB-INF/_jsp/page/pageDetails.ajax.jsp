<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.tree.TreeCache" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int id = RequestReader.getInt(request, "pageId");
    TreeCache tc = TreeCache.getInstance();
    PageData data = tc.getPage(id);
%>
<table class="details">
    <tr>
        <td><label><%=StringUtil.getHtml("_id", locale)%>
        </label></td>
        <td><%=data.getId()%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_creationDate", locale)%>
        </label></td>
        <td><%=data.getCreationDate()%>
        </td>
    </tr>
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
        <td><label><%=StringUtil.getHtml("_description", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getDescription())%>
        </td>
    </tr>
    <tr>
        <td><label><%=StringUtil.getHtml("_pageTemplate", locale)%>
        </label></td>
        <td><%=StringUtil.toHtml(data.getTemplateName())%>
        </td>
    </tr>
</table>


