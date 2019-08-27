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
%>
<cms:message/>
<section class="contentTop">
    <h1>
        <%=Strings.html("_profile",locale)%>
    </h1>
</section>
<div class="row">
    <section class="col-md-8 contentSection">
        <div class="paragraph">
            <cms:line label="_id"><%=Integer.toString(user.getId())%>
            </cms:line>
            <cms:line label="_login"><%=StringUtil.toHtml(user.getLogin())%>
            </cms:line>
            <cms:line label="_title"><%=StringUtil.toHtml(user.getTitle())%>
            </cms:line>
            <cms:line label="_firstName"><%=StringUtil.toHtml(user.getFirstName())%>
            </cms:line>
            <cms:line label="_lastName"><%=StringUtil.toHtml(user.getLastName())%>
            </cms:line>
            <cms:line label="_notes"><%=StringUtil.toHtml(user.getNotes())%>
            </cms:line>
            <cms:line label="_portrait"><% if (!user.getPortraitName().isEmpty()) {%><img src="/ctrl/user/showPortrait/<%=user.getId()%>" alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%>
            </cms:line>
            <h3><%=Strings.html("_address",locale)%>
            </h3>
            <cms:line label="_street"><%=StringUtil.toHtml(user.getStreet())%>
            </cms:line>
            <cms:line label="_zipCode"><%=StringUtil.toHtml(user.getZipCode())%>
            </cms:line>
            <cms:line label="_city"><%=StringUtil.toHtml(user.getCity())%>
            </cms:line>
            <cms:line label="_country"><%=StringUtil.toHtml(user.getCountry())%>
            </cms:line>
            <h3><%=Strings.html("_contact",locale)%>
            </h3>
            <cms:line label="_email"><%=StringUtil.toHtml(user.getEmail())%>
            </cms:line>
            <cms:line label="_phone"><%=StringUtil.toHtml(user.getPhone())%>
            </cms:line>
            <cms:line label="_fax"><%=StringUtil.toHtml(user.getFax())%>
            </cms:line>
            <cms:line label="_mobile"><%=StringUtil.toHtml(user.getMobile())%>
            </cms:line>
        </div>
    </section>
    <aside class="col-md-4 asideSection">
        <div class="section">
            <div class="paragraph">
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangePassword');"><%=Strings.html("_changePassword",locale)%>
                    </a>
                </div>
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/ctrl/user/openChangeProfile');"><%=Strings.html("_changeProfile",locale)%>
                    </a>
                </div>
            </div>
        </div>
    </aside>
</div>
