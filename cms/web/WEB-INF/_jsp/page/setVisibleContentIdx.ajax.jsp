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
<%@ page import="de.elbe5.pagepart.PagePartData" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) SessionReader.getSessionObject(request, "pageData");
    PagePartData part = data.getEditPagePart();
    int contentCount=part.getContentCount();
    int contentIdx=part.getCurrentContentIdx();
    request.setAttribute("treeNode", data);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/pagepart.ajx" method="post" id="pagepartcontentindexform" name="pagepartcontentindexform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
        <input type="hidden" name="partId" value="<%=part.getId()%>"/>
        <input type="hidden" name="act" value="saveVisibleContentIdx"/>
        <table class="padded form">
            <tr>
                <td>
                    <label for="contentIdx"><%=StringUtil.getHtml("_contentIdx", locale)%>
                    </label></td>
                <td>
                    <div>
                        <select id="contentIdx" name="contentIdx">
                            <% for (int i=0;i<contentCount;i++){%>
                            <option value="<%=i%>" <%=contentIdx==i ? "selected" : ""%>><%=i+1%></option>
                            <%}%>
                        </select>
                    </div>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_cancel", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_ok", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#pagepartcontentindexform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/pagepart.ajx', params);
    });
</script>

