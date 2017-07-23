<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TemplateData data = (TemplateData) SessionReader.getSessionObject(request, "templateData");
    assert (data != null);
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/template.srv" method="post" id="templateform" name="templateform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveTemplate"/>
    <input type="hidden" name="templateType" value="<%=data.getType().name()%>"/>
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
                    <label for="dataType"><%=StringUtil.getHtml("_dataType", locale)%>&nbsp</label></td>
                <td>
                    <input type="text" id="dataType" name="dataType" value="<%=StringUtil.toHtml(data.getDataTypeName())%>" maxlength="20"/>
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
                    <label for="usage"><%=StringUtil.getHtml("_usage", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="usage" name="usage" value="<%=StringUtil.toHtml(data.getUsage())%>" maxlength="120"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="editable"><%=StringUtil.getHtml("_editable", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="editable" name="editable" value="true" <%=data.isEditable() ? "checked" : ""%>/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="dynamic"><%=StringUtil.getHtml("_dynamic", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="dynamic" name="dynamic" value="true" <%=data.isDynamic() ? "checked" : ""%>/>
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
