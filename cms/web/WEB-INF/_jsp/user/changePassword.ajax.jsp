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
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int userId=SessionReader.getUserId(request);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/user.ajx" method="post" id="userform" name="userform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="changePassword">
    <input type="hidden" name="userId" value="<%=userId%>">
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="oldPassword"><%=StringUtil.getHtml("_oldPassword", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="password" id="oldPassword" name="oldPassword" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="newPassword1"><%=StringUtil.getHtml("_newPassword", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="password" id="newPassword1" name="newPassword1" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="newPassword2"><%=StringUtil.getHtml("_retypePassword", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="password" id="newPassword2" name="newPassword2" value="" maxlength="16"/>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#userform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/user.ajx', params);
    });
</script>
