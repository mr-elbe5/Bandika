<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.file.FileFilterData" %>
<%@ page import="de.bandika.file.LinkedFileData" %>
<%@ page import="de.bandika.file.FileBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageAssetSelectData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  FileFilterData filterData = (FileFilterData) sdata.getParam("fileFilterData");
  PageAssetSelectData selectData = (PageAssetSelectData) sdata.getParam("selectData");
  ArrayList<LinkedFileData> files = FileBean.getInstance().getAllAvailableFilesForPage(filterData);
  if (selectData.isForHtmlEditor()) {
    int callbackFuncNum = rdata.getParamInt("CKEditorFuncNum", -1);
    if (callbackFuncNum != -1)
      selectData.setCallbackFuncNum(callbackFuncNum);
  }
%>
<script type="text/javascript">
  var ImageUploadWindow;
  var callIndex = null;
  function callEditorCallback(imgId) {
    window.opener.CKEDITOR.tools.callFunction(<%=selectData.getCallbackFuncNum()%>, '/_file?method=show&fid=' + imgId);
    window.close();
  }
  function callFieldCallback(fileId) {
    opener.setLink('/_file?method=show&fid=' + fileId);
    window.close();
  }
</script>
<form class="form-horizontal" action="/_file" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>
  <input type="hidden" name="type" value="<%=filterData.getType()%>"/>

  <div class="well">
    <bandika:dataTable id="fileTable" formName="form" headerKeys="name,select,preview,exclusive" sort="true" paging="true" displayLength="5">
      <% for (LinkedFileData data : files) {%>
      <tr>
        <td><%=FormatHelper.toHtml(data.getShortName())%>
        </td>
        <td>
          <a href="#" onClick="window.open('/_file?method=show&fid=<%=data.getId()%>','FileViewer','width=<%=data.getWidth()==0 ? 600 : data.getWidth() + 20%>,height=<%=data.getHeight()==0 ? 800 : data.getHeight() + 50%>');return false;">
            <div>
              <% if (selectData.isForHtmlEditor()) {%>
              <a href="#" onClick="callEditorCallback(<%=data.getId()%>);return false;">
                    <%} else {%>
                <a href="#" onClick="callFieldCallback(<%=data.getId()%>);return false;">
                  <%
                    }
                    if (data.hasThumbnail()) {
                  %>
                  <img src="/_file?method=showThumbnail&fid=<%=data.getId()%>" width="<%=data.getThumbnail().getWidth()%>"
                       height="<%=data.getThumbnail().getHeight()%>" border='0' alt="" id="img<%=data.getId()%>"/>
                  <%} else {%>
                  <%=FormatHelper.toHtml(data.getName())%>
                  <%}%>
                </a>
            </div>
        </td>
        <td><a href="#"
               onClick="window.open('/_file?method=show&fid=<%=data.getId()%>','FileViewer','width=<%=data.getWidth() + 20%>,height=<%=data.getHeight() + 50%>');return false;"><%=StringCache.getHtml("preview")%>
        </a></td>
        <td><%=data.isExclusive() && data.getPageId() == filterData.getPageId() ? "X" : "" %>
        </td>
      </tr>
      <%}%>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return linkTo('/_file?method=openCreateFile&id=<%=selectData.getPageId()%>');"><%=StringCache.getHtml("new")%>
    </button>
    <button class="btn" onclick="window.close();"><%=StringCache.getHtml("close")%>
    </button>
  </div>
</form>

