<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.webserver.user.UserBean" %>
<%@ page import = "de.elbe5.base.user.UserData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<fieldset>
    <table class = "listTable">
        <thead>
        <tr>
            <th><%=StringUtil.getHtml("_name", locale)%>
            </th>
        </tr>
        </thead>
        <tbody>
        <% for (UserData udata : users) {%>
        <tr>
            <td>
                <a href = "#" onclick = "return post2ModalDialog('/user.srv?act=addGroupUser',{userAddId:<%=udata.getId()%>})"><%=StringUtil.toHtml(udata.getName())%>
                </a></td>
        </tr>
        <%}%>
        </tbody>
    </table>
</fieldset>
<div class = "buttonset topspace">
    <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
</div>




