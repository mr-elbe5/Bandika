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
<%@ page import="de.bandika.search.SearchData" %>
<%@ page import="de.bandika.search.SearchResultData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale = sdata.getLocale();
    SearchResultData result = (SearchResultData) rdata.get("searchResultData");
%>
<form class="form-horizontal" action="/lucenesearch.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="search">

    <div class="well">
        <legend><%=StringCache.getHtml("lucene_search", locale)%>
        </legend>
        <div>
            <bandika:controlGroup labelKey="lucene_searchPattern" locale="<%=locale.getLanguage()%>" name="pattern"
                                  mandatory="true">
                <input class="input-block-level" type="text" id="pattern" name="pattern"
                       value="<%=result==null ? "" : result.getPattern()%>" maxlength="60"/>
            </bandika:controlGroup>
        </div>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("lucene_search", locale)%>
        </button>
    </div>
</form>
<div class="spacer20">&nbsp;</div>
<% if (result != null && result.getResults() != null) {%>
<div>
    <legend><%=StringCache.getHtml("lucene_results",locale)%>
    </legend>
    <div class="tableHeaderDiv">
        <table class="table">
            <tr>
                <th class="tableHeaderLeft"><%=StringCache.getHtml("lucene_type", locale)%>
                </th>
                <th class="tableHeaderSeparator"><%=StringCache.getHtml("lucene_name", locale)%>
                </th>
                <th class="tableHeaderSeparator"><%=StringCache.getHtml("lucene_content", locale)%>
                </th>
                <th class="tableHeaderSeparator"><%=StringCache.getHtml("lucene_relevance", locale)%>
                </th>
            </tr>
        </table>
    </div>
    <table class="table">
        <%
            for (SearchData data : result.getResults()) {
                String link = data.getLink();
        %>
        <tr>
            <td><img src="<%=StringFormat.toHtml(data.getTypeIcon())%>" alt=""/></td>
            <td><% if (link != null) {%><a <%=link%>><%=data.getNameContext()%>
            </a><%} else {%><%=data.getNameContext()%><%}%></td>
            <td><%=data.getContentContext()%>
            </td>
            <td><%=data.getScore()%>
            </td>
        </tr>
        <%}%>
    </table>
</div>
<%}%>
<script type="text/javascript">
    document.form.pattern.focus();
</script>
