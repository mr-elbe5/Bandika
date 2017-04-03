<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale=sdata.getLocale();
  PageData data = (PageData) sdata.get("pageData");
  PagePartData part = data.getEditPagePart();
%>
<div class="layerContent">
  <form class="form-horizontal" action="/page.srv" method="post" name="shareform" accept-charset="UTF-8">
    <div class="well">
      <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
      <input type="hidden" name="partId" value="<%=part.getId()%>"/>
      <input type="hidden" name="act" value="sharePagePart"/>
      <bandika:controlGroup labelKey="portal_name" name="name" mandatory="true">
        <input class="input-block-level" type="text" id="name" name="name" value="" maxlength="60"/>
      </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.shareform.submit();"><%=StringCache.getHtml("webapp_share", locale)%>
      </button>
    </div>
  </form>
</div>
