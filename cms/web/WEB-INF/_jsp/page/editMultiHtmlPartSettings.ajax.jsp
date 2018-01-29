<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.page.PageData" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.page.MultiHtmlPartData" %>
<%@ page import="de.bandika.cms.page.PagePartActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) SessionReader.getSessionObject(request, "pageData");
    assert(data!=null);
    MultiHtmlPartData part = (MultiHtmlPartData) data.getEditPagePart();
    int contentCount=part.getContentCount();
    request.setAttribute("treeNode", data);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/pageedit.ajx" method="post" id="pagepartsettingsform" name="pagepartsettingsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
        <input type="hidden" name="partId" value="<%=part.getId()%>"/>
        <input type="hidden" name="act" value="<%=PagePartActions.saveMultiHtmlPartSettings%>"/>
        <table class="padded form">
            <tr>
                <td>
                    <label for="cssClass"><%=StringUtil.getHtml("_cssClass", locale)%>
                    </label></td>
                <td>
                    <div>
                        <input type="text" id="cssClass" name="cssClass" value="<%=StringUtil.toHtml(part.getCssClass())%>" maxlength="200"/>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="contentCount"><%=StringUtil.getHtml("_contentCount", locale)%>
                    </label></td>
                <td>
                    <div>
                        <select id="contentCount" name="contentCount">
                            <% for (int i = 1; i < 9; i++) {%>
                            <option value="<%=i%>" <%=contentCount == i ? "selected" : ""%>><%=i%>
                            </option>
                            <%}%>
                        </select>
                    </div>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="script"><%=StringUtil.getHtml("_script", locale)%>
                    </label></td>
                <td>
                    <textarea id="script" name="script" rows="20" cols=""><%=StringUtil.toHtmlInput(part.getScript())%></textarea>
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
    $('#pagepartsettingsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/pageedit.ajx', params);
    });
</script>

