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
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika.template.TemplateBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.template.TemplateTypeData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  TemplateData data = (TemplateData) sdata.getParam("templateData");
  ArrayList<TemplateTypeData> templateTypes = TemplateBean.getInstance().getAllTemplateTypes();
%>
<form class="form-horizontal" action="/_template" method="post" name="form" accept-charset="UTF-8" enctype="multipart/form-data">
  <input type="hidden" name="method" value="saveTemplate"/>

  <div class="well">
    <legend><%=StringCache.getHtml("template")%>
    </legend>
    <table class="table">
      <%if (!data.isBeingCreated()) {%>
      <bandika:controlGroup labelKey="name" padded="true"><%=FormatHelper.toHtml(data.getName())%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="typeName" padded="true"><%=FormatHelper.toHtml(data.getTypeName())%>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="className" padded="true"><%=FormatHelper.toHtml(data.getClassName())%>
      </bandika:controlGroup>
      <%} else {%>
      <bandika:controlGroup labelKey="name" name="name" mandatory="true">
        <input class="input-block-level" type="text" id="name" name="name" value="<%=FormatHelper.toHtml(data.getName())%>" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="typeName" name="typeName" mandatory="true" padded="true">
        <select class="input-block-level" id="typeName" name="typeName">
          <% for (TemplateTypeData type : templateTypes) {%>
          <option value="<%=FormatHelper.toHtml(type.getName())%>" <%=data.getTypeName().equals(type.getName()) ? "selected" : ""%>><%=FormatHelper.toHtml(type.getName())%>
          </option>
          <%}%>
        </select>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="className" name="className" mandatory="false">
        <input class="input-block-level" type="text" id="className" name="className" value="<%=FormatHelper.toHtml(data.getClassName())%>" maxlength="120"/>
      </bandika:controlGroup>
      <%}%>
      <bandika:controlGroup labelKey="file" name="file" mandatory="false">
        <bandika:fileUpload name="file"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="download" name="download" padded="true">
        <a href="/_template?method=downloadTemplate&name=<%=data.getName()%>&typeName=<%=data.getTypeName()%>"><%=StringCache.getHtml("download")%></a>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="matchTypes" name="matchTypes" mandatory="false">
        <input class="input-block-level" type="text" id="matchTypes" name="matchTypes" value="<%=FormatHelper.toHtml(data.getMatchTypes())%>" maxlength="60"/>
      </bandika:controlGroup>
      <bandika:controlGroup labelKey="description" name="description" mandatory="false">
        <textarea class="input-block-level" id="description" name="description" rows="5" cols=""><%=FormatHelper.toHtmlInput(data.getDescription())%>
        </textarea>
      </bandika:controlGroup>
    </table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn" onclick="return linkTo('/_template?method=openEditTemplates');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>
