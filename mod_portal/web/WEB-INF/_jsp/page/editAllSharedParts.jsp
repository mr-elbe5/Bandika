<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale=sdata.getLocale();
  List<PagePartData> parts = PageBean.getInstance().getAllSharedPagePartsWithUsages();
%>
<form class="form-horizontal" action="/page.srv" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="act" value="reopenEditSharedParts"/>

  <div class="well">
    <legend><%=StringCache.getHtml("portal_sharedParts", locale)%>
    </legend>
    <bandika:table id="partTable" checkId="partId" formName="form" headerKeys="portal_name,portal_template,portal_pages">
      <% for (PagePartData data : parts) {%>
      <tr>
        <td><input type="checkbox" name="partId" value="<%=data.getId()%>"/></td>
        <td><%=StringFormat.toHtml(data.getName())%></td>
        <td><%=StringFormat.toHtml(data.getPartTemplate())%>
        </td>
        <td>
          <% for (int id : data.getPageIds()){
            MenuData mdata= MenuCache.getInstance().getNode(id);
            if (mdata==null)
              continue;
          %>
          <div><a href="/page.srv?act=show&pageId=<%=id%>" target="_blank"><%=StringFormat.toHtml(mdata.getName())%></a></div>
          <%}%>
        </td>
      </tr>
      <%}%>
    </bandika:table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitAction('openDeleteSharedPart');"><%=StringCache.getHtml("webapp_delete", locale)%>
    </button>
  </div>
</form>


