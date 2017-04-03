<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.GeneralRightsProvider" %>
<%@ page import = "de.elbe5.cms.page.PagePartData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.PagePartBean" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    List<PagePartData> parts = PagePartBean.getInstance().getAllSharedPagePartsWithUsages();
    int partId = RequestHelper.getInt(request, "partId");%><% if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {%>
<li<%=partId != 0 ? " class=\"open\"" : ""%>>
    <div class = "icn ipart"><%=StringUtil.getHtml("_sharedParts", locale)%>
    </div>
    <ul>
        <%if (parts != null) {
            for (PagePartData part : parts) {%>
        <li>
            <div class = "contextSource icn ipart <%=partId==part.getId() ? "selected" : ""%>"
                    onclick = "$('#properties').load('/pagepart.ajx?act=showSharedPartProperties&partId=<%=part.getId()%>')"><%=StringUtil.toHtml(part.getShareName())%>
            </div>
            <div class = "contextMenu">
                <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteSharedPart",locale)%>', '/pagepart.ajx?act=openDeleteSharedPart&partId=<%=part.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
            </div>
        </li>
        <%}
        }%>
    </ul>
</li>
<%}%>