<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.page.PagePartActions" %>
<%@ page import="de.elbe5.cms.template.TemplateBean" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.PagePartFlexClass" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int pageId = RequestReader.getInt(request, "pageId");
    String sectionName = RequestReader.getString(request, "sectionName");
    int partId = RequestReader.getInt(request, "partId");
    boolean addBelow=RequestReader.getBoolean(request,"addBelow");
    List<TemplateData> templates = TemplateBean.getInstance().getAllTemplates(TemplateData.TYPE_PART);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._newPagePart.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/pagepart.ajx" name="partform" act="<%=PagePartActions.addPagePart%>" ajax="true">
        <div class="modal-body">
            <cms:message/>
            <input type="hidden" name="pageId" value="<%=pageId%>" />
            <input type="hidden" name="partId" value="<%=partId%>" />
            <input type="hidden" name="sectionName" value="<%=sectionName%>" />
            <cms:line label="<%=Strings._position.toString()%>" padded="true">
                <cms:radio name="below" value="false" ><%=Strings._above.html(locale)%></cms:radio>&nbsp;<cms:radio name="below" value="true" checked="true"><%=Strings._below.html(locale)%></cms:radio>
            </cms:line>
            <cms:select name="flexClass" label="<%=Strings._flexClass.toString()%>" required="true">
                <% String selection= Statics.DEFAULT_CLASS.name();
                    for (PagePartFlexClass css : PagePartFlexClass.values()){%>
                <option value="<%=css.getCssClass()%>" <%=css.name().equals(selection) ? "selected" : ""%>><%=css.name()%></option>
                <%}%>
            </cms:select>
            <cms:line label="<%=Strings._template.toString()%>" padded="true" required="true">
                <% for (TemplateData tdata : templates) {%>
                <cms:radio name="template" value="<%=String.valueOf(tdata.getName())%>"><%=StringUtil.toHtml(tdata.getDisplayName())%></cms:radio><br/>
                <%}%>
            </cms:line>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary"
                    data-dismiss="modal"><%=Strings._cancel.html(locale)%>
            </button>
            <button type="submit" class="btn btn-primary"><%=Strings._add.html(locale)%>
            </button>
        </div>
        </cms:form>
    </div>
</div>
