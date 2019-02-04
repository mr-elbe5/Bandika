<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.template.TemplateActions" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    TemplateData data = (TemplateData) SessionReader.getSessionObject(request, "templateData");
    assert (data != null);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._template.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/template.ajx" name="templateform" act="<%=TemplateActions.saveTemplate%>">
            <input type="hidden" name="templateType" value="<%=data.getType()%>"/>
            <input type="hidden" name="templateName" value="<%=data.getName()%>"/>
            <div class="modal-body">
                <cms:requesterror/>
                <%if (!data.isNew()) {%>
                <cms:line label="<%=Strings._name.toString()%>"><%=StringUtil.toHtml(data.getName())%></cms:line>
                <% } else {%>
                <cms:text name="name" label="<%=Strings._name.toString()%>" required="true"><%=StringUtil.toHtml(data.getName())%></cms:text>
                <%}%>
                <cms:text name="displayName" label="<%=Strings._displayName.toString()%>"><%=StringUtil.toHtml(data.getDisplayName())%></cms:text>
                <cms:textarea name="description" label="<%=Strings._description.toString()%>" height="5rem"><%=StringUtil.toHtml(data.getDescription())%></cms:textarea>
                <cms:editor name="code" label="<%=Strings._code.toString()%>" type="html" hint="<%=Strings._htmlHint.toString()%>" height="20rem" required="true">
                    <%=StringUtil.toHtml(data.getCode())%>
                </cms:editor>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
        <script type="text/javascript">
            var editor = initAce($('#code'));
            $('#templateform').submit(function (event) {
                var $this = $(this);
                event.preventDefault();
                $('#code').val(editor.getSession().getValue());
                var params = $this.serialize();
                postByAjax('/template.ajx', params,'<%=Statics.MODAL_DIALOG_JQID%>');
            });
        </script>
    </div>
</div>

