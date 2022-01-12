<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.request.RequestKeys" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    String msg = rdata.getString(RequestKeys.KEY_MESSAGE);
    String msgType = rdata.getString(RequestKeys.KEY_MESSAGETYPE);
    String msgKey="";
    switch (msgType) {
        case RequestKeys.MESSAGE_TYPE_INFO:
            msgKey="_info";
            break;
        case RequestKeys.MESSAGE_TYPE_SUCCESS:
            msgKey="_success";
            break;
        case RequestKeys.MESSAGE_TYPE_ERROR:
            msgKey="_error";
            break;
    }
%>
<div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH(msgKey, locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <%=$H(msg)%>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close",locale)%>
            </button>
        </div>
    </div>
</div>
