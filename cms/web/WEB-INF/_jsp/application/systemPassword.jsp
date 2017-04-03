<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.InstallerAction" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<form action="/installer.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=InstallerAction.setSystemPassword.name()%>"/>
    <fieldset>
        <legend><%=StringUtil.getHtml("_installation")%>
        </legend>
        <table class="padded form">
            <tr>
                <td>
                    <label>&nbsp;</label></td>
                <td><span><%=StringUtil.getHtml("_systemPasswordHint")%></span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="systemPwd"><%=StringUtil.getHtml("_systemPwd")%>
                    </label></td>
                <td><input type="password" id="systemPwd" name="systemPwd" value="" maxlength="30" required/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="systemPwd2"><%=StringUtil.getHtml("_retypePassword")%>
                    </label></td>
                <td><input type="password" id="systemPwd2" name="systemPwd2" value="" maxlength="30" required/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button type="submit" class="primary"><%=StringUtil.getHtml("_ok")%>
        </button>
    </div>
</form>


