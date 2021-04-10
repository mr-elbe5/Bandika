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
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.file.ImageData" %>
<%@ page import="de.elbe5.content.ContentCache" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentSessionContent();
    assert contentData != null;
    int imageId=rdata.getInt("imageId");
    ImageData image = ContentCache.getFile(imageId,ImageData.class);
    assert(image!=null);
    if (contentData.hasUserReadRight(rdata)) {
%>
<li>
    <div class="treeline">
        <a id="<%=image.getId()%>" href="" onclick="return ckImgCallback('<%=image.getURL()%>');">
            <img src="/ctrl/image/showPreview/<%=image.getId()%>" alt="<%=$H(image.getDisplayName())%>"/>
            <%=$H(image.getDisplayName())%>
        </a>
        <a class="fa fa-eye" title="<%=$SH("_view",locale)%>" href="<%=image.getURL()%>" target="_blank"> </a>
    </div>
</li>
<%}%>
