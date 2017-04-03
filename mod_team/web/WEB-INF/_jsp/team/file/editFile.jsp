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
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    TeamFilePartData cpdata = (TeamFilePartData) rdata.get("pagePartData");
    TeamFileData data = (TeamFileData) sdata.get("file");
    Locale locale = sdata.getLocale();
%>
<form class="form-horizontal" action="/teamfile.srv" method="post" name="teamfileform" accept-charset="UTF-8"
      enctype="multipart/form-data">
    <input type="hidden" name="act" value="saveFile"/>
    <input type="hidden" name="pageId" value="<%=cpdata.getPageId()%>"/>
    <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
    <input type="hidden" name="fid" value="<%=data.getId()%>"/>
    <legend><%=StringFormat.toHtml(cpdata.getTitle())%>
    </legend>
    <div>
        <% if (!data.isNew()) {%>
        <bandika:controlGroup labelKey="team_fileName" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=StringFormat.toHtml(data.getShortName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_owner" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=StringFormat.toHtml(data.getOwnerName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_author" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=StringFormat.toHtml(data.getAuthorName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_checkedoutby" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=StringFormat.toHtml(data.getCheckoutName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_changeDate" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=StringFormat.toHtmlDateTime(data.getChangeDate(),locale)%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_size" locale="<%=locale.getLanguage()%>"
                              padded="true"><%=String.valueOf(data.getSize() / 1024)%>&nbsp;kB</bandika:controlGroup>
        <%}%>
        <bandika:controlGroup labelKey="team_file" locale="<%=locale.getLanguage()%>" name="file" mandatory="true">
            <bandika:fileUpload name="file"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_name" locale="<%=locale.getLanguage()%>" name="name" mandatory="false">
            <input class="input-block-level" type="text" id="name" name="name"
                   value="<%=StringFormat.toHtml(data.getName())%>" maxlength="255"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="team_description" locale="<%=locale.getLanguage()%>" name="description"
                              mandatory="false">
            <textarea class="input-block-level" id="description" name="description" rows="5"
                      cols=""><%=StringFormat.toHtml(data.getDescription())%>
            </textarea>
        </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save", locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=show&pageId=<%=cpdata.getPageId()%>&fid=<%=data.getId()%>');"><%=StringCache.getHtml("webapp_back", locale)%>
        </button>
    </div>
</form>

