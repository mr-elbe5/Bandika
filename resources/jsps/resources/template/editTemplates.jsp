<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.base.Formatter" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.template.TemplateBean" %>
<%@ page import="de.net25.resources.template.TemplateData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  ArrayList<TemplateData> templates = null;
  try {
    TemplateBean ts = (TemplateBean) Statics.getBean(Statics.KEY_TEMPLATE);
    templates = ts.getTemplateList();
  } catch (Exception ignore) {
  }
%>
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_TEMPLATE%>"/>
  <input type="hidden" name="method" value=""/>

  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderLeftCol"><%=Strings.getHtml("id", sdata.getLocale())%></td>
        <td class="adminHeader2RightCol"><%=Strings.getHtml("description", sdata.getLocale())%>
        </td>
      </tr>
      <% boolean otherLine = false;
        if (templates != null) {
          for (TemplateData template : templates) {
            otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><input type="radio" name="tid" value="<%=template.getId()%>"/></td>
        <td><%=template.getId()%>
        </td>
        <td><%=Formatter.toHtml(template.getDescription())%>
        </td>
      </tr>
      <%
          }
        }
      %>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show"><%=Strings.getHtml("back", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_TEMPLATE%>&method=openCreate"><%=Strings.getHtml("new", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('openEdit');"><%=Strings.getHtml("change", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('openDelete');"><%=Strings.getHtml("delete", sdata.getLocale())%>
    </a></li>
  </ul>
</form>
