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
<%@ page import="de.bandika.cms.template.TemplateCache" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="de.bandika.cms.template.TemplateType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.page.PageAdminActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    PageData data = (PageData) SessionReader.getSessionObject(request, "pageData");
    assert(data!=null);
    request.setAttribute("treeNode", data);
    List<TemplateData> pageTemplates = TemplateCache.getInstance().getTemplates(TemplateType.PAGE);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/pageadmin.srv" method="post" id="pagesettingsform" name="pagesettingsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
        <input type="hidden" name="act" value="<%=PageAdminActions.savePageSettings%>"/>
        <table class="padded form">
            <jsp:include page="../tree/editNode.inc.jsp" flush="true"/>
            <jsp:include page="../tree/editResource.inc.jsp" flush="true"/>
            <tr>
                <td>
                    <label for="templateName"><%=StringUtil.getHtml("_pageTemplate", locale)%>
                    </label></td>
                <td>
                    <select id="templateName" name="templateName">
                        <option value="" <%=data.getTemplateName().isEmpty() ? "selected" : ""%>><%=StringUtil.getHtml("_pleaseSelect", locale)%>
                        </option>
                        <% for (TemplateData tdata : pageTemplates) {%>
                        <option value="<%=StringUtil.toHtml(tdata.getName())%>" <%=tdata.getName().equals(data.getTemplateName()) ? "selected" : ""%>><%=StringUtil.toHtml(tdata.getName())%>
                        </option>
                        <%}%>
                    </select>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_isDefaultPage", locale)%>
                </label></td>
                <td>
          <span><%=Boolean.toString(data.isDefaultPage())%>
          </span>
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
    $('#pagesettingsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/pageadmin.ajx', params);
    });
</script>

