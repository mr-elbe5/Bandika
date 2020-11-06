<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2020 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.file.MediaData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    ContentData contentData = rdata.getCurrentContent();
    assert contentData != null;
    List<String> mediaTypes =contentData.getMediaClasses();
    int fileId=rdata.getInt("fileId");
%>
        <li class="media open">
            <span>[<%=$SH("_media", locale)%>]</span>
            <%if (contentData.hasUserEditRight(rdata)) {%>
            <div class="icons">
                <% if (rdata.hasClipboardData(RequestData.KEY_MEDIA)) {%>
                <a class="icon fa fa-paste" href="/ctrl/media/pasteMedia?parentId=<%=contentData.getId()%>" title="<%=$SH("_pasteMedia",locale)%>"> </a>
                <%}
                    if (!mediaTypes.isEmpty()) {%>
                <a class="icon fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newMedia",locale)%>"></a>
                <div class="dropdown-menu">
                    <%for (String mediaType : mediaTypes) {
                        String name = $SH("class."+mediaType, locale);%>
                    <a class="dropdown-item" onclick="return openModalDialog('/ctrl/media/openCreateMedia?parentId=<%=contentData.getId()%>&type=<%=mediaType%>');"><%=name%>
                    </a>
                    <%
                        }%>
                </div>
                <%
                    }%>
            </div>
            <%}%>
            <ul>
                <%
                    List<MediaData> mediaFiles = contentData.getFiles(MediaData.class);
                    for (MediaData media : mediaFiles) {%>
                <li class="<%=fileId==media.getId() ? "current" : ""%>">
                    <div class="treeline">
                        <span id="<%=media.getId()%>">
                            <%=media.getDisplayName()%>
                        </span>
                        <div class="icons">
                            <a class="icon fa fa-eye" href="<%=media.getURL()%>" target="_blank" title="<%=$SH("_view",locale)%>"> </a>
                            <a class="icon fa fa-download" href="<%=media.getURL()%>?download=true" title="<%=$SH("_download",locale)%>"> </a>
                            <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/media/openEditMedia/<%=media.getId()%>');" title="<%=$SH("_edit",locale)%>"> </a>
                            <a class="icon fa fa-scissors" href="" onclick="return linkTo('/ctrl/media/cutMedia/<%=media.getId()%>');" title="<%=$SH("_cut",locale)%>"> </a>
                            <a class="icon fa fa-copy" href="" onclick="return linkTo('/ctrl/media/copyMedia/<%=media.getId()%>');" title="<%=$SH("_copy",locale)%>"> </a>
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/media/deleteMedia/<%=media.getId()%>');" title="<%=$SH("_delete",locale)%>"> </a>
                        </div>
                    </div>
                </li>
                <%}%>
            </ul>
        </li>


