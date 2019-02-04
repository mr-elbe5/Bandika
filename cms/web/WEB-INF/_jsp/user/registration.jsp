<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.UserActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="java.util.Date" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    UserData user = (UserData) request.getAttribute("userData");
    assert(user!=null);
%>
<cms:message />
<section class="contentTop">
    <h1>
        <%=Strings._register.html(locale)%>
    </h1>
</section>
<div class="contentSection">
    <cms:form url="/user.ajx" name="registerform" act="<%=UserActions.register%>">
        <div class="paragraph">
            <cms:requesterror/>
            <cms:line padded="true"><%=Strings._registrationHint.html(locale)%>&nbsp;<%=Strings._mandatoryHint.html(locale)%></cms:line>
            <cms:text name="login" label="<%=Strings._loginName.toString()%>" required="true"><%=StringUtil.toHtml(user.getLogin())%></cms:text>
            <cms:password name="password1" label="<%=Strings._password.toString()%>" required="true"></cms:password>
            <cms:password name="password2" label="<%=Strings._retypePassword.toString()%>" required="true"></cms:password>
            <cms:text name="title" label="<%=Strings._title.toString()%>"><%=StringUtil.toHtml(user.getTitle())%></cms:text>
            <cms:text name="firstName" label="<%=Strings._firstName.toString()%>"><%=StringUtil.toHtml(user.getFirstName())%></cms:text>
            <cms:text name="lastName" label="<%=Strings._lastName.toString()%>" required="true"><%=StringUtil.toHtml(user.getLastName())%></cms:text>
            <h3><%=Strings._address.html(locale)%></h3>
            <cms:text name="street" label="<%=Strings._street.toString()%>"><%=StringUtil.toHtml(user.getStreet())%></cms:text>
            <cms:text name="zipCode" label="<%=Strings._zipCode.toString()%>"><%=StringUtil.toHtml(user.getZipCode())%></cms:text>
            <cms:text name="city" label="<%=Strings._city.toString()%>"><%=StringUtil.toHtml(user.getCity())%></cms:text>
            <cms:text name="country" label="<%=Strings._country.toString()%>"><%=StringUtil.toHtml(user.getCountry())%></cms:text>
            <h3><%=Strings._contact.html(locale)%></h3>
            <cms:text name="email" label="<%=Strings._email.toString()%>" required="true"><%=StringUtil.toHtml(user.getEmail())%></cms:text>
            <cms:text name="phone" label="<%=Strings._phone.toString()%>"><%=StringUtil.toHtml(user.getPhone())%></cms:text>
            <cms:text name="fax" label="<%=Strings._fax.toString()%>"><%=StringUtil.toHtml(user.getFax())%></cms:text>
            <cms:text name="mobile" label="<%=Strings._mobile.toString()%>"><%=StringUtil.toHtml(user.getMobile())%></cms:text>
            <hr/>
            <cms:line padded="true">
                <div class="imgBox left50">
                    <img id="captchaImg" src="/user.srv?act=<%=UserActions.showCaptcha%>&timestamp=<%=new Date().getTime()%>" alt="captcha" />
                    <%=Strings._captchaHint.htmlMultiline(locale)%>
                    <br/><br/>
                    <a class="link" href="#" onclick="return renewCaptcha();"><%=Strings._captchaRenew.html(locale)%></a>
                </div>
            </cms:line>
            <cms:text name="captcha" label="<%=Strings._captcha.toString()%>"></cms:text>
        </div>
        <div>
            <button type="submit" class="btn btn-primary"><%=Strings._register.html(locale)%>
            </button>
        </div>
    </cms:form>
    <script type="text/javascript">
        function renewCaptcha(){
            $.ajax({
                url: '/user.ajx', type: 'POST', data: {act :'<%=UserActions.renewCaptcha%>'}, cache: false, dataType: 'html'
            }).success(function (html, textStatus) {
                $('#captchaImg').attr("src","/user.srv?act=<%=UserActions.showCaptcha%>&timestamp="+new Date().getTime());
            });
            return false;
        }
    </script>
</div>

