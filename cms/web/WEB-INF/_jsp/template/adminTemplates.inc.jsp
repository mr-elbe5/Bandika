<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.template.TemplateBean" %>
<%@ page import="de.bandika.cms.template.TemplateData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.bandika.cms.template.TemplateActions" %>
<%
    if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
        Locale locale = SessionReader.getSessionLocale(request);
        Map<String, List<TemplateData>> templates = TemplateBean.getInstance().getAllTemplates();
        assert (templates != null);
        String templateName = RequestReader.getString(request, "templateName");
%>
<li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
    <div class="icn itemplate contextSource"><%=StringUtil.getHtml("_templates", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn iimport" onclick="return openLayerDialog('<%=StringUtil.getHtml("_importTemplates",locale)%>', '/template.ajx?act=<%=TemplateActions.openImportTemplates%>')"><%=StringUtil.getHtml("_import", locale)%>
        </div>
    </div>
    <ul>
        <li<%=!templateName.isEmpty() ? " class=\"open\"" : ""%>>
            <div class="contextSource"><%=StringUtil.getHtml("_masterTemplates", locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newMasterTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateData.TYPE_MASTER)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/template.ajx?act=<%=TemplateActions.showTemplateDetails%>&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openDeleteTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
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
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newPageTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateData.TYPE_PAGE)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/template.ajx?act=<%=TemplateActions.showTemplateDetails%>&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openDeleteTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
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
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newPartTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_PART%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateData.TYPE_PART)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/template.ajx?act=<%=TemplateActions.showTemplateDetails%>&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openDeleteTemplate%>&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
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
                <div class="icn inew" onclick="return openLayerDialog('<%=StringUtil.getHtml("_newTemplateSnippet",locale)%>', '/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_SNIPPET%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%
                    for (TemplateData template : templates.get(TemplateData.TYPE_SNIPPET)) {
                %>
                <li>
                    <div class="contextSource icn itemplate <%=templateName.equals(template.getName()) ? "selected" : ""%>" onclick="return openLayerDialog('<%=StringUtil.getHtml("_details",locale)%>', '/template.ajx?act=<%=TemplateActions.showTemplateDetails%>&templateType=<%=TemplateData.TYPE_SNIPPET%>&templateName=<%=template.getName()%>')"><%=StringUtil.toHtml(template.getName())%>
                    </div>
                    <div class="contextMenu">
                        <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_editTemplateSnippet",locale)%>', '/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_SNIPPET%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idelete" onclick="return openLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=<%=TemplateActions.openDeleteTemplate%>&templateType=<%=TemplateData.TYPE_SNIPPET%>&templateName=<%=template.getName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
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