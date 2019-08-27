<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.search.UserSearchData" %>
<%@ page import="de.elbe5.search.UserSearchResultData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    UserSearchResultData userResult = (UserSearchResultData) rdata.get("searchResultData");
%>
<section class="mainSection searchResults">
    <h1><%=Strings.html("_searchResults",locale)%>
    </h1>
    <table class="padded searchResultsTable">
        <tr>
            <th class="col2"><%=Strings.html("_name",locale)%>
            </th>
            <th class="col3"><%=Strings.html("_email",locale)%>
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
