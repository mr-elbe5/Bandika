<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    SiteData data = (SiteData) SessionHelper.getSessionObject(request, "siteData");
    request.setAttribute("treeNode", data);
%>
<jsp:include page = "/WEB-INF/_jsp/_masterinclude/error.inc.jsp"/>
<form action = "/site.srv" method = "post" id = "siterightsform" name = "siterightsform" accept-charset = "UTF-8">
    <fieldset>
        <input type = "hidden" name = "siteId" value = "<%=data.getId()%>"/> <input type = "hidden" name = "act" value = "saveSiteRights"/>
        <table class = "form">
            <jsp:include page = "../tree/editRights.inc.jsp" flush = "true"/>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#siterightsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/site.ajx', params);
    });
</script>

