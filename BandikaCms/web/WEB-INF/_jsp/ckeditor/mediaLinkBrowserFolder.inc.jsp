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
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.file.MediaData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getRequestObject(RequestData.KEY_CONTENT,ContentData.class);
    assert contentData != null;%>
<li class="open">
    <a id="<%=contentData.getId()%>"><%=contentData.getName()%>
    </a>
    <ul>
        <% if (contentData.hasUserReadRight(rdata)){
            List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
            for (MediaData media : mediaFiles) {
        %>
        <li>
            <div class="treeline">
                <a id="<%=media.getId()%>" href="" onclick="return ckLinkCallback('<%=media.getURL()%>');">
                    <%=$H(media.getDisplayName())%>
                </a>
                <a class="fa fa-eye" title="<%=$SH("_show",locale)%>" href="<%=media.getURL()%>"> </a>
            </div>
        </li>
        <%}
        }
        for (ContentData subFolder : contentData.getChildren()) {
            rdata.setRequestObject(RequestData.KEY_CONTENT, subFolder);%>
        <jsp:include page="/WEB-INF/_jsp/ckeditor/mediaLinkBrowserFolder.inc.jsp" flush="true"/>
        <% }
        rdata.setRequestObject(RequestData.KEY_CONTENT, contentData);
        %>
    </ul>
</li>

