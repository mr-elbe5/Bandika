<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PagePartData" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.page.PageBean" %>
<%@ page import="de.bandika.cms.page.PagePartActions" %>
<%
    if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
        Locale locale = SessionReader.getSessionLocale(request);
        List<PagePartData> parts = PageBean.getInstance().getAllSharedPagePartsWithUsages();
        int partId = RequestReader.getInt(request, "partId");
%>
<li<%=partId != 0 ? " class=\"open\"" : ""%>>
    <div class="icn ipart"><%=StringUtil.getHtml("_sharedParts", locale)%>
    </div>
    <ul>
        <%
            if (parts != null) {
                for (PagePartData part : parts) {
        %>
        <li>
            <div class="contextSource icn ipart <%=partId==part.getId() ? "selected" : ""%>" onclick="$('#details').load('/pageadmin.ajx?act=<%=PagePartActions.showSharedPartDetails%>&partId=<%=part.getId()%>')"><%=StringUtil.toHtml(part.getShareName())%>
            </div>
            <div class="contextMenu">
                <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteSharedPart",locale)%>', '/pageadmin.ajx?act=<%=PagePartActions.openDeleteSharedPart%>&partId=<%=part.getId()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                </div>
            </div>
        </li>
        <%
                }
            }
        %>
    </ul>
</li>
<%}%>