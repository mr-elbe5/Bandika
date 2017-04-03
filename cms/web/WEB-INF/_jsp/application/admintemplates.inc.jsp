<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><!DOCTYPE html><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.configuration.GeneralRightsProvider" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.template.TemplateBean" %>
<%@ page import = "de.elbe5.cms.template.TemplateData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    TemplateBean ts = TemplateBean.getInstance();
    List<TemplateData> masterTemplates = ts.getAllTemplates(TemplateData.TYPE_MASTER);
    List<TemplateData> layoutTemplates = ts.getAllTemplates(TemplateData.TYPE_PAGE);
    List<TemplateData> partTemplates = ts.getAllTemplates(TemplateData.TYPE_PART);
    String templateName = RequestHelper.getString(request, "templateName");
    String templateType = RequestHelper.getString(request, "templateType");
    if (SessionHelper.hasAnyRight(request, GeneralRightsProvider.RIGHTS_TYPE_GENERAL)) {
%>
<li<%=!templateName.isEmpty() || !templateType.isEmpty() ? " class=\"open\"" : ""%>>
    <div class = "icn itemplate contextSource"><%=StringUtil.getHtml("_templates", locale)%>
    </div>
    <div class = "contextMenu">
        <div class="icn iimport" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_importTemplates",locale)%>', '/template.ajx?act=openImportTemplates')"><%=StringUtil.getHtml("_import", locale)%>
        </div>
    </div>
    <ul>
        <li<%=templateType.equals(TemplateData.TYPE_MASTER) ? " class=\"open\"" : ""%>>
            <div class = "contextSource"><%=StringUtil.getHtml("_masterTemplates", locale)%>
            </div>
            <div class = "contextMenu">
                <div class="icn inew" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_newMasterTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&type=<%=TemplateData.TYPE_MASTER%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%if (masterTemplates != null) {
                    for (TemplateData template : masterTemplates) {%>
                <li>
                    <div class = "contextSource icn itemplate <%=templateName.equals(template.getFileName()) ? "selected" : ""%>"
                            onclick = "$('#properties').load('/template.ajx?act=showTemplateProperties&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getFileName()%>')"><%=StringUtil.toHtml(template.getFileName())%>
                    </div>
                    <div class = "contextMenu">
                        <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idownload" onclick = "return linkTo('/template.srv?act=downloadTemplate&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_download", locale)%>
                        </div>
                        <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%}
                }%>
            </ul>
        </li>
        <li<%=templateType.equals(TemplateData.TYPE_PAGE) ? " class=\"open\"" : ""%>>
            <div class = "contextSource"><%=StringUtil.getHtml("_pageTemplates", locale)%>
            </div>
            <div class = "contextMenu">
                <div class="icn inew" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_newPageTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&type=<%=TemplateData.TYPE_PAGE%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%if (masterTemplates != null) {
                    for (TemplateData template : layoutTemplates) {%>
                <li>
                    <div class = "contextSource icn itemplate <%=templateName.equals(template.getFileName()) ? "selected" : ""%>"
                            onclick = "$('#properties').load('/template.ajx?act=showTemplateProperties&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getFileName()%>')"><%=StringUtil.toHtml(template.getFileName())%>
                    </div>
                    <div class = "contextMenu">
                        <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idownload" onclick = "return linkTo('/template.srv?act=downloadTemplate&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_download", locale)%>
                        </div>
                        <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%}
                }%>
            </ul>
        </li>
        <li<%=templateType.equals(TemplateData.TYPE_PART) ? " class=\"open\"" : ""%>>
            <div class = "contextSource"><%=StringUtil.getHtml("_partTemplates", locale)%>
            </div>
            <div class = "contextMenu">
                <div class="icn inew" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_newPartTemplate",locale)%>', '/template.ajx?act=openCreateTemplate&type=<%=TemplateData.TYPE_PART%>')"><%=StringUtil.getHtml("_new", locale)%>
                </div>
            </div>
            <ul>
                <%if (partTemplates != null) {
                    for (TemplateData template : partTemplates) {%>
                <li>
                    <div class = "contextSource icn itemplate <%=templateName.equals(template.getFileName()) ? "selected" : ""%>"
                            onclick = "$('#properties').load('/template.ajx?act=showTemplateProperties&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getFileName()%>')"><%=StringUtil.toHtml(template.getFileName())%>
                    </div>
                    <div class = "contextMenu">
                        <div class="icn iedit" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_editTemplate",locale)%>', '/template.ajx?act=openEditTemplate&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_edit", locale)%>
                        </div>
                        <div class="icn idownload" onclick = "return linkTo('/template.srv?act=downloadTemplate&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_download", locale)%>
                        </div>
                        <div class="icn idelete" onclick = "return openModalLayerDialog('<%=StringUtil.getHtml("_deleteTemplate",locale)%>', '/template.ajx?act=openDeleteTemplate&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=template.getFileName()%>');"><%=StringUtil.getHtml("_delete", locale)%>
                        </div>
                    </div>
                </li>
                <%}
                }%>
            </ul>
        </li>
    </ul>
</li>
<%}%>