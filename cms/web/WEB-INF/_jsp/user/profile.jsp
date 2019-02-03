<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
%>
<cms:message />
<section class="contentTop">
    <h1>
        <%=Strings._profile.html(locale)%>
    </h1>
</section>
<div class="row">
    <section class="col-md-8 contentSection">
        <div class="paragraph">
            <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(user.getId())%></cms:line>
            <cms:line label="<%=Strings._login.toString()%>" ><%=StringUtil.toHtml(user.getLogin())%></cms:line>
            <cms:line label="<%=Strings._title.toString()%>"><%=StringUtil.toHtml(user.getTitle())%></cms:line>
            <cms:line label="<%=Strings._firstName.toString()%>"><%=StringUtil.toHtml(user.getFirstName())%></cms:line>
            <cms:line label="<%=Strings._lastName.toString()%>" ><%=StringUtil.toHtml(user.getLastName())%></cms:line>
            <cms:line label="<%=Strings._locale.toString()%>"><%=StringUtil.toHtml(user.getLocale().getLanguage())%></cms:line>
            <cms:line label="<%=Strings._notes.toString()%>"><%=StringUtil.toHtml(user.getNotes())%></cms:line>
            <cms:line label="<%=Strings._portrait.toString()%>"><% if (!user.getPortraitName().isEmpty()) {%><img
                    src="/user.srv?act=<%=UserActions.showPortrait%>&userId=<%=user.getId()%>"
                    alt="<%=StringUtil.toHtml(user.getName())%>"/> <%}%></cms:line>
            <h3><%=Strings._address.html(locale)%></h3>
            <cms:line label="<%=Strings._street.toString()%>"><%=StringUtil.toHtml(user.getStreet())%></cms:line>
            <cms:line label="<%=Strings._zipCode.toString()%>"><%=StringUtil.toHtml(user.getZipCode())%></cms:line>
            <cms:line label="<%=Strings._city.toString()%>"><%=StringUtil.toHtml(user.getCity())%></cms:line>
            <cms:line label="<%=Strings._country.toString()%>"><%=StringUtil.toHtml(user.getCountry())%></cms:line>
            <h3><%=Strings._contact.html(locale)%></h3>
            <cms:line label="<%=Strings._email.toString()%>" ><%=StringUtil.toHtml(user.getEmail())%></cms:line>
            <cms:line label="<%=Strings._phone.toString()%>"><%=StringUtil.toHtml(user.getPhone())%></cms:line>
            <cms:line label="<%=Strings._fax.toString()%>"><%=StringUtil.toHtml(user.getFax())%></cms:line>
            <cms:line label="<%=Strings._mobile.toString()%>"><%=StringUtil.toHtml(user.getMobile())%></cms:line>
        </div>
    </section>
    <aside class="col-md-4 asideSection">
        <div class="section">
            <div class="paragraph">
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openChangePassword%>');"><%=Strings._changePassword.html(locale)%>
                    </a>
                </div>
                <div>
                    <a class="link" href="#" onclick="return openModalDialog('/user.ajx?act=<%=UserActions.openChangeProfile%>');"><%=Strings._changeProfile.html(locale)%>
                    </a>
                </div>
            </div>
        </div>
    </aside>
</div>
