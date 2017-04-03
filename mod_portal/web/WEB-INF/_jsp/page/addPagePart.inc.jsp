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
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.template.PartTemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    int pageId = rdata.getInt("pageId");
    String areaName = rdata.getString("areaName");
    String areaType = rdata.getString("areaType");
    int partId = rdata.getInt("partId");
    List<PartTemplateData> templates = TemplateCache.getInstance().getAreaPartTemplates(areaType);
    List<PagePartData> parts = PageBean.getInstance().getAllSharedPageParts();
%>
<div class="layerContent">
    <ul class="nav nav-tabs" id="tabSelect">
        <li class="active" ><a href="#templateTab" data-toggle="tab" ><%=StringCache.getHtml("portal_fromTemplate", locale)%></a></li>
        <li><a href="#shareTab" data-toggle="tab"><%=StringCache.getHtml("portal_sharedPart", locale)%></a></li>
    </ul>

    <div class="tab-content">
        <div class="tab-pane active" id="templateTab">
            <table class="table">
                <colgroup>
                    <col width="30%">
                    <col width="70%">
                </colgroup>
                <thead>
                <tr>
                    <th><%=StringCache.getHtml("portal_name", locale)%>
                    </th>
                    <th><%=StringCache.getHtml("portal_description", locale)%>
                    </th>
                </tr>
                </thead>
                <tbody>
                <% for (PartTemplateData tdata : templates) {%>
                <tr>
                    <td>
                        <a href="/page.srv?act=addPagePart&pageId=<%=pageId%>&partId=<%=partId%>&areaName=<%=areaName%>&template=<%=tdata.getName()%>"><%=StringFormat.toHtml(tdata.getName())%>
                        </a></td>
                    <td><%=StringFormat.toHtml(tdata.getDescription())%>
                    </td>
                </tr>
                <%}%>
                </tbody>
            </table>
        </div>
        <div class="tab-pane" id="shareTab">
            <table class="table">
                <thead>
                <tr>
                    <th><%=StringCache.getHtml("portal_name", locale)%>
                    </th>
                </tr>
                </thead>
                <tbody>
                <% for (PagePartData data : parts) {%>
                <tr>
                    <td>
                        <a href="/page.srv?act=addSharedPart&pageId=<%=pageId%>&partId=<%=partId%>&areaName=<%=areaName%>&sharedPartId=<%=data.getId()%>"><%=StringFormat.toHtml(data.getName())%>
                        </a>
                    </td>
                </tr>
                <%}%>
                </tbody>
            </table>
        </div>
    </div>

    <script>
        $(function () {
            $('#tabSelect').find('a:first').tab('show');
        })
    </script>
</div>
