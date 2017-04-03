<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    PageData data = (PageData) SessionHelper.getSessionObject(request, "pageData");
    request.setAttribute("treeNode", data);
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/page.srv" method = "post" id = "pagerightsform" name = "pagerightsform" accept-charset = "UTF-8">
    <fieldset>
        <input type = "hidden" name = "pageId" value = "<%=data.getId()%>"/> <input type = "hidden" name = "act" value = "savePageRights"/>
        <table class = "form">
            <jsp:include page = "../tree/editRights.inc.jsp" flush = "true"/>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "btn btn-primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#pagerightsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/page.ajx', params);
    });
</script>

