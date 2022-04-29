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
<%@ page import="de.elbe5.search.UserSearchData" %>
<%@ page import="de.elbe5.search.UserSearchResultData" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    UserSearchResultData userResult = rdata.getAttributes().get("searchResultData", UserSearchResultData.class);
%>
<section class="mainSection searchResults">
    <h1><%=$SH("_searchResults")%>
    </h1>
    <table class="padded searchResultsTable">
        <tr>
            <th class="col2"><%=$SH("_name")%>
            </th>
            <th class="col3"><%=$SH("_email")%>
            </th>
        </tr>
        <%if (userResult != null && !userResult.getResults().isEmpty()) {%>
        <tr> <%for (UserSearchData data : userResult.getResults()) {%>
        <tr>
            <td><%=data.getNameContext()%>
            </td>
            <td><%=data.getEmailContext()%>
            </td>
        </tr>
        <%
                }
            }
        %>
    </table>
</section>
