<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.user.UserBean" %>
<%@ page import="de.elbe5.user.UserData" %>
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
            <h5 class="modal-title"><%=Strings.html("_changeProfile",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="changeprofileform" ajax="true" multi="true">
            <input type="hidden" name="userId" value="<%=rdata.getUserId()%>"/>
            <div class="modal-body">
                <cms:formerror/>
                <cms:line label="_id"><%=Integer.toString(user.getId())%>
                </cms:line>
                <cms:line label="_login" required="true"><%=StringUtil.toHtml(user.getLogin())%>
                </cms:line>
                <cms:text name="title" label="_title" value="<%=StringUtil.toHtml(user.getTitle())%>"/>
                <cms:text name="firstName" label="_firstName" value="<%=StringUtil.toHtml(user.getFirstName())%>"/>
                <cms:text name="lastName" label="_lastName" required="true" value="<%=StringUtil.toHtml(user.getLastName())%>"/>
                <cms:textarea name="notes" label="_notes" height="5rem"><%=StringUtil.toHtml(user.getNotes())%>
                </cms:textarea>
                <cms:file name="portrait" label="_portrait"><% if (!user.getPortraitName().isEmpty()) {%><img src="/ctrl/user/showPortrait/<%=user.getId()%>" alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%>
                </cms:file>
                <h3><%=Strings.html("_address",locale)%>
                </h3>
                <cms:text name="street" label="_street" value="<%=StringUtil.toHtml(user.getStreet())%>"/>
                <cms:text name="zipCode" label="_zipCode" value="<%=StringUtil.toHtml(user.getZipCode())%>"/>
                <cms:text name="city" label="_city" value="<%=StringUtil.toHtml(user.getCity())%>"/>
                <cms:text name="country" label="_country" value="<%=StringUtil.toHtml(user.getCountry())%>"/>
                <h3><%=Strings.html("_contact",locale)%>
                </h3>
                <cms:text name="email" label="_email" required="true" value="<%=StringUtil.toHtml(user.getEmail())%>"/>
                <cms:text name="phone" label="_phone" value="<%=StringUtil.toHtml(user.getPhone())%>"/>
                <cms:text name="fax" label="_fax" value="<%=StringUtil.toHtml(user.getFax())%>"/>
                <cms:text name="mobile" label="_mobile" value="<%=StringUtil.toHtml(user.getMobile())%>"/>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings.html("_save",locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>

        