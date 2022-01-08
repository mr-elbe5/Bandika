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
<%@ page import="de.elbe5.file.DocumentData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.request.RequestData" %>
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
            List<DocumentData> documents = contentData.getFiles(DocumentData.class);
            for (DocumentData document : documents) {
        %>
        <li>
            <div class="treeline">
                <a id="<%=document.getId()%>" href="" onclick="return ckLinkCallback('<%=document.getURL()%>?download=true');">
                    <%=$H(document.getDisplayName())%>
                </a>
                <a class="fa fa-eye" title="<%=$SH("_download",locale)%>" href="<%=document.getURL()%>?download=true"> </a>
            </div>
        </li>
        <%}
        }
        for (ContentData subFolder : contentData.getChildren()) {
            rdata.setRequestObject(RequestData.KEY_CONTENT, subFolder);%>
        <jsp:include page="/WEB-INF/_jsp/ckeditor/documentLinkBrowserFolder.inc.jsp" flush="true"/>
        <% }
        rdata.setRequestObject(RequestData.KEY_CONTENT, contentData);
        %>
    </ul>
</li>

