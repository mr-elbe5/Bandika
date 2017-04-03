<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%@ page import="de.bandika.application.StringCache" %>
<form class="form-horizontal" action="/_module" method="post" name="form" accept-charset="UTF-8" enctype="multipart/form-data">
  <input type="hidden" name="method" value="uploadModule"/>

  <div class="well">
    <legend><%=StringCache.getHtml("uploadModule")%>
    </legend>
    <div>
      <bandika:controlGroup labelKey="moduleFile" name="moduleFile" mandatory="true">
        <bandika:fileUpload name="moduleFile"/>
      </bandika:controlGroup>
    </div>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("upload")%>
    </button>
    <button class="btn" onclick="return linkTo('/_module?method=openEditModules');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>

