<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.application.Statics" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.template.TemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TemplateData data = (TemplateData) rdata.getSessionObject("templateData");
    assert (data != null);%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_template",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/ctrl/template/saveTemplate" name="templateform">
            <input type="hidden" name="templateType" value="<%=data.getType()%>"/>
            <input type="hidden" name="templateName" value="<%=data.getName()%>"/>
            <div class="modal-body">
                <cms:formerror/>
                <%if (!data.isNew()) {%>
                <cms:line label="_name"><%=StringUtil.toHtml(data.getName())%>
                </cms:line>
                <% } else {%>
                <cms:text name="name" label="_name" required="true" value="<%=StringUtil.toHtml(data.getName())%>"/>
                <%}%>
                <cms:text name="displayName" label="_displayName" value="<%=StringUtil.toHtml(data.getDisplayName())%>"/>
                <cms:textarea name="description" label="_description" height="5rem"><%=StringUtil.toHtml(data.getDescription())%>
                </cms:textarea>
                <cms:editor name="code" label="_code" type="html" hint="_htmlHint" height="20rem" required="true">
                    <%=StringUtil.toHtml(data.getCode())%>
                </cms:editor>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings.html("_save",locale)%>
                </button>
            </div>
        </cms:form>
        <script type="text/javascript">
            let editor = initAce($('#code'));
            $('#templateform').submit(function (event) {
                let $this = $(this);
                event.preventDefault();
                $('#code').val(editor.getSession().getValue());
                let params = $this.serialize();
                postByAjax('/ctrl/template/saveTemplate', params, '<%=Statics.MODAL_DIALOG_JQID%>');
            });
        </script>
    </div>
</div>
