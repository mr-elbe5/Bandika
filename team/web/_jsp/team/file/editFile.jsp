<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  TeamFilePartData cpdata = (TeamFilePartData) rdata.getParam("pagePartData");
  TeamFileData data = (TeamFileData) sdata.getParam("file");
  Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/_teamfile" method="post" name="teamfileform" accept-charset="UTF-8" enctype="multipart/form-data">
  <input type="hidden" name="method" value="saveFile"/>
  <input type="hidden" name="id" value="<%=cpdata.getPageId()%>"/>
  <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
  <input type="hidden" name="fid" value="<%=data.getId()%>"/>
  <legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
  </legend>
  <div>
    <% if (!data.isBeingCreated()) {%>
    <bandika:controlGroup labelKey="fileName" locale="<%=locale.getLanguage()%>" padded="true"><%=FormatHelper.toHtml(data.getShortName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="owner" locale="<%=locale.getLanguage()%>" padded="true"><%=FormatHelper.toHtml(data.getOwnerName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="author" locale="<%=locale.getLanguage()%>" padded="true"><%=FormatHelper.toHtml(data.getAuthorName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="checkedoutby" locale="<%=locale.getLanguage()%>" padded="true"><%=FormatHelper.toHtml(data.getCheckoutName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="changeDate" locale="<%=locale.getLanguage()%>" padded="true"><%=FormatHelper.toHtmlDateTime(data.getChangeDate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="size" locale="<%=locale.getLanguage()%>" padded="true"><%=String.valueOf(data.getSize() / 1024)%>&nbsp;kB</bandika:controlGroup>
    <%}%>
    <bandika:controlGroup labelKey="file" locale="<%=locale.getLanguage()%>" name="file" mandatory="true">
      <bandika:fileUpload name="file"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="name" locale="<%=locale.getLanguage()%>" name="name" mandatory="false">
      <input class="input-block-level" type="text" id="name" name="name" value="<%=FormatHelper.toHtml(data.getName())%>" maxlength="255"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="description" locale="<%=locale.getLanguage()%>" name="description" mandatory="false">
      <textarea class="input-block-level" id="description" name="description" rows="5" cols=""><%=FormatHelper.toHtml(data.getDescription())%>
      </textarea>
    </bandika:controlGroup>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.teamfileform.submit();return false;"><%=StringCache.getHtml("save", locale)%>
    </button>
    <button class="btn" onclick="return linkTo('/_page?method=show&id=<%=cpdata.getPageId()%>&fid=<%=data.getId()%>');"><%=StringCache.getHtml("back", locale)%>
    </button>
  </div>
</form>

