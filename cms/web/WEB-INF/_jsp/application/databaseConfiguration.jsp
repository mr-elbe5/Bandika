<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.cms.application.InstallerAction" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<form action="/installer.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=InstallerAction.setDatabaseConfiguration.name()%>"/>
    <fieldset>
        <legend><%=StringUtil.getHtml("_installation")%>
        </legend>
        <table class="padded form">
            <tr>
                <td>
                    <label>&nbsp;</label></td>
                <td><span><%=StringUtil.getHtml("_dbConnectionHint")%></span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="dbClass"><%=StringUtil.getHtml("_dbClass")%>
                    </label></td>
                <td>
                    <input type="text" id="dbClass" name="dbClass" value="<%=RequestReader.getString(request,"dbClass")%>" maxlength="255" placeholder="ClassName" required/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="dbUrl"><%=StringUtil.getHtml("_dbUrl")%>
                    </label></td>
                <td>
                    <input type="text" id="dbUrl" name="dbUrl" value="<%=RequestReader.getString(request,"dbUrl")%>" maxlength="255" required/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="dbUser"><%=StringUtil.getHtml("_dbUser")%>
                    </label></td>
                <td>
                    <input type="text" id="dbUser" name="dbUser" value="<%=RequestReader.getString(request,"dbUser")%>" maxlength="60" required/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="dbPwd"><%=StringUtil.getHtml("_dbPwd")%>
                    </label></td>
                <td><input type="password" id="dbPwd" name="dbPwd" value="" maxlength="30" required/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button class="primary"><%=StringUtil.getHtml("_ok")%>
        </button>
    </div>
</form>


