<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.module.ModuleData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  ModuleData data = (ModuleData) sdata.getParam("moduleData");
%>
<form class="form-horizontal" action="/_module" method="post" name="form" accept-charset="UTF-8" enctype="multipart/form-data">
  <input type="hidden" name="method" value="installModule"/>

  <div class="well">
    <legend><%=StringCache.getHtml("installModule")%>
    </legend>
    <div>
      <bandika:controlGroup labelKey="name" padded="true"><%=data.getName()%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="installType" padded="true"><%=data.isBeingCreated() ? StringCache.getHtml("newInstall") : StringCache.getHtml("update")%>
      </bandika:controlGroup>
    </div>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("execute")%>
    </button>
    <button class="btn" onclick="return linkTo('/_module?method=openEditModules');"><%=StringCache.getHtml("cancel")%>
    </button>
  </div>
</form>

