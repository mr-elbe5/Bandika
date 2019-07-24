<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.search.PageSearchData" %>
<%@ page import="de.elbe5.cms.search.PageSearchResultData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageSearchResultData pageResult = (PageSearchResultData) rdata.get("searchResultData");
%>
<cms:message/>
<section class="contentTop">
    <h1><%=Strings._search.html(locale)%>
    </h1>
    <form action="/search/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
        <div class="input-group">
            <input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60"
                   value="<%=StringUtil.toHtml(pageResult.getPattern())%>"/>
            <button class="btn btn-outline-primary my-2 my-sm-0" type="submit"><%=Strings._search.html(locale)%>
            </button>
        </div>
    </form>
</section>
<section class="searchSection">
    <div class="searchResults">
        <% if (!pageResult.getResults().isEmpty()) {%>
        <h2><%=Strings._searchResults.html(locale)%>
        </h2>
        <%    for (PageSearchData data : pageResult.getResults()) {
                String description = data.getDescriptionContext();
                String content = data.getContentContext();
        %>
        <div class="searchResult">
            <div class="searchTitle"><a href="<%=data.getUrl()%>"
                                        title="<%=Strings._show.html(locale)%>"><%=data.getNameContext()%>
            </a>
            </div>
            <% if (!description.isEmpty()) {%>
            <div class="searchDescription"><%=data.getDescriptionContext()%>
            </div>
            <% }
                if (!content.isEmpty()) {
            %>
            <div class="searchContent"><%=data.getContentContext()%>
            </div>
            <% }%>
        </div>
        <% }
        } else {%>
        <span><%=Strings._noResults.html(locale)%></span>
        <%}%>
    </div>
</section>
