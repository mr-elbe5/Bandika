<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.template.TemplateBean" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="de.elbe5.cms.template.TemplateActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    Map<String, List<String>> templateNames = TemplateBean.getInstance().getAllTemplateNames();
    assert (templateNames != null);
    String requestedTemplateName = RequestReader.getString(request, "templateName");
%>

                            <li class="open">
                                <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._templates.html(locale)%></span>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openImportTemplates%>');"><%=Strings._import.html(locale)%></a>
                                </div>
                                <ul>
                                    <li<%=!requestedTemplateName.isEmpty() ? " class=\"open\"" : ""%>>
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._masterTemplates.html(locale)%></span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>');"><%=Strings._new.html(locale)%></a>
                                        </div>
                                        <ul>
                                            <%
                                                for (String templateName : templateNames.get(TemplateData.TYPE_MASTER)) {
                                            %>
                                            <li class="<%=requestedTemplateName.equals(templateName) ? "open" : ""%>">
                                                <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(templateName)%></span>
                                                <div class="dropdown-menu">
                                                <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=templateName%>');"><%=Strings._edit.html(locale)%></a>
                                                <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/template.ajx?act=<%=TemplateActions.deleteTemplate%>&templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=templateName%>');"><%=Strings._delete.html(locale)%></a>
                                                </div>
                                            </li>
                                            <%
                                                }
                                            %>
                                        </ul>
                                    </li>
                                    <li<%=!requestedTemplateName.isEmpty() ? " class=\"open\"" : ""%>>
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._pageTemplates.html(locale)%></span>
                                        <div class="dropdown-menu">
                                        <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>');"><%=Strings._new.html(locale)%></a>
                                        </div>
                                        <ul>
                                            <%
                                                for (String templateName : templateNames.get(TemplateData.TYPE_PAGE)) {
                                            %>
                                            <li class="<%=requestedTemplateName.equals(templateName) ? "open" : ""%>">
                                                <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(templateName)%></span>
                                                <div class="dropdown-menu">
                                                <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=templateName%>');"><%=Strings._edit.html(locale)%></a>
                                                <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/template.ajx?act=<%=TemplateActions.deleteTemplate%>&templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=templateName%>');"><%=Strings._delete.html(locale)%></a>
                                                </div>
                                            </li>
                                            <%
                                                }
                                            %>
                                        </ul>
                                    </li>
                                    <li<%=!requestedTemplateName.isEmpty() ? " class=\"open\"" : ""%>>
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._partTemplates.html(locale)%></span>
                                        <div class="dropdown-menu">
                                        <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openCreateTemplate%>&templateType=<%=TemplateData.TYPE_PART%>');"><%=Strings._new.html(locale)%></a>
                                        </div>
                                        <ul>
                                            <%
                                                for (String templateName : templateNames.get(TemplateData.TYPE_PART)) {
                                            %>
                                            <li class="<%=requestedTemplateName.equals(templateName) ? "open" : ""%>">
                                                <span class="dropdown-toggle" data-toggle="dropdown"><%=StringUtil.toHtml(templateName)%></span>
                                                <div class="dropdown-menu">
                                                <a class="dropdown-item" href="" onclick="return openModalDialog('/template.ajx?act=<%=TemplateActions.openEditTemplate%>&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=templateName%>');"><%=Strings._edit.html(locale)%></a>
                                                <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/template.ajx?act=<%=TemplateActions.deleteTemplate%>&templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=templateName%>');"><%=Strings._delete.html(locale)%></a>
                                                </div>
                                            </li>
                                            <%
                                                }
                                            %>
                                        </ul>
                                    </li>
                                </ul>
                            </li>
