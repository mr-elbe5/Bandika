<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.user.UserBean" %>
<%@ page import="de.bandika.cms.user.UserData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.user.UserActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    UserData user = UserBean.getInstance().getUser(SessionReader.getSessionLoginData(request).getId());
%>
<section class="mainSection">
    <div class="flexRow">
        <section class="contentSection flexItem three">
            <div class="section">
                <div class="details">
                    <h3><%=StringUtil.getString("_profile", locale)%>
                    </h3>
                    <table class="padded details lined">
                        <tr>
                            <td><label><%=StringUtil.getHtml("_name", locale)%>
                            </label></td>
                            <td><%=StringUtil.toHtml(user.getName())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_street", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getStreet())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_zipCode", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getZipCode())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_city", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getCity())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_country", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getCountry())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_locale", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getLocale().getLanguage())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_email", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getEmail())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_phone", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getPhone())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_fax", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getFax())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_mobile", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getMobile())%>
                            </td>
                        </tr>
                        <tr>
                            <td>
                                <label><%=StringUtil.getHtml("_notes", locale)%>
                                </label></td>
                            <td><%=StringUtil.toHtml(user.getNotes())%>
                            </td>
                        </tr>
                    </table>
                </div>
            </div>
        </section>
        <aside class="asideSection flexItem one">
            <nav class="section">
                <ul class="linkList">
                    <li>
                        <div class="icn ipassword" onclick="return openLayerDialog('<%=StringUtil.getHtml("_changePassword",locale)%>', '/user.ajx?act=<%=UserActions.openChangePassword%>');"><%=StringUtil.getHtml("_changePassword", locale)%>
                        </div>
                    </li>
                    <li>
                        <div class="icn iprofile" onclick="return openLayerDialog('<%=StringUtil.getHtml("_changeProfile",locale)%>', '/user.ajx?act=<%=UserActions.openChangeProfile%>');"><%=StringUtil.getHtml("_changeProfile", locale)%>
                        </div>
                    </li>
                </ul>
            </nav>
        </aside>
    </div>
</section>