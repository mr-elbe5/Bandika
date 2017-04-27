<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.rights.Right" %>
<%@ page import="de.bandika.rights.SystemZone" %>
<%@ page import="de.bandika.servlet.RequestReader" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.template.TemplateBean" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="de.bandika.cms.template.TemplateType" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%
    if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
        Locale locale = SessionReader.getSessionLocale(request);
        Map<TemplateType, List<TemplateData>> templates = TemplateBean.getInstance().getAllTemplates();
        assert (templates != null);
        String templateName = RequestReader.getString(request, "templateName");
%>
<li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class="icn itemplate contextSource"><%=StringUtil.getHtml("_templates", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iimport" onclick="return openLayerDialog('<%=StringUtil.getHtml("_importTemplates",locale)%>', '/template.ajx?act=openImportTemplates')"><%=StringUtil.getHtml("_import", locale)%>
        </div>
    </div>
    <ul>
        <li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
            <div class="contextSource"><%=StringUtil.getHtml("_masterTemplates", locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newMasterTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&templateType=<%=TemplateType.MASTER%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateType.MASTER)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="$('#details').load('/template.ajx?act=showTemplateDetails&templateType=<%=TemplateType.MASTER%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateType.MASTER%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateType.MASTER%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </li>
        <li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
            <div class="contextSource"><%=StringUtil.getHtml("_pageTemplates", locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newPageTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&templateType=<%=TemplateType.PAGE%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateType.PAGE)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="$('#details').load('/template.ajx?act=showTemplateDetails&templateType=<%=TemplateType.PAGE%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateType.PAGE%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateType.PAGE%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </li>
        <li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
            <div class="contextSource"><%=StringUtil.getHtml("_partTemplates", locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newPartTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&templateType=<%=TemplateType.PART%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateType.PART)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="$('#details').load('/template.ajx?act=showTemplateDetails&templateType=<%=TemplateType.PART%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateType.PART%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateType.PART%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </li>
        <li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
            <div class="contextSource"><%=StringUtil.getHtml("_templateSnippets", locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newTemplateSnippet",locale)%>', '/template.ajx?act=openCreateTemplate&templateType=<%=TemplateType.SNIPPET%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateType.SNIPPET)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="$('#details').load('/template.ajx?act=showTemplateDetails&templateType=<%=TemplateType.SNIPPET%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplateSnippet",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateType.SNIPPET%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateType.SNIPPET%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%
                    }
                %>
            </ul>
        </li>
    </ul>
</li>
<%}%>