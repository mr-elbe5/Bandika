<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.DataCache" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    List<DataCache> caches = null;
    try {
        caches = DataCache.getAllCaches();
    } catch (Exception ignore) {
    }
%>
<form class="form-horizontal" action="/application.srv" method="post" name="form" accept-charset="UTF-8">
    <div class="well">
        <input type="hidden" name="act" value=""/>
        <legend><%=StringCache.getHtml("portal_caches",locale)%>
        </legend>
        <bandika:table id="cachesTable" checkId="name" formName="form" headerKeys="portal_name,portal_maxCount,portal_cacheCount">
            <%
                if (caches != null) {
                    for (DataCache cache : caches) {
            %>
            <tr>
                <td><input type="checkbox" name="name" value="<%=cache.getName()%>"/></td>
                <td><%=StringFormat.toHtml(cache.getName())%>
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
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary" onclick="return submitAction('clearCache');"><%=StringCache.getHtml("portal_clear",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('ensureCacheConsistency');"><%=StringCache.getHtml("portal_ensureConsistency",locale)%>
        </button>
    </div>
</form>

