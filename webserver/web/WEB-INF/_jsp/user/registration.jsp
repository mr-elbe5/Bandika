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
<%@ page import="de.elbe5.user.UserData" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    UserData user = (UserData) rdata.get("userData");
    assert (user != null);
    String url = "/ctrl/user/register/" + user.getId();
%>
<cms:message/>
<section class="contentTop">
    <h1>
        <%=Strings.html("_register",locale)%>
    </h1>
</section>
<div class="contentSection">
    <cms:form url="<%=url%>" name="registerform">
        <div class="paragraph">
            <cms:formerror/>
            <cms:line padded="true"><%=Strings.html("_registrationHint",locale)%>&nbsp;<%=Strings.html("_mandatoryHint",locale)%>
            </cms:line>
            <cms:text name="login" label="_loginName" required="true" value="<%=StringUtil.toHtml(user.getLogin())%>"/>
            <cms:password name="password1" label="_password" required="true"/>
            <cms:password name="password2" label="_retypePassword" required="true"/>
            <cms:text name="title" label="_title" value="<%=StringUtil.toHtml(user.getTitle())%>"/>
            <cms:text name="firstName" label="_firstName" value="<%=StringUtil.toHtml(user.getFirstName())%>"/>
            <cms:text name="lastName" label="_lastName" required="true" value="<%=StringUtil.toHtml(user.getLastName())%>"/>
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
            <hr/>
            <cms:line padded="true">
                <div class="imgBox left50">
                    <img id="captchaImg" src="/ctrl/user/showCaptcha?timestamp=<%=new Date().getTime()%>" alt="captcha"/>
                    <%=Strings.htmlMultiline("_captchaHint",locale)%>
                    <br/><br/>
                    <a class="link" href="#" onclick="return renewCaptcha();"><%=Strings.html("_captchaRenew",locale)%>
                    </a>
                </div>
            </cms:line>
            <cms:text name="captcha" label="_captcha" value=""/>
        </div>
        <div>
            <button type="submit" class="btn btn-primary"><%=Strings.html("_register",locale)%>
            </button>
        </div>
    </cms:form>
    <script type="text/javascript">
        function renewCaptcha() {
            $.ajax({
                url: '/ctrl/user/renewCaptcha',
                type: 'POST',
                data: {},
                cache: false,
                dataType: 'html'
            }).success(function (html, textStatus) {
                $('#captchaImg').attr("src", "/ctrl/user/showCaptcha?timestamp=" + new Date().getTime());
            });
            return false;
        }
    </script>
</div>

