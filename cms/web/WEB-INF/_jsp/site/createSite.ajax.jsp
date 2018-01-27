<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.template.TemplateCache" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="de.bandika.cms.template.TemplateType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.site.SiteAction" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<TemplateData> pageTemplates = TemplateCache.getInstance().getTemplates(TemplateType.PAGE);
    int siteId = RequestReader.getInt(request, "siteId");
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/site.srv" method="post" id="layoutform" name="layoutform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="siteId" value="<%=siteId%>"/>
        <input type="hidden" name="act" value="<%=SiteAction.createSite%>"/>
        <table class="padded form">
            <tr>
                <td>
                    <label for="displayName"><%=StringUtil.getHtml("_displayName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="displayName" name="displayName" value="" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("_name", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="name" name="name" value="" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_layoutForDefaultPage", locale)%>
                </label></td>
                <td>
                    <div>
                        <input type="radio" name="templateName" value="" checked/><%=StringUtil.getHtml("_noDefaultPage", locale)%>
                    </div>
                    <%for (TemplateData tdata : pageTemplates) {%>
                    <div>
                        <input type="radio" name="templateName" value="<%=tdata.getName()%>"/><%=StringUtil.toHtml(tdata.getName())%>
                    </div>
                    <%}%>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_create", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#layoutform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        post2ModalDialog('/site.ajx', $this.serialize());
    });
</script>
