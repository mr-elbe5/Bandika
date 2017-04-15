<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.search.*" %>
<%@ page import="de.bandika.cms.search.ContentSearchData" %>
<%@ page import="de.bandika.cms.search.UserSearchData" %>
<%@ page import="de.bandika.cms.search.UserSearchResultData" %>
<%@ page import="de.bandika.cms.search.ContentSearchResultData" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    ContentSearchResultData contentResult = (ContentSearchResultData) request.getAttribute("contentSearchResultData");
    UserSearchResultData userResult = (UserSearchResultData) request.getAttribute("userSearchResultData");
%>
<section class="mainSection searchResults">
    <h1><%=StringUtil.getHtml("_searchResults")%>
    </h1>
    <table class="padded searchResultsTable">
        <tr>
            <th class="col1"><%=StringUtil.getHtml("_type", locale)%>
            </th>
            <th class="col2"><%=StringUtil.getHtml("_name", locale)%>
            </th>
            <th class="col3"><%=StringUtil.getHtml("_description", locale)%>
            </th>
            <th class="col4"><%=StringUtil.getHtml("_content", locale)%>
            </th>
            <th class="col5"><%=StringUtil.getHtml("_relevance", locale)%>
            </th>
        </tr>
        <%
            if (userResult!=null && !userResult.getResults().isEmpty()){%>
        <tr>
            <td colspan="5"><h3><%=StringUtil.getHtml("_users")%></h3>
            </td>
                <%for (UserSearchData data : userResult.getResults()) {%>
        <tr>
            <td><%=data.getIconSpan(locale)%>
            </td>
            <td><%=data.getNameSpan(locale)%><%=data.getInfoSpan(locale)%>
            </td>
            <td>
            </td>
            <td>
            </td>
            <td><%=data.getScore()%>
            </td>
        </tr>
        <%}}
        if (contentResult!=null && !contentResult.getResults().isEmpty()){%>
        <tr>
            <td colspan="5"><h3><%=StringUtil.getHtml("_content")%></h3>
            </td>
        <% for (ContentSearchData data : contentResult.getResults()) {%>
        <tr>
            <td><%=data.getIconSpan(locale)%>
            </td>
            <td><%=data.getNameSpan(locale)%><%=data.getInfoSpan(locale)%>
            </td>
            <td><%=data.getDescriptionContext()%>
            </td>
            <td><%=data.getContentContext()%>
            </td>
            <td><%=data.getScore()%>
            </td>
        </tr>
        <%}}%>
    </table>
</section>
