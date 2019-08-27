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
<%@ page import="de.elbe5.page.PageFlexClass" %>
<%@ page import="de.elbe5.templatepage.TemplatePageData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.templatepage.templatepagepart.TemplatePagePartData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    TemplatePageData data = (TemplatePageData) rdata.getCurrentPage();
    assert (data != null);
    TemplatePagePartData part = (TemplatePagePartData) data.getEditPagePart();
    String url = "/ctrl/templatepage/savePagePartSettings/" + data.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_pagePartSettings",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="settingsform">
            <input type="hidden" name="partId" value="<%=part.getId()%>"/>
            <cms:formerror/>
            <cms:select name="flexClass" label="_flexClass">
                <% String selection = part.getFlexClass();
                    if (selection.isEmpty())
                        selection = Statics.DEFAULT_CLASS.name();
                    for (PageFlexClass css : PageFlexClass.values()) {%>
                <option value="<%=css.getCssClass()%>" <%=css.name().equals(selection) ? "selected" : ""%>><%=css.name()%>
                </option>
                <%}%>
            </cms:select>
            <cms:text name="cssClasses" label="_cssClass" value="<%=StringUtil.toHtml(part.getCssClasses())%>"/>
            <cms:editor name="script" label="_script" type="javascript" hint="_javascriptHint" height="10rem">
                <%=StringUtil.toHtml(part.getScript())%>
            </cms:editor>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings.html("_save",locale)%>
                </button>
            </div>
        </cms:form>
        <script type="text/javascript">
            let editor = initAce($('#script'));
            $('#settingsform').submit(function (event) {
                let $this = $(this);
                event.preventDefault();
                $('#script').val(editor.getSession().getValue());
                let params = $this.serialize();
                postByAjax('/ctrl/templatepage/savePagePartSettings/<%=data.getId()%>', params, '<%=Statics.MODAL_DIALOG_JQID%>');
            });
        </script>
    </div>
</div>


