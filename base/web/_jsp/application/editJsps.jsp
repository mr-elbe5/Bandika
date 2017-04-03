<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="java.util.HashMap" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.Collections" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>

<%
  SessionData sdata = RequestHelper.getSessionData(request);
  @SuppressWarnings("unchecked")
  HashMap<String, String> jsps = (HashMap<String, String>) sdata.getParam("jsps");
  ArrayList<String> keys = new ArrayList<String>(jsps.keySet());
  Collections.sort(keys);
%>
<form class="form-horizontal" action="/_application" method="post" name="form" accept-charset="UTF-8">
  <div class="well">
    <input type="hidden" name="method" value="saveJsps"/>
    <legend><%=StringCache.getHtml("jsps")%>
    </legend>
    <div>
      <%for (String key : keys) {%>
      <bandika:controlGroup labelKey="<%=key%>" name="<%=key%>" mandatory="true">
        <input class="input-block-level" type="text" id="<%=key%>" name="<%=key%>" value="<%=FormatHelper.toHtml(jsps.get(key))%>" maxlength="255"/>
      </bandika:controlGroup>
      <%}%>
    </div>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
  </div>
</form>
