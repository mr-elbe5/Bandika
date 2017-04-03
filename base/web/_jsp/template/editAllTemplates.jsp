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
<%@ page import="de.bandika.template.TemplateData" %>
<%@ page import="de.bandika.template.TemplateBean" %>
<%@ page import="de.bandika.template.TemplateTypeData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  ArrayList<TemplateTypeData> templateTypes;
  ArrayList<TemplateData> templates;
  TemplateBean ts = TemplateBean.getInstance();
  try {
    templateTypes = ts.getAllTemplateTypes();
  } catch (Exception ignore) {
    templateTypes = new ArrayList<TemplateTypeData>();
  }
%>
<form class="form-horizontal" action="/_template" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>

  <div class="well">
    <legend><%=StringCache.getHtml("templates")%>
    </legend>
    <bandika:dataTable id="templateTable" checkId="tname" formName="form" headerKeys="name,type,matchTypes" sort="true" paging="true" displayLength="10">
      <% for (TemplateTypeData typeData : templateTypes) {
        templates = ts.getAllTemplates(typeData.getName());
        if (templates != null) {
          for (TemplateData template : templates) { %>
      <tr>
        <td><input type="checkbox" name="tname" value="<%=template.getName()+"#"+template.getTypeName()%>"/></td>
        <td>
          <a href="/_template?method=openEditTemplate&tname=<%=FormatHelper.encode(template.getName()+"#"+template.getTypeName())%>"><%=FormatHelper.toHtml(template.getName())%>
          </a></td>
        <td><%=FormatHelper.toHtml(template.getTypeName())%>
        </td>
        <td><%=FormatHelper.toHtml(template.getMatchTypes())%>
        </td>
      </tr>
      <%
            }
          }
        }
      %>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return linkTo('/_template?method=openCreateTemplate');"><%=StringCache.getHtml("new")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openEditTemplate');"><%=StringCache.getHtml("change")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteTemplates');"><%=StringCache.getHtml("delete")%>
    </button>
  </div>
</form>
