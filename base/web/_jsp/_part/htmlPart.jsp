<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika.page.HtmlPartData" %>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.page.PageData" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  boolean editMode = rdata.getParamInt("partEditMode", 0) == 1;
  HtmlPartData pdata = (HtmlPartData) rdata.getParam("pagePartData");
%>
<div>
  <%
    if (editMode) {
      SessionData sdata = RequestHelper.getSessionData(request);
      PageData data = (PageData) sdata.getParam("pageData");
      int pageId = data == null ? 0 : data.getId();
  %>
  <textarea name="htmlArea" cols="80" rows="10"><%=FormatHelper.toHtmlInput(pdata.getHtml())%>
  </textarea>
  <script type="text/javascript">CKEDITOR.replace('htmlArea', {
    customConfig: '/_statics/script/editorConfig.js',
    toolbar: 'Full',
    filebrowserBrowseUrl: '/_page?method=openSelectAsset&assetType=LINK&forHTML=1&type=page&availableTypes=page,document,image&id=<%=pageId%>',
    filebrowserImageBrowseUrl: '/_page?method=openSelectAsset&assetType=FILE&forHTML=1&type=image&availableTypes=image&id=<%=pageId%>'
  });</script>
  <%} else {%>
  <%=pdata.getHtml()%>
  <%}%>
</div>
