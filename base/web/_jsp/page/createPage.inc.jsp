<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  ArrayList<TemplateData> layoutTemplates = TemplateCache.getInstance().getTemplates("layout");
  int id = rdata.getCurrentPageId();
  boolean adminLayer = rdata.getParamBoolean("adminLayer");
%>
<div class="layerContent">
  <form class="form-horizontal" action="/_page" method="post" name="layoutform" accept-charset="UTF-8">
    <div class="well">
      <input type="hidden" name="id" value="<%=id%>"/>
      <input type="hidden" name="adminLayer" value="<%=adminLayer ? "1" : "0"%>"/>
      <input type="hidden" name="method" value="createPage"/>
      <bandika:controlGroup labelKey="name" name="name" mandatory="true">
        <input class="input-block-level" type="text" id="name" name="name" value="" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="path" name="path" mandatory="true">
        <input class="input-block-level" type="text" id="path" name="path" value="" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="layout" padded="true">
        <%
          boolean first = true;
          for (TemplateData tdata : layoutTemplates) {
        %>
        <div>
          <input type="radio" name="layout" value="<%=FormatHelper.toHtml(tdata.getName())%>" <%=first ? "checked=\"checked\"" : ""%>/><%=FormatHelper.toHtml(tdata.getName())%>
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
