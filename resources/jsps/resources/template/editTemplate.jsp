<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.template.TemplateData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  TemplateData template = (TemplateData) sdata.getParam("templateData");
  String jspName = "/templates/tpl" + template.getId() + ".jsp";
  boolean preview = rdata.getParamBoolean("preview");
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_TEMPLATE%>"/>
  <input type="hidden" name="method" value=""/>

  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("id", sdata.getLocale())%>
        </td>
        <td class="adminRight"><%=template.getId()%>
        </td>
      </tr>
      <tr class="adminLine">
        <td class="adminLeft"><%=Strings.getHtml("description", sdata.getLocale())%>
        </td>
        <td class="adminRight"><input class="adminInput" type="text" name="description" maxlength="100"
                                      value="<%=Formatter.toHtml(template.getDescription())%>"/></td>
      </tr>
      <tr class="adminLine">
        <td colspan="2"><%=Strings.getHtml("html", sdata.getLocale())%>*</td>
      </tr>
      <tr class="adminLine">
        <td colspan="2"><textarea class="adminFullWidthInput" name="html" cols="120"
                                  rows="30"><%=Formatter.toHtmlInput(template.getHtml())%>
        </textarea></td>
      </tr>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a href="#" onClick="submitMethod('save');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_TEMPLATE%>&method=openEditTemplates"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>
  <% if (preview) {%>
  <div>
    <%try {%>
    <jsp:include page="<%=jspName%>" flush="true"/>
    <%
      } catch (Exception ignore) {
      }
    %>
  </div>
  <%}%>
</form>
