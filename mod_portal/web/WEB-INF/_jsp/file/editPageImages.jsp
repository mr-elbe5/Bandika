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
<%@ page import="de.bandika.file.ImageBean" %>
<%@ page import="de.bandika.file.ImageData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    int pageId=rdata.getInt("pageId");
    List<ImageData> files = ImageBean.getInstance().getCurrentImagesOfPage(pageId, false);
%>
<form class="form-horizontal" action="/image.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>
    <input type="hidden" name="pageId" value="<%=pageId%>"/>

    <div class="well">
        <legend><%=rdata.getTitle()%>
        </legend>
        <bandika:table id="fileTable" checkId="fid" formName="form" headerKeys="portal_name,portal_thumbnail,portal_dimension">
            <% for (ImageData data : files) { %>
            <tr>
                <td><input type="checkbox" name="fid" value="<%=data.getId()%>"/></td>
                <td>
                    <a href="/image.srv?act=openEditImage&fid=<%=data.getId()%>&pageId=<%=pageId%>"><%=StringFormat.toHtml(data.getFileName())%>
                    </a></td>
                <td>
                    <a href="#"
                       onClick="window.open('/image.srv?act=show&fid=<%=data.getId()%>','FileViewer','width=<%=data.getWidth() + 20%>,height=<%=data.getHeight() + 50%>');return false;">
                        <% if (data.hasThumbnail()) {%>
                        <img src="/image.srv?act=showThumbnail&fid=<%=data.getId()%>"
                             border='0' alt="" id="img<%=data.getId()%>"/><%} else {%><%=data.getFileName()%><%}%></a>
                </td>
                <td><%=data.getWidth()%>&nbsp;x&nbsp;<%=data.getHeight()%>
                </td>
            </tr>
            <%}%>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary" onclick="return submitAction('openEditImage');"><%=StringCache.getHtml("portal_edit",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeleteImage');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
    </div>
</form>
