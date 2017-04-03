<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.template.LayoutTemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    PageData data = (PageData) rdata.get("pageData");
    List<LayoutTemplateData> layoutTemplates = TemplateCache.getInstance().getLayoutTemplates();
    int pageId = rdata.getInt("pageId");
%>
<div class="layerContent">
    <table class="table">
        <colgroup>
            <col style="width:30%">
            <col style="width:70%">
        </colgroup>
        <thead>
        <tr>
            <th><%=StringCache.getHtml("portal_name",locale)%>
            </th>
            <th><%=StringCache.getHtml("portal_description",locale)%>
            </th>
        </tr>
        </thead>
        <tbody>
        <% for (LayoutTemplateData tdata : layoutTemplates) {%>
        <tr <%=tdata.getName().equals(data.getLayoutTemplate()) ? "class=\"info\"" : ""%>>
            <td>
                <a href="/page.srv?act=changeLayout&pageId=<%=pageId%>&layout=<%=StringFormat.encode(tdata.getName())%>"><%=StringFormat.toHtml(tdata.getName())%>
                </a></td>
            <td><%=StringFormat.toHtml(tdata.getDescription())%>
            </td>
        </tr>
        <%}%>
        </tbody>
    </table>
</div>


