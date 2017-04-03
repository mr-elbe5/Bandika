<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.file.DocumentBean" %>
<%@ page import="de.bandika.file.DocumentData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    List<DocumentData> documents = DocumentBean.getInstance().getAllPublicDocuments(true);
%>
<form class="form-horizontal" action="/document.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="openEditDocument"/>

    <div class="well">
        <legend><%=rdata.getTitle()%>
        </legend>
        <bandika:table id="fileTable" checkId="fid" formName="form" headerKeys="portal_name,portal_document,portal_usages">
            <% for (DocumentData data : documents) {%>
            <tr>
                <td><input type="checkbox" name="fid" value="<%=data.getId()%>"/></td>
                <td>
                    <a href="/document.srv?act=openEditDocument&fid=<%=data.getId()%>"><%=StringFormat.toHtml(data.getFileName())%>
                    </a></td>
                <td>
                    <a href="/document.srv?act=download&fid=<%=data.getId()%>"><%=StringCache.getHtml("portal_download",locale)%></a>
                </td>
                <td><%=data.getPageIds() == null ? "" : data.getPageIds().size()%>
                </td>
            </tr>
            <%}%>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return linkTo('/document.srv?act=openCreateDocument');"><%=StringCache.getHtml("webapp_new",locale)%>
        </button>
        <button class="btn btn-primary" onclick="return submitAction('openEditDocument');"><%=StringCache.getHtml("portal_edit",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeleteDocument');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
    </div>
</form>

