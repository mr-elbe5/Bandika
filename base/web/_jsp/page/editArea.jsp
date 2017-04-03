<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.AreaData" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  PageData data = (PageData) sdata.getParam("pageData");
  String areaName = rdata.getParamString("areaName");
  String matchTypes = rdata.getParamString("areaMatchTypes");
  PagePartData editPagePart = data.getEditPagePart();
  AreaData area = data.getArea(areaName);%>
<div class="area">
  <div class="areaHeader"><%=FormatHelper.toHtml(areaName)%>
  </div>
  <% if (area != null) {
    for (PagePartData pdata : area.getParts()) {
      if (pdata == null)
        continue;
      rdata.setParam("pagePartData", pdata);
      if (pdata == editPagePart)
        rdata.setParam("partEditMode", "1");
  %>
  <div class="<%=pdata == editPagePart ? "editPagePart" : "viewPagePart"%>">
    <div>
      <bandika:areaTools partId="<%=pdata.getId()%>" id="<%=data.getId()%>" areaName="<%=areaName%>" anyPartEditMode="<%=editPagePart!=null%>" partEditMode="<%=pdata == editPagePart%>"/>
    </div>
    <jsp:include page="<%=pdata.getPartTemplateUrl()%>" flush="true"/>
  </div>
  <%
        rdata.removeParam("pagePartData");
        rdata.removeParam("partEditMode");
      }
    }
    if (editPagePart == null) {%>
  <div>
    <bandika:areaTools partId="-1" id="<%=data.getId()%>" areaName="<%=areaName%>" endTag="true" anyPartEditMode="false" partEditMode="false"/>
  </div>
  <%}%>
</div>

<div id="selectPart<%=areaName%>" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("selectTemplate")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("selectTemplate")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $("#selectPart<%=areaName%>").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#selectPart<%=areaName%>').on('show', function () {
    var partId = document.form.partId.value;
    $("#selectPart<%=areaName%>").find($(".modal-body")).load('/_page?method=openAddPagePart&id=<%=data.getId()%>&areaName=<%=areaName%>&areaMatchTypes=<%=matchTypes%>&partId=' + partId);
  });
</script>



