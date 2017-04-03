<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getCurrentPageId();
  boolean admin = rdata.getParamBoolean("adminLayer");
%>

<div id="selectLayout" class="modal hide iframeLayer" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("selectTemplate")%>" aria-hidden="true">
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
    $("#selectLayout").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#selectLayout').on('show', function () {
    $("#selectLayout").find('iframe').attr('src', '/_page?method=openCreatePage&id=<%=id%>&adminLayer=<%=admin ? "1" : "0"%>');
  });
</script>


