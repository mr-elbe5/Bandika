<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.templatepage.PagePartFlexClass" %>
<%@ page import="de.elbe5.cms.page.templatepage.templatepagepart.TemplatePagePartData" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.template.TemplateBean" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    int pageId = rdata.getId();
    String sectionName = rdata.getString("sectionName");
    int partId = rdata.getInt("partId");
    String partType = rdata.getString("partType");
    boolean addBelow = rdata.getBoolean("addBelow", true);
    String url = "/templatepage/addPagePart/" + pageId;
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._newPagePart.html(locale)%> (<%=partType%>)
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="partform" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <input type="hidden" name="partId" value="<%=partId%>"/>
                <input type="hidden" name="partType" value="<%=partType%>"/>
                <input type="hidden" name="sectionName" value="<%=sectionName%>"/>
                <cms:line label="<%=Strings._position.toString()%>" padded="true">
                    <cms:radio name="below" value="false" checked="<%=!addBelow%>"><%=Strings._above.html(locale)%>
                    </cms:radio>&nbsp;<cms:radio name="below" value="true"
                                                 checked="<%=addBelow%>"><%=Strings._below.html(locale)%>
                </cms:radio>
                </cms:line>
                <cms:select name="flexClass" label="<%=Strings._flexClass.toString()%>" required="true">
                    <% String selection = Statics.DEFAULT_CLASS.name();
                        for (PagePartFlexClass css : PagePartFlexClass.values()) {%>
                    <option value="<%=css.getCssClass()%>" <%=css.name().equals(selection) ? "selected" : ""%>><%=css.name()%>
                    </option>
                    <%}%>
                </cms:select>
                <% if (partType.equals(TemplatePagePartData.class.getSimpleName())){
                    List<TemplateData> templates = TemplateBean.getInstance().getAllTemplates(TemplateData.TYPE_PART);
                %>
                <cms:line label="<%=Strings._template.toString()%>" padded="true">
                    <div >
                        <% for (TemplateData tdata : templates) {%>
                        <cms:radio name="template"
                                   value="<%=tdata.getName()%>"><%=StringUtil.toHtml(tdata.getDisplayName())%>
                        </cms:radio><br/>
                        <%}%>
                    </div>
                </cms:line>
                <%}%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary"
                        data-dismiss="modal"><%=Strings._cancel.html(locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings._add.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>
