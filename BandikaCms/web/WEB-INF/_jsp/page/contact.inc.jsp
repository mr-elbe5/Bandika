<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    PageData contentData = rdata.getCurrentContent(PageData.class);
    assert contentData != null;
    String contactName = rdata.getString("contactName");
    String contactEmail = rdata.getString("contactEmail");
    String contactMessage = rdata.getString("contactMessage");
    String url = "/ctrl/page/sendContact/" + contentData.getId();
    boolean editing = contentData.isEditing();
%>
<% if (!editing) {%>
            <form:form url="<%=url%>" name="contactform" >
                <form:formerror/>
                <form:text name="contactName" label="_name" required="true" value="<%=$H(contactName)%>"/>
                <form:text name="contactEmail" label="_email" required="true" value="<%=$H(contactEmail)%>"/>
                <form:textarea name="contactMessage" label="_message" required="true" height="10rem"><%=$H(contactMessage)%>
                </form:textarea>
                <form:line label="" padded="true">
                    <img src="/ctrl/user/showCaptcha?v=<%=Long.toString(new Date().getTime())%>" alt="" />
                </form:line>
                <form:text name="captcha" required="true" label="_captcha" value=""/>
                <form:line label="" padded="true">
                    <div><%=$SH("_captchaHint", locale)%></div>
                </form:line>
                <div class="form-group row">
                    <div class = "col-md-12">
                        <button type="submit" class="btn btn-outline-primary pull-right"><%=$SH("_send",locale)%>
                        </button>
                    </div>
                </div>
            </form:form>
<%} else {%>
            <form:text name="contactName" label="_name" required="true" value="<%=$H(contactName)%>"/>
            <form:text name="contactEmail" label="_email" required="true" value="<%=$H(contactEmail)%>"/>
            <form:textarea name="contactMessage" label="_message" required="true" height="10rem"><%=$H(contactMessage)%>
            </form:textarea>
            <form:line label="" padded="true">
                <img src="/ctrl/user/showCaptcha?v=<%=Long.toString(new Date().getTime())%>" alt="" />
            </form:line>
            <form:text name="captcha" required="true" label="_captcha" value=""/>
            <form:line label="" padded="true">
                <div><%=$SH("_captchaHint", locale)%></div>
            </form:line>
            <div class="form-group">
                <button type="submit" class="btn btn-outline-primary pull-right" disabled><%=$SH("_send",locale)%>
                </button>
            </div>
<%}%>



