<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.servlet.Message" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.base.cache.StringCache" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Message msg = Message.getMessage(request);
    String closeScript= RequestReader.getString(request, Statics.KEY_CLOSESCRIPT);
    assert (msg!=null);
%>
<div class="modal-dialog modal-dialog-centered" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=StringCache.getHtml(msg.getTypeKey(), locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
            <div class="modal-body">
                <%=StringUtil.toHtml(msg.getMessage())%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
            </div>
    </div>
    <% if (!closeScript.isEmpty()){%>
        <script type="application/javascript">
            $('#modalDialog').on('hidden.bs.modal', function (e) {
                <%=closeScript%>
            })
        </script>
    <%}%>
</div>
