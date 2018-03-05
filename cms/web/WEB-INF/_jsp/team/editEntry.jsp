<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.team.TeamBlogPartData" %>
<%@ page import="de.bandika.cms.team.TeamBlogEntryData" %>
<%@ page import="de.bandika.cms.team.TeamBlogBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%
    Locale locale= SessionReader.getSessionLocale(request);
    PageData data=(PageData) request.getAttribute("pageData");
    TeamBlogPartData cpdata = (TeamBlogPartData) request.getAttribute("pagePartData");
    TeamBlogEntryData editEntry = (TeamBlogEntryData) SessionReader.getSessionObject(request, "entry");
    List<TeamBlogEntryData> entries = TeamBlogBean.getInstance().getEntryList(cpdata.getId());
%>
<form class="form-horizontal" action="/teamblog.srv" method="post" name="teamblogform" accept-charset="UTF-8"
      enctype="multipart/form-data">
    <input type="hidden" name="act" value="saveEntry"/>
    <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
    <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
    <input type="hidden" name="eid" value="<%=editEntry.getId()%>"/>
    <legend><%=StringUtil.toHtml(cpdata.getTitle())%>
    </legend>
    <% for (TeamBlogEntryData entryData : entries) {%>
    <div class="blogEntry">
        <div class="blogEntryTitle"><%=StringUtil.toHtml(entryData.getTitle())%>
            (<%=StringUtil.toHtml(entryData.getAuthorName())%>
            ) <%=StringUtil.toHtmlDateTime(entryData.getChangeDate(),locale)%>
        </div>
        <div class="blogEntryText"><%=StringUtil.toHtml(entryData.getText())%>
        </div>
    </div>
    <%}%>
    <div class="blogEntry">
        <div class="blogEntryTitle"><input type="text" name="title"
                                           value="<%=StringUtil.toHtml(editEntry.getTitle())%>"/>
        </div>
        <div class="blogEntryText"><textarea name="text" cols="40"
                                             rows="5"><%=StringUtil.toHtml(editEntry.getText())%>
        </textarea></div>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringUtil.getHtml("webapp_save", locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=show&pageId=<%=data.getId()%>');"><%=StringUtil.getHtml("webapp_back", locale)%>
        </button>
    </div>
</form>

