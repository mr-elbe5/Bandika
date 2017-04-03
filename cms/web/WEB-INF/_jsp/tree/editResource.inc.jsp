<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.Configuration" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.webserver.tree.ResourceNode" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    ResourceNode data = (ResourceNode) request.getAttribute("treeNode");
%>
<tr>
    <td>
        <label for = "keywords"><%=StringUtil.getHtml("_keywords", locale)%>
        </label></td>
    <td>
        <div>
            <input type = "text" id = "keywords" name = "keywords" value = "<%=StringUtil.toHtml(data.getKeywords())%>" maxlength = "200"/>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_publishedVersion", locale)%>
    </label></td>
    <td>
        <div><%=Integer.toString(data.getPublishedVersion())%>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_draftVersion", locale)%>
    </label></td>
    <td>
        <div><%=Integer.toString(data.getDraftVersion())%>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_currentVersion", locale)%>
    </label></td>
    <td>
        <div><%=Integer.toString(data.getLoadedVersion())%>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_contentChangeDate", locale)%>
    </label></td>
    <td>
        <div><%=Configuration.getInstance().getHtmlDateTime(data.getContentChangeDate(), locale)%>
        </div>
</tr>
