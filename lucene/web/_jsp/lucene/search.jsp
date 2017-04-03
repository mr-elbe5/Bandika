<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.lucene.SearchResultData" %>
<%@ page import="de.bandika.lucene.SearchData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale = sdata.getLocale();
  SearchResultData result = (SearchResultData) rdata.getParam("searchResultData");
%>
<form class="form-horizontal" action="/_lucenesearch" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="search">

  <div class="well">
    <legend><%=StringCache.getHtml("luceneSearch", locale)%>
    </legend>
    <div>
      <bandika:controlGroup labelKey="searchPattern" locale="<%=locale.getLanguage()%>" name="pattern" mandatory="true">
        <input class="input-block-level" type="text" id="pattern" name="pattern" value="<%=result==null ? "" : result.getPattern()%>" maxlength="60"/>
      </bandika:controlGroup>
    </div>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("search", locale)%>
    </button>
  </div>
</form>
<div class="spacer20">&nbsp;</div>
<% if (result != null && result.getResults() != null) {%>
<div>
  <legend><%=StringCache.getHtml("results")%>
  </legend>
  <div class="tableHeaderDiv">
    <table class="table">
      <tr>
        <th class="tableHeaderLeft"><%=StringCache.getHtml("res.type", locale)%>
        </th>
        <th class="tableHeaderSeparator"><%=StringCache.getHtml("res.name", locale)%>
        </th>
        <th class="tableHeaderSeparator"><%=StringCache.getHtml("res.content", locale)%>
        </th>
        <th class="tableHeaderSeparator"><%=StringCache.getHtml("res.relevance", locale)%>
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
      <td><img src="<%=FormatHelper.toHtml(data.getTypeIcon())%>" alt=""/></td>
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
