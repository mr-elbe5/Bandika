<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.site.SiteData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.site.SiteActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    SiteData data = (SiteData) SessionReader.getSessionObject(request, "siteData");
    assert data!=null;
    boolean inherited = data.inheritsRights();
    request.setAttribute("treeNode", data);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/site.srv" method="post" id="siterightsform" name="siterightsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="siteId" value="<%=data.getId()%>"/>
        <input type="hidden" name="act" value="<%=SiteActions.saveSiteRights%>"/>
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
        <button type="submit" class="primary" disabled="disabled" ><%=StringUtil.getHtml("_save", locale)%>
        </button>
        <%}%>
    </div>
</form>
<script type="text/javascript">
    $('#siterightsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/site.ajx', params);
    });
</script>

