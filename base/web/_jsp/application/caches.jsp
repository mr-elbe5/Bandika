<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.DataCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<DataCache> caches = null;
  try {
    caches = DataCache.getAllCaches();
  } catch (Exception ignore) {
  }
%>
<form class="form-horizontal" action="/_application" method="post" name="form" accept-charset="UTF-8">
  <div class="well">
    <input type="hidden" name="method" value=""/>
    <legend><%=StringCache.getHtml("caches")%>
    </legend>
    <bandika:dataTable id="cachesTable" checkId="name" formName="form" headerKeys="name,maxCount,cacheCount">
      <%
        if (caches != null) {
          for (DataCache cache : caches) {
      %>
      <tr>
        <td><input type="checkbox" name="name" value="<%=cache.getName()%>"/></td>
        <td><%=FormatHelper.toHtml(cache.getName())%>
        </td>
        <td><%=cache.getMaxCount()%>
        </td>
        <td><%=cache.getCacheCount()%>
        </td>
      </tr>
      <%
          }
        }
      %>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitMethod('clearCache');"><%=StringCache.getHtml("clear")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('ensureCacheConsistency');"><%=StringCache.getHtml("ensureConsistency")%>
    </button>
  </div>
</form>

