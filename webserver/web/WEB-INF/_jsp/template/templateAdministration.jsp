<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.rights.SystemZone" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.template.TemplateFactory" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.template.TemplateBean" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.template.TemplateInfo" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
%>
<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <% if (rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT)) {%>
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=Strings.html("_templates",locale)%>
                </a>
                <div class="icons">
                    <a class="icon fa fa-upload" href="" onclick="return openModalDialog('/ctrl/template/openImportTemplates');" title="<%=Strings.html("_import",locale)%>"> </a>
                </div>
                <ul>
                    <% for (TemplateInfo info : TemplateFactory.getInfos()){
                        List<String> templateNames = TemplateBean.getInstance().getTemplateNames(info.getType());
                        assert (templateNames != null);
                    %>
                    <li class="open">
                        <span><%=Strings.html(info.getKey(),locale)%></span>
                        <div class="icons">
                            <a class="icon fa fa-plus" href="" onclick="return openModalDialog('/ctrl/template/openCreateTemplate?templateType=<%=info.getType()%>');" title="<%=Strings.html("_new",locale)%>"> </a>
                        </div>
                        <ul>
                            <%for (String templateName : templateNames) {%>
                            <li>
                                <span><%=StringUtil.toHtml(templateName)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href="" onclick="return openModalDialog('/ctrl/template/openEditTemplate?templateType=<%=info.getType()%>&templateName=<%=templateName%>');" title="<%=Strings.html("_edit",locale)%>"> </a>
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/template/deleteTemplate?templateType=<%=info.getType()%>&templateName=<%=templateName%>');" title="<%=Strings.html("_delete",locale)%>"> </a>
                                </div>
                            </li>
                            <%}%>
                        </ul>
                    </li>
                    <%}%>
                </ul>
            </li>
            <%}%>
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>

    
