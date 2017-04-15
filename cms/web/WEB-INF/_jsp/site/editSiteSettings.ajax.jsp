<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.site.SiteData" %>
<%@ page import="de.bandika.cms.template.TemplateCache" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="de.bandika.cms.template.TemplateType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    SiteData data = (SiteData) SessionReader.getSessionObject(request, "siteData");
    List<TemplateData> masterTemplates = TemplateCache.getInstance().getTemplates(TemplateType.MASTER);
    request.setAttribute("treeNode", data);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/site.srv" method="post" id="sitesettingsform" name="sitesettingsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="siteId" value="<%=data.getId()%>"/>
        <input type="hidden" name="act" value="saveSiteSettings"/>
        <table class="padded form">
            <jsp:include page="../tree/editNode.inc.jsp" flush="true"/>
            <tr>
                <td>
                    <label for="inheritsMaster"><%=StringUtil.getHtml("_inheritsMaster", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="inheritsMaster" name="inheritsMaster" value="true" <%=data.inheritsMaster() ? "checked" : ""%>/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="templateName"><%=StringUtil.getHtml("_masterTemplate", locale)%>
                    </label></td>
                <td>
                    <select id="templateName" name="templateName">
                        <option value="" <%=data.getTemplateName().isEmpty() ? "selected" : ""%>><%=StringUtil.getHtml("_pleaseSelect", locale)%>
                        </option>
                        <% for (TemplateData tdata : masterTemplates) {%>
                        <option value="<%=StringUtil.toHtml(tdata.getName())%>" <%=tdata.getName().equals(data.getTemplateName()) ? "selected" : ""%>><%=StringUtil.toHtml(tdata.getName())%>
                        </option>
                        <%}%>
                    </select>
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
    $('#sitesettingsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/site.ajx', params);
    });
</script>

