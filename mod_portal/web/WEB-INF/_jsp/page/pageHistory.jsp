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
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.application.AppConfiguration" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    PageData data = (PageData) rdata.get("pageData");
    List<PageData> pageVersions = PageBean.getInstance().getPageHistory(data.getId());
%>
<form class="form-horizontal" action="/page.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>
    <input type="hidden" name="pageId" value="<%=data.getId()%>"/>

    <div class="well">
        <legend><%=StringFormat.toHtml(data.getName())%>
        </legend>
        <bandika:table id="pageTable" checkId="version" formName="form" headerKeys="portal_version,portal_changeDate,portal_author">
            <% for (PageData versionData : pageVersions) {%>
            <tr>
                <td><input type="checkbox" name="version" value="<%=versionData.getVersion()%>"/></td>
                <td>
                    <a href="/page.srv?act=show&pageId=<%=versionData.getId()%>&version=<%=versionData.getVersion()%>"
                       target="_blank"><%=versionData.getVersion()%>
                    </a></td>
                <td><%=AppConfiguration.getInstance().getDateFormat(locale).format(versionData.getChangeDate())%>
                </td>
                <td><%=StringFormat.toHtml(versionData.getAuthorName())%>
                </td>
            </tr>
            <%}%>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return submitAction('restoreHistoryPage');"><%=StringCache.getHtml("portal_restore",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeleteHistoryPage');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=openPageSettings&pageId=<%=data.getId()%>');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>

