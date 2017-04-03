<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionUserData(request).getId());%>
<form action="/user.ajx" method="post" id="userform" name="userform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="changeProfile">
    <input type="hidden" name="userId" value="<%=user.getId()%>">
    <fieldset>
        <table class="padded form">
                <tr>
                    <td>
                        <label for="firstName"><%=StringUtil.getHtml("_firstName", locale)%></label></td>
                    <td><input type="text" id="firstName" name="firstName" value="<%=StringUtil.toHtml(user.getFirstName())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="middleName"><%=StringUtil.getHtml("_middleName", locale)%></label></td>
                    <td><input type="text" id="middleName" name="middleName" value="<%=StringUtil.toHtml(user.getMiddleName())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="lastName"><%=StringUtil.getHtml("_lastName", locale)%>&nbsp;*</label></td>
                    <td><input type="text" id="lastName" name="lastName" value="<%=StringUtil.toHtml(user.getLastName())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="street"><%=StringUtil.getHtml("_street", locale)%></label></td>
                    <td><input type="text" id="street" name="street" value="<%=StringUtil.toHtml(user.getStreet())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="zipCode"><%=StringUtil.getHtml("_zipCode", locale)%></label></td>
                    <td><input type="text" id="zipCode" name="zipCode" value="<%=StringUtil.toHtml(user.getZipCode())%>" maxlength="30"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="city"><%=StringUtil.getHtml("_city", locale)%></label></td>
                    <td><input type="text" id="city" name="city" value="<%=StringUtil.toHtml(user.getCity())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="country"><%=StringUtil.getHtml("_country", locale)%></label></td>
                    <td><input type="text" id="country" name="country" value="<%=StringUtil.toHtml(user.getCountry())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="locale"><%=StringUtil.getHtml("_locale", locale)%>&nbsp;*</label></td>
                    <td><input type="text" id="locale" name="locale" value="<%=StringUtil.toHtml(user.getLocale().getLanguage())%>" maxlength="20"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="email"><%=StringUtil.getHtml("_email", locale)%>&nbsp;*</label></td>
                    <td><input type="text" id="email" name="email" value="<%=StringUtil.toHtml(user.getEmail())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="phone"><%=StringUtil.getHtml("_phone", locale)%></label></td>
                    <td><input type="text" id="phone" name="phone" value="<%=StringUtil.toHtml(user.getPhone())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="mobile"><%=StringUtil.getHtml("_mobile", locale)%></label></td>
                    <td><input type="text" id="mobile" name="mobile" value="<%=StringUtil.toHtml(user.getMobile())%>" maxlength="100"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        <label for="notes"><%=StringUtil.getHtml("_notes", locale)%></label></td>
                    <td><textarea id="notes" name="notes" rows="5"><%=StringUtil.toHtmlInput(user.getNotes())%></textarea>
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
        