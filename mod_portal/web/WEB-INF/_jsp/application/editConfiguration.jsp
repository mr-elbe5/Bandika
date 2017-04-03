<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.*" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>

<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    @SuppressWarnings("unchecked")
    Map<String, String> configs = (HashMap<String, String>) sdata.get("configs");
    List<String> keys = new ArrayList<>(configs.keySet());
    Collections.sort(keys);
%>
<form class="form-horizontal" action="/application.srv" method="post" name="form" accept-charset="UTF-8">
    <div class="well">
        <input type="hidden" name="act" value="saveConfiguration"/>
        <legend><%=StringCache.getHtml("portal_configuration",locale)%>
        </legend>
        <div>
            <%for (String key : keys) {
            String label="conf_"+key; %>
            <bandika:controlGroup labelKey="<%=label%>" name="<%=key%>" mandatory="true">
                <input class="input-block-level" type="text" id="<%=key%>" name="<%=key%>"
                       value="<%=StringFormat.toHtml(configs.get(key))%>" maxlength="255"/>
            </bandika:controlGroup>
            <%}%>
        </div>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
    </div>
</form>
