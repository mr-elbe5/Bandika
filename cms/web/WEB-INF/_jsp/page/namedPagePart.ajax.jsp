<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.page.PagePartData" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.PagePartActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) SessionReader.getSessionObject(request, "pageData");
    assert(data!=null);
    PagePartData part = data.getEditPagePart();
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/pagepart.srv" method="post" id="shareform" name="shareform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
        <input type="hidden" name="partId" value="<%=part.getId()%>"/>
        <input type="hidden" name="act" value="<%=PagePartActions.sharePagePart%>"/>
        <table class="padded form">
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="name" name="name" value="" maxlength="60"/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button type="submit" class="primary"><%=StringUtil.getHtml("_share", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#shareform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/pagepart.ajx', params);
    });
</script>
