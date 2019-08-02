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
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    UserData user = UserBean.getInstance().getUser(rdata.getSessionUser().getId());
    String url = "/ctrl/user/changeProfile/" + user.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._changeProfile.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="changeprofileform" ajax="true" multi="true">
            <input type="hidden" name="userId" value="<%=rdata.getUserId()%>"/>
            <div class="modal-body">
                <cms:formerror/>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(user.getId())%>
                </cms:line>
                <cms:line label="<%=Strings._login.toString()%>" required="true"><%=StringUtil.toHtml(user.getLogin())%>
                </cms:line>
                <cms:text name="title" label="<%=Strings._title.toString()%>" value="<%=StringUtil.toHtml(user.getTitle())%>"/>
                <cms:text name="firstName" label="<%=Strings._firstName.toString()%>" value="<%=StringUtil.toHtml(user.getFirstName())%>"/>
                <cms:text name="lastName" label="<%=Strings._lastName.toString()%>" required="true" value="<%=StringUtil.toHtml(user.getLastName())%>"/>
                <cms:textarea name="notes" label="<%=Strings._notes.toString()%>" height="5rem"><%=StringUtil.toHtml(user.getNotes())%>
                </cms:textarea>
                <cms:file name="portrait" label="<%=Strings._portrait.toString()%>"><% if (!user.getPortraitName().isEmpty()) {%><img src="/ctrl/user/showPortrait/<%=user.getId()%>" alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%>
                </cms:file>
                <h3><%=Strings._address.html(locale)%>
                </h3>
                <cms:text name="street" label="<%=Strings._street.toString()%>" value="<%=StringUtil.toHtml(user.getStreet())%>"/>
                <cms:text name="zipCode" label="<%=Strings._zipCode.toString()%>" value="<%=StringUtil.toHtml(user.getZipCode())%>"/>
                <cms:text name="city" label="<%=Strings._city.toString()%>" value="<%=StringUtil.toHtml(user.getCity())%>"/>
                <cms:text name="country" label="<%=Strings._country.toString()%>" value="<%=StringUtil.toHtml(user.getCountry())%>"/>
                <h3><%=Strings._contact.html(locale)%>
                </h3>
                <cms:text name="email" label="<%=Strings._email.toString()%>" required="true" value="<%=StringUtil.toHtml(user.getEmail())%>"/>
                <cms:text name="phone" label="<%=Strings._phone.toString()%>" value="<%=StringUtil.toHtml(user.getPhone())%>"/>
                <cms:text name="fax" label="<%=Strings._fax.toString()%>" value="<%=StringUtil.toHtml(user.getFax())%>"/>
                <cms:text name="mobile" label="<%=Strings._mobile.toString()%>" value="<%=StringUtil.toHtml(user.getMobile())%>"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>

        