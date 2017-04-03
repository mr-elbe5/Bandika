<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  UserBean ubean = UserBean.getInstance();
  ArrayList<UserData> users = ubean.getAllUsers();
%>

<div class="layerContent">
  <input type="hidden" name="userAddId" value=""/>
  <table class="table">
    <thead>
    <tr>
      <th><%=StringCache.getHtml("name")%>
      </th>
    </tr>
    </thead>
    <tbody>
    <% for (UserData udata : users) {%>
    <tr>
      <td>
        <a href="#" onclick="document.form.userAddId.value='<%=udata.getId()%>';document.form.method.value='addGroupUser';document.form.submit();"><%=FormatHelper.toHtml(udata.getName())%>
        </a></td>
    </tr>
    <%}%>
    </tbody>
  </table>
</div>



