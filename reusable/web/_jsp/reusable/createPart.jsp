<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  String matchTypes = rdata.getParamString("areaMatchTypes");
  ArrayList<TemplateData> templates = TemplateCache.getInstance().getMatchingTemplates("part", matchTypes);
%>
<div class="layerContent">
  <form class="form-horizontal" action="/_reusable" method="post" name="layoutform" accept-charset="UTF-8">
    <input type="hidden" name="partId" value="0"/>
    <input type="hidden" name="method" value="createReusablePart"/>

    <div class="well">
      <bandika:controlGroup labelKey="name" name="name" mandatory="true">
        <input class="input-block-level" type="text" id="name" name="name" value="" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="layout" padded="true">
        <%
          boolean first = true;
          for (TemplateData tdata : templates) {
        %>
        <div>
          <input type="radio" name="template" value="<%=FormatHelper.toHtml(tdata.getName())%>" <%=first ? "checked=\"checked\"" : ""%>/><%=FormatHelper.toHtml(tdata.getName())%>
        </div>
        <%
            first = false;
          }
        %>
      </bandika:controlGroup>
    </div>
    <div class="btn-toolbar">
      <button class="btn btn-primary" onclick="document.layoutform.submit();"><%=StringCache.getHtml("create")%>
      </button>
    </div>
  </form>
</div>
