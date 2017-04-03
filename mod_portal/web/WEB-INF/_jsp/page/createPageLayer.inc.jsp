<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    int pageId = rdata.getInt("pageId");
    boolean admin = rdata.getBoolean("adminLayer");
%>

<div id="selectLayout" class="modal hide iframeLayer" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("portal_selectTemplate",locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <legend><%=StringCache.getHtml("portal_selectTemplate",locale)%>
        </legend>
    </div>
    <div class="modal-body">
        <iframe src="/blank.html" class="layerIframe" scrolling="no"></iframe>
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
        $("#selectLayout").find('iframe').attr('src', '/page.srv?act=openCreatePage&pageId=<%=pageId%>&adminLayer=<%=admin ? "1" : "0"%>');
    });
</script>


