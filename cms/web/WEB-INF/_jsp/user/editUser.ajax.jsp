<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    UserData user = (UserData) rdata.getSessionObject("userData");
    assert user != null;
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    String label;
    String url = "/user/saveUser/" + user.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._editUser.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="userform" multi="true" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <h3><%=Strings._settings.html(locale)%>
                </h3>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(user.getId())%>
                </cms:line>
                <cms:text name="login" label="<%=Strings._login.toString()%>" required="true"
                          value="<%=StringUtil.toHtml(user.getLogin())%>"/>
                <cms:password name="password" label="<%=Strings._password.toString()%>"/>
                <cms:text name="title" label="<%=Strings._title.toString()%>"
                          value="<%=StringUtil.toHtml(user.getTitle())%>"/>
                <cms:text name="firstName" label="<%=Strings._firstName.toString()%>"
                          value="<%=StringUtil.toHtml(user.getFirstName())%>"/>
                <cms:text name="lastName" label="<%=Strings._lastName.toString()%>" required="true"
                          value="<%=StringUtil.toHtml(user.getLastName())%>"/>
                <cms:text name="locale" label="<%=Strings._locale.toString()%>"
                          value="<%=StringUtil.toHtml(user.getLocale().getLanguage())%>"/>
                <cms:textarea name="notes" label="<%=Strings._notes.toString()%>"
                              height="5rem"><%=StringUtil.toHtml(user.getNotes())%>
                </cms:textarea>
                <cms:file name="portrait"
                          label="<%=Strings._portrait.toString()%>"><% if (!user.getPortraitName().isEmpty()) {%><img
                        src="/user/showPortrait/<%=user.getId()%>"
                        alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%></cms:file>
                <cms:line label="<%=Strings._approved.toString()%>" padded="true">
                    <cms:check name="approved" value="true" checked="<%=user.isApproved()%>"></cms:check>
                </cms:line>
                <h3><%=Strings._address.html(locale)%>
                </h3>
                <cms:text name="street" label="<%=Strings._street.toString()%>"
                          value="<%=StringUtil.toHtml(user.getStreet())%>"/>
                <cms:text name="zipCode" label="<%=Strings._zipCode.toString()%>"
                          value="<%=StringUtil.toHtml(user.getZipCode())%>"/>
                <cms:text name="city" label="<%=Strings._city.toString()%>"
                          value="<%=StringUtil.toHtml(user.getCity())%>"/>
                <cms:text name="country" label="<%=Strings._country.toString()%>"
                          value="<%=StringUtil.toHtml(user.getCountry())%>"/>
                <h3><%=Strings._contact.html(locale)%>
                </h3>
                <cms:text name="email" label="<%=Strings._email.toString()%>" required="true"
                          value="<%=StringUtil.toHtml(user.getEmail())%>"/>
                <cms:line label="<%=Strings._emailVerified.toString()%>" padded="true">
                    <cms:check name="emailVerified" value="true" checked="<%=user.isEmailVerified()%>"></cms:check>
                </cms:line>
                <cms:text name="phone" label="<%=Strings._phone.toString()%>"
                          value="<%=StringUtil.toHtml(user.getPhone())%>"/>
                <cms:text name="fax" label="<%=Strings._fax.toString()%>"
                          value="<%=StringUtil.toHtml(user.getFax())%>"/>
                <cms:text name="mobile" label="<%=Strings._mobile.toString()%>"
                          value="<%=StringUtil.toHtml(user.getMobile())%>"/>
                <h3><%=Strings._groups.html(locale)%>
                </h3>
                <cms:line label="<%=Strings._group.toString()%>"><%=Strings._inGroup.html(locale)%>
                </cms:line>
                <% for (GroupData gdata : groups) {%>
                <%label = gdata.getName();%>
                <cms:line label="<%=label%>" padded="true">
                    <cms:check name="groupIds" value="<%=Integer.toString(gdata.getId())%>"
                               checked="<%=user.getGroupIds().contains(gdata.getId())%>"/>
                </cms:line>
                <%}%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>
