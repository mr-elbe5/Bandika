<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<form action = "/installer.srv" method = "post" name = "form" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "setAdminPassword"/>
    <fieldset>
        <legend><%=StringUtil.getHtml("_installation")%>
        </legend>
        <table class = "form">
            <tr>
                <td>
                    <label>&nbsp;</label></td>
                <td><span><%=StringUtil.getHtml("_dbAdminPasswordHint")%></span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "adminPwd"><%=StringUtil.getHtml("_dbAdminPwd")%>
                    </label></td>
                <td><input type = "password" id = "adminPwd" name = "adminPwd" value = "" maxlength = "30" required/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "adminPwd2"><%=StringUtil.getHtml("_retypePassword")%>
                    </label></td>
                <td><input type = "password" id = "adminPwd2" name = "adminPwd2" value = "" maxlength = "30" required/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_ok")%>
        </button>
    </div>
</form>


