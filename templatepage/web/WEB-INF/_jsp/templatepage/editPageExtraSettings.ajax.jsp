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
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.template.TemplateBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TemplatePageData pageData = (TemplatePageData) rdata.getCurrentPage();
    assert (pageData != null);
    List<String> templateNames = TemplateBean.getInstance().getTemplateNames(PageData.TYPE_PAGE_TEMPLATE);
%>
                <cms:select name="templateName" label="_pageTemplate" required="true">
                    <option value="" <%=pageData.getTemplateName().isEmpty() ? "selected" : ""%>><%=Strings.html("_pleaseSelect",locale)%>
                    </option>
                    <% for (String templateName : templateNames) {%>
                    <option value="<%=StringUtil.toHtml(templateName)%>" <%=templateName.equals(pageData.getTemplateName()) ? "selected" : ""%>><%=StringUtil.toHtml(templateName)%>
                    </option>
                    <%}%>
                </cms:select>
