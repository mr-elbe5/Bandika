<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.template.TemplateActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TemplateData data = (TemplateData) SessionReader.getSessionObject(request, "templateData");
    assert (data != null);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/template.srv" method="post" id="templateform" name="templateform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=TemplateActions.saveTemplate%>"/>
    <input type="hidden" name="templateType" value="<%=data.getType()%>"/>
    <input type="hidden" name="templateName" value="<%=data.getName()%>"/>
    <fieldset>
        <table class="padded form">
            <%if (!data.isNew()) {%>
            <tr>
                <td><label><%=StringUtil.getHtml("_name", locale)%>
                </label></td>
                <td>
          <span><%=StringUtil.toHtml(data.getName())%>
          </span>
                </td>
            </tr>
            <% } else {%>
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="name" name="name" value="<%=StringUtil.toHtml(data.getName())%>" maxlength="60"/>
                </td>
            </tr>
            <%}%>
            <tr>
                <td>
                    <label for="displayName"><%=StringUtil.getHtml("_displayName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="displayName" name="displayName" value="<%=StringUtil.toHtml(data.getDisplayName())%>" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="description"><%=StringUtil.getHtml("_description", locale)%>&nbsp;*</label></td>
                <td>
          <textarea id="description" name="description" rows="3" cols=""><%=StringUtil.toHtmlInput(data.getDescription())%>
          </textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="code"><%=StringUtil.getHtml("_code", locale)%>&nbsp;*</label></td>
                <td>
          <textarea id="code" name="code" rows="20" cols=""><%=StringUtil.toHtmlInput(data.getCode())%>
          </textarea>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="sectionTypes"><%=StringUtil.getHtml("_sectionTypes", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="sectionTypes" name="sectionTypes" value="<%=StringUtil.toHtml(data.getSectionTypes())%>" maxlength="120"/>
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
    $('#templateform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/template.srv', params);
    });
</script>
