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
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.file.ImageBean" %>
<%@ page import="de.bandika.file.ImageData" %>
<%@ page import="de.bandika.page.PageAssetSelectData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    int pageId=rdata.getInt("pageId");
    PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
    List<ImageData> files = ImageBean.getInstance().getAllAvailableImagesForPage(pageId);
    if (selectData.isForHtmlEditor()) {
        int callbackFuncNum = rdata.getInt("CKEditorFuncNum", -1);
        if (callbackFuncNum != -1)
            selectData.setCallbackFuncNum(callbackFuncNum);
    }
%>
<script type="text/javascript">
    function callEditorCallback(imgId) {
        window.opener.CKEDITOR.tools.callFunction(<%=selectData.getCallbackFuncNum()%>, '/image.srv?act=show&fid=' + imgId);
        window.close();
    }
    function callFieldCallback(fileId) {
        <% if (selectData.getAssetUsage().equals(PageAssetSelectData.ASSET_USAGE_LINK)){%>
        opener.setSelLink('/image.srv?act=show&fid=' + fileId);
        <%} else{%>
        opener.setSelImage(fileId);
        <%}%>
        window.close();
    }
</script>
<form class="form-horizontal" action="/image.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>

    <div class="well">
        <bandika:table id="fileTable" formName="form" headerKeys="portal_name,portal_select,portal_preview,portal_dimension,portal_exclusive">
            <%for (ImageData data : files) {%>
            <tr>
                <td><%=StringFormat.toHtml(data.getFileName())%>
                </td>
                <td>
                    <div>
                        <% if (selectData.isForHtmlEditor()) {%>
                        <a href="#" onClick="callEditorCallback(<%=data.getId()%>);return false;">
                                    <%} else {%>
                            <a href="#" onClick="callFieldCallback(<%=data.getId()%>);return false;">
                                <%
                                    }
                                    if (data.hasThumbnail()) {
                                %>
                                <img src="/image.srv?act=showThumbnail&fid=<%=data.getId()%>"
                                     border='0' alt=""
                                     id="img<%=data.getId()%>"/>
                                <%} else {%>
                                <%=StringCache.getHtml("portal_select",locale)%>
                                <%}%>
                            </a>
                    </div>
                </td>
                <td><a href="#"
                       onClick="window.open('/image.srv?act=show&fid=<%=data.getId()%>','FileViewer','width=<%=data.getWidth() + 20%>,height=<%=data.getHeight() + 50%>');return false;"><%=StringCache.getHtml("portal_preview",locale)%>
                </a></td>
                <td><%=data.getWidth()%>&nbsp;x&nbsp;<%=data.getHeight()%>
                </td>
                <td><%=data.isExclusive() && data.getPageId() == pageId ? "X" : "" %>
                </td>
            </tr>
            <%}%>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return linkTo('/image.srv?act=openCreateImage&pageId=<%=selectData.getPageId()%>');"><%=StringCache.getHtml("webapp_new",locale)%>
        </button>
        <button class="btn" onclick="window.close();"><%=StringCache.getHtml("portal_close",locale)%>
        </button>
    </div>
</form>

