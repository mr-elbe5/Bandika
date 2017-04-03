<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) SessionReader.getSessionObject(request, "pageData");
    boolean inherited = data.inheritsRights();
    request.setAttribute("treeNode", data);%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/page.srv" method="post" id="pagerightsform" name="pagerightsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/> <input type="hidden" name="act" value="savePageRights"/>
        <table class="padded form">
            <jsp:include page="../tree/editRights.inc.jsp" flush="true"/>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <%if (inherited) {%>
        <span> <%=StringUtil.getHtml("_inheritedHint", locale)%></span>
        <%} else {%>
        <button type="submit" class="primary" <%=inherited ? "disabled=\"disabled\"" : ""%>><%=StringUtil.getHtml("_save", locale)%>
        </button>
        <%}%>
    </div>
</form>
<script type="text/javascript">
    $('#pagerightsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/page.ajx', params);
    });
</script>

