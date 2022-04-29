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
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.search.PageSearchData" %>
<%@ page import="de.elbe5.search.PageSearchResultData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    PageSearchResultData pageResult = rdata.getAttributes().get("searchResultData",PageSearchResultData.class);
    assert(pageResult!=null);
%>
<form:message/>
<section class="contentTop">
    <h1><%=$SH("_search")%>
    </h1>
    <form action="/ctrl/search/search" method="post" id="searchboxform" name="searchboxform" accept-charset="UTF-8">
        <div class="input-group">
            <label for="searchPattern"></label><input class="form-control mr-sm-2" id="searchPattern" name="searchPattern" maxlength="60" value="<%=$H(pageResult.getPattern())%>"/>
            <button class="btn btn-outline-primary my-2 my-sm-0" type="submit"><%=$SH("_search")%>
            </button>
        </div>
    </form>
</section>
<section class="searchSection">
    <div class="searchResults">
        <% if (!pageResult.getResults().isEmpty()) {%>
        <h2><%=$SH("_searchResults")%>
        </h2>
        <% for (PageSearchData data : pageResult.getResults()) {
            String description = data.getDescriptionContext();
            String content = data.getContentContext();%>
        <div class="searchResult">
            <div class="searchTitle">
                <a href="<%=data.getUrl()%>" title="<%=$SH("_show")%>"><%=data.getNameContext()%>
                </a>
            </div>
            <% if (!description.isEmpty()) {%>
            <div class="searchDescription"><%=data.getDescriptionContext()%>
            </div>
            <% }
                if (!content.isEmpty()) {%>
            <div class="searchContent"><%=data.getContentContext()%>
            </div>
            <% }%>
        </div>
        <% }
        } else {%>
        <span><%=$SH("_noResults")%></span>
        <%}%>
    </div>
</section>
