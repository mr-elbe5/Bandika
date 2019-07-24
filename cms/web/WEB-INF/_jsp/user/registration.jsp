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
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    UserData user = (UserData) rdata.get("userData");
    assert (user != null);
    String url = "/user/register/" + user.getId();
%>
<cms:message/>
<section class="contentTop">
    <h1>
        <%=Strings._register.html(locale)%>
    </h1>
</section>
<div class="contentSection">
    <cms:form url="<%=url%>" name="registerform">
        <div class="paragraph">
            <cms:formerror/>
            <cms:line
                    padded="true"><%=Strings._registrationHint.html(locale)%>&nbsp;<%=Strings._mandatoryHint.html(locale)%>
            </cms:line>
            <cms:text name="login" label="<%=Strings._loginName.toString()%>" required="true"
                      value="<%=StringUtil.toHtml(user.getLogin())%>"/>
            <cms:password name="password1" label="<%=Strings._password.toString()%>" required="true"/>
            <cms:password name="password2" label="<%=Strings._retypePassword.toString()%>" required="true"/>
            <cms:text name="title" label="<%=Strings._title.toString()%>"
                      value="<%=StringUtil.toHtml(user.getTitle())%>"/>
            <cms:text name="firstName" label="<%=Strings._firstName.toString()%>"
                      value="<%=StringUtil.toHtml(user.getFirstName())%>"/>
            <cms:text name="lastName" label="<%=Strings._lastName.toString()%>" required="true"
                      value="<%=StringUtil.toHtml(user.getLastName())%>"/>
            <h3><%=Strings._address.html(locale)%>
            </h3>
            <cms:text name="street" label="<%=Strings._street.toString()%>"
                      value="<%=StringUtil.toHtml(user.getStreet())%>"/>
            <cms:text name="zipCode" label="<%=Strings._zipCode.toString()%>"
                      value="<%=StringUtil.toHtml(user.getZipCode())%>"/>
            <cms:text name="city" label="<%=Strings._city.toString()%>" value="<%=StringUtil.toHtml(user.getCity())%>"/>
            <cms:text name="country" label="<%=Strings._country.toString()%>"
                      value="<%=StringUtil.toHtml(user.getCountry())%>"/>
            <h3><%=Strings._contact.html(locale)%>
            </h3>
            <cms:text name="email" label="<%=Strings._email.toString()%>" required="true"
                      value="<%=StringUtil.toHtml(user.getEmail())%>"/>
            <cms:text name="phone" label="<%=Strings._phone.toString()%>"
                      value="<%=StringUtil.toHtml(user.getPhone())%>"/>
            <cms:text name="fax" label="<%=Strings._fax.toString()%>" value="<%=StringUtil.toHtml(user.getFax())%>"/>
            <cms:text name="mobile" label="<%=Strings._mobile.toString()%>"
                      value="<%=StringUtil.toHtml(user.getMobile())%>"/>
            <hr/>
            <cms:line padded="true">
                <div class="imgBox left50">
                    <img id="captchaImg" src="/user/showCaptcha?timestamp=<%=new Date().getTime()%>" alt="captcha"/>
                    <%=Strings._captchaHint.htmlMultiline(locale)%>
                    <br/><br/>
                    <a class="link" href="#" onclick="return renewCaptcha();"><%=Strings._captchaRenew.html(locale)%>
                    </a>
                </div>
            </cms:line>
            <cms:text name="captcha" label="<%=Strings._captcha.toString()%>" value=""/>
        </div>
        <div>
            <button type="submit" class="btn btn-primary"><%=Strings._register.html(locale)%>
            </button>
        </div>
    </cms:form>
    <script type="text/javascript">
        function renewCaptcha() {
            $.ajax({
                url: '/user/renewCaptcha', type: 'POST', data: {}, cache: false, dataType: 'html'
            }).success(function (html, textStatus) {
                $('#captchaImg').attr("src", "/user/showCaptcha?timestamp=" + new Date().getTime());
            });
            return false;
        }
    </script>
</div>

