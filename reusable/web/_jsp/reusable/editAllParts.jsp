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
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.reusable.ReusablePartBean" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>

<%
  SessionData sdata = RequestHelper.getSessionData(request);
  ArrayList<PagePartData> parts = ReusablePartBean.getInstance().getAllReusablePageParts();
%>
<form class="form-horizontal" action="/_reusable" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="reopenEditReusableParts"/>

  <div class="well">
    <legend><%=StringCache.getHtml("reusableParts")%>
    </legend>
    <bandika:dataTable id="partTable" checkId="partId" formName="form" sort="true" paging="true" headerKeys="name,template">
      <% for (PagePartData data : parts) {%>
      <tr>
        <td><input type="checkbox" name="partId" value="<%=data.getId()%>"/></td>
        <td>
          <a href="/_reusable?method=openEditReusablePart&partId=<%=data.getId()%>"><%=FormatHelper.toHtml(data.getName())%>
          </a></td>
        <td><%=FormatHelper.toHtml(data.getPartTemplate())%>
        </td>
      </tr>
      <%}%>
    </bandika:dataTable>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="$('#selectPartLayout').modal();return false;"><%=StringCache.getHtml("new")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openEditReusablePart');"><%=StringCache.getHtml("edit")%>
    </button>
    <button class="btn btn-primary" onclick="return submitMethod('openDeleteReusablePart');"><%=StringCache.getHtml("delete")%>
    </button>
  </div>
</form>

<div id="selectPartLayout" class="modal hide iframeLayer" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("selectTemplate")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("selectTemplate")%>
    </legend>
  </div>
  <div class="modal-body">
    <iframe src="/_jsp/blank.jsp" class="layerIframe" scrolling="no"></iframe>
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $("#selectPartLayout").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#selectPartLayout').on('show', function () {
    $("#selectPartLayout").find('iframe').attr('src', '/_reusable?method=openCreateReusablePart');
  });
</script>

