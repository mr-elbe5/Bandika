<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.application.Statics" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata = RequestData.getRequestData(request);
    String url = rdata.getString(Statics.KEY_URL);
    String targetId = rdata.getString(Statics.KEY_TARGETID);
    String msg = rdata.getString(Statics.KEY_MESSAGE);
    String msgType = rdata.getString(Statics.KEY_MESSAGETYPE);%><% if (targetId.isEmpty()) {%>
<div id="pageContent">

    <form action="<%=url%>" method="POST" id="forwardform" accept-charset="UTF-8">
        <%if (!msg.isEmpty()) {%>
        <input type="hidden" name="<%=Statics.KEY_MESSAGE%>" value="<%=StringUtil.toHtml(msg)%>"/>
        <input type="hidden" name="<%=Statics.KEY_MESSAGETYPE%>" value="<%=StringUtil.toHtml(msgType)%>"/>
        <%}%>
    </form>

</div>
<script type="text/javascript">
    $('#forwardform').submit();
</script>
<%} else {
    StringBuilder sb = new StringBuilder("{");
    if (!msg.isEmpty()) {
        sb.append(Statics.KEY_MESSAGE).append(" : '").append(StringUtil.toJs(msg)).append("',");
        sb.append(Statics.KEY_MESSAGETYPE).append(" : '").append(StringUtil.toJs(msgType)).append("'");
    }
    sb.append("}");%>
<div id="pageContent"></div>
<script type="text/javascript">
    let $dlg = $(MODAL_DLG_JQID);
    $dlg.html('');
    $dlg.modal('hide');
    $('.modal-backdrop').remove();
    postByAjax('<%=url%>', <%=sb.toString()%>, '<%=StringUtil.toJs(targetId)%>');
</script>
<%}%>
