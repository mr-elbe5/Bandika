<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.group.GroupBean" %>
<%@ page import="de.bandika.cms.group.GroupData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    UserData user = (UserData) SessionReader.getSessionObject(request, "userData");
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/user.ajx" method="post" id="userform" name="userform" accept-charset="UTF-8" enctype="multipart/form-data">
    <input type="hidden" name="act" value="saveUser"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td><label><%=StringUtil.getHtml("_id", locale)%>
                </label></td>
                <td>
          <span><%=Integer.toString(user.getId())%>
          </span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="login"><%=StringUtil.getHtml("_loginName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="login" name="login" value="<%=StringUtil.toHtml(user.getLogin())%>" maxlength="30"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="password"><%=StringUtil.getHtml("_password", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="password" id="password" name="password" value="<%=StringUtil.toHtml(user.getPassword())%>" maxlength="16"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="title"><%=StringUtil.getHtml("_title", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="title" name="title" value="<%=StringUtil.toHtml(user.getTitle())%>" maxlength="30"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="firstName"><%=StringUtil.getHtml("_firstName", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="firstName" name="firstName" value="<%=StringUtil.toHtml(user.getFirstName())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="lastName"><%=StringUtil.getHtml("_lastName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="lastName" name="lastName" value="<%=StringUtil.toHtml(user.getLastName())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="street"><%=StringUtil.getHtml("_street", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="street" name="street" value="<%=StringUtil.toHtml(user.getStreet())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="zipCode"><%=StringUtil.getHtml("_zipCode", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="zipCode" name="zipCode" value="<%=StringUtil.toHtml(user.getZipCode())%>" maxlength="30"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="city"><%=StringUtil.getHtml("_city", locale)%>&nbsp</label></td>
                <td>
                    <input type="text" id="city" name="city" value="<%=StringUtil.toHtml(user.getCity())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="country"><%=StringUtil.getHtml("_country", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="country" name="country" value="<%=StringUtil.toHtml(user.getCountry())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="locale"><%=StringUtil.getHtml("_locale", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="locale" name="locale" value="<%=StringUtil.toHtml(user.getLocale().getLanguage())%>" maxlength="20"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="email"><%=StringUtil.getHtml("_email", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="email" name="email" value="<%=StringUtil.toHtml(user.getEmail())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="phone"><%=StringUtil.getHtml("_phone", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="phone" name="phone" value="<%=StringUtil.toHtml(user.getPhone())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="fax"><%=StringUtil.getHtml("_fax", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="fax" name="fax" value="<%=StringUtil.toHtml(user.getFax())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="mobile"><%=StringUtil.getHtml("_mobile", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="mobile" name="mobile" value="<%=StringUtil.toHtml(user.getMobile())%>" maxlength="100"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="notes"><%=StringUtil.getHtml("_notes", locale)%>
                    </label></td>
                <td><textarea id="notes" name="notes" rows="5"><%=StringUtil.toHtmlInput(user.getNotes())%></textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="portrait"><%=StringUtil.getHtml("_portrait", locale)%>
                    </label></td>
                <td><input type="file" id="portrait" name="portrait"/><% if (!user.getPortraitName().isEmpty()){%><img src="/user.srv?act=showPortrait&userId=<%=user.getId()%>" alt="<%=StringUtil.toHtml(user.getName())%>" /> <%}%>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="approved"><%=StringUtil.getHtml("_approved", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="approved" name="approved" value="true" <%=user.isApproved() ? "checked" : ""%>/>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_groups", locale)%>
                </label></td>
                <td>
                    
                    <% for (GroupData group : groups) {
                        if (group.getId() == GroupData.ID_ALL) {%>
                    <div><input type="checkbox" checked disabled/>&nbsp;<%=StringUtil.toHtml(group.getName())%>
                    </div>
                    <%} else {%>
                    <div>
                        <input type="checkbox" name="groupIds" value="<%=group.getId()%>" <%=user.getGroupIds().contains(group.getId()) ? "checked=\"checked\"" : ""%> />&nbsp;<%=StringUtil.toHtml(group.getName())%><%}%>
                    </div>
                    <%}%>
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
        var params = $this.serializeFiles();
        postMulti2ModalDialog('/user.ajx', params);
    });
</script>
