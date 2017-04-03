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
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.file.DocumentData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    int pageId=rdata.getInt("pageId");
    DocumentData data = (DocumentData) sdata.get("file");
%>
<form class="form-horizontal" action="/document.srv" method="post" name="form" accept-charset="UTF-8"
      enctype="multipart/form-data">
    <input type="hidden" name="act" value="saveDocument"/>
    <input type="hidden" name="pageId" value="<%=pageId%>"/>
    <input type="hidden" name="fid" value="<%=data.getId()%>"/>

    <div class="well">
        <legend><%=rdata.getTitle()%>
        </legend>
        <% if (!data.isNew()) { %>
        <bandika:controlGroup labelKey="portal_document">
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_fileName" padded="true"><%=StringFormat.toHtml(data.getFileName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_author" padded="true"><%=StringFormat.toHtml(data.getAuthorName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_changeDate" padded="true"><%=StringFormat.toHtmlDateTime(data.getChangeDate(),locale)%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_size"
                              padded="true"><%=String.valueOf(data.getSize() / 1024)%>&nbsp;kB</bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_assignedPage"
                              padded="true"><%=data.isExclusive() ? String.valueOf(data.getPageId()) : "-"%>
        </bandika:controlGroup>
        <%}%>
        <bandika:controlGroup labelKey="portal_file" name="file" mandatory="true">
            <bandika:fileUpload name="file"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_nameReplacement" name="name" mandatory="false">
            <input class="input-block-level" type="text" id="name" name="name"
                   value="<%=StringFormat.toHtml(data.getFileNameWithoutExtension())%>" maxlength="255"/>
        </bandika:controlGroup>
        <%
            if (pageId != 0) {
        %>
        <bandika:controlGroup labelKey="portal_exclusive" name="exclusive">
            <input class="input-block-level" type="checkbox" id="exclusive" name="exclusive"
                   value="1" <%=data.isExclusive() ? "checked" : ""%>/>
        </bandika:controlGroup>
        <%}%>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/document.srv?act=reopenDefaultPage&pageId=<%=pageId%>');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>

