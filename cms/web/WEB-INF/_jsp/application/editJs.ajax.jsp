<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.application.DynamicsActions" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    String js = RequestReader.getString(request, "js");
    assert (js != null);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/dynamics.ajx" method="post" id="scriptform" name="scriptform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=DynamicsActions.saveJs%>"/>
    <fieldset>
        <table class="padded form">
            <tr>
                <td>
                    <label for="js"><%=StringUtil.getHtml("_code", locale)%>&nbsp;*</label></td>
                <td>
                    <textarea id="js" name="js" rows="20" cols=""><%=StringUtil.toHtmlInput(js)%></textarea>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#scriptform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/dynamics.ajx', params);
    });
</script>
