<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<Integer> ids = RequestReader.getIntegerList(request, "userId");
    UserBean bean = UserBean.getInstance();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<div class="info">
    <div class="formText"><%=StringUtil.getHtml("_reallyDeleteUser", locale)%>
    </div>
    <table class="padded listTable">
        <% for (Integer id : ids) {
            UserData user = bean.getUser(id);%>
        <tr>
            <td><%=user.getName()%>
            </td>
        </tr>
        <%}%>
    </table>
</div>
<div class="buttonset topspace">
    <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
    </button>
    <button class="primary" onclick="post2ModalDialog('/user.ajx', {act: '<%=UserActions.deleteUser%>', userId: '<%=StringUtil.getIntString(ids)%>'});"><%=StringUtil.getHtml("_delete", locale)%>
    </button>
</div>
