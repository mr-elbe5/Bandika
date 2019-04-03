<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.base.cache.StringCache" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata=RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    String msg = rdata.getString(Statics.KEY_MESSAGE);
    String msgType= rdata.getString(Statics.KEY_MESSAGETYPE);
%>
<div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=StringCache.getHtml(Statics.getTypeKey(msgType), locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
            <div class="modal-body">
                <%=StringUtil.toHtml(msg)%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
            </div>
    </div>
</div>
