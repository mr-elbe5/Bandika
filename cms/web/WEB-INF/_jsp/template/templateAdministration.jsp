<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.SystemZone" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="de.elbe5.cms.template.TemplateBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.cms.template.TemplateData" %>
<%@ page import="java.util.Map" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    Map<String, List<String>> templateNames = TemplateBean.getInstance().getAllTemplateNames();
    assert (templateNames != null);
%>

<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <% if (rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT)) {
        %>
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=Strings._templates.html(locale)%>
                </a>
                <div class="icons">
                    <a class="icon fa fa-upload" href=""
                       onclick="return openModalDialog('/template/openImportTemplates');" title="<%=Strings._import.html(locale)%>">
                    </a>
                </div>
                <ul>
                    <li class="open">
                        <span><%=Strings._masterTemplates.html(locale)%></span>
                        <div class="icons">
                            <a class="icon fa fa-plus" href=""
                               onclick="return openModalDialog('/template/openCreateTemplate?templateType=<%=TemplateData.TYPE_MASTER%>');" title="<%=Strings._new.html(locale)%>">
                            </a>
                        </div>
                        <ul>
                            <%
                                for (String templateName : templateNames.get(TemplateData.TYPE_MASTER)) {
                            %>
                            <li>
                                <span><%=StringUtil.toHtml(templateName)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href=""
                                       onclick="return openModalDialog('/template/openEditTemplate?templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=templateName%>');" title="<%=Strings._edit.html(locale)%>">
                                    </a>
                                    <a class="icon fa fa-trash-o" href=""
                                       onclick="if (confirmDelete()) return linkTo('/template/deleteTemplate?templateType=<%=TemplateData.TYPE_MASTER%>&templateName=<%=templateName%>');" title="<%=Strings._delete.html(locale)%>">
                                    </a>
                                </div>
                            </li>
                            <%
                                }
                            %>
                        </ul>
                    </li>
                    <li class="open">
                        <span><%=Strings._pageTemplates.html(locale)%></span>
                        <div class="icons">
                            <a class="icon fa fa-plus" href=""
                               onclick="return openModalDialog('/template/openCreateTemplate?templateType=<%=TemplateData.TYPE_PAGE%>');" title="<%=Strings._new.html(locale)%>">
                            </a>
                        </div>
                        <ul>
                            <%
                                for (String templateName : templateNames.get(TemplateData.TYPE_PAGE)) {
                            %>
                            <li>
                                <span><%=StringUtil.toHtml(templateName)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href=""
                                       onclick="return openModalDialog('/template/openEditTemplate?templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=templateName%>');" title="<%=Strings._edit.html(locale)%>">
                                    </a>
                                    <a class="icon fa fa-trash-o" href=""
                                       onclick="if (confirmDelete()) return linkTo('/template/deleteTemplate?templateType=<%=TemplateData.TYPE_PAGE%>&templateName=<%=templateName%>');" title="<%=Strings._delete.html(locale)%>">
                                    </a>
                                </div>
                            </li>
                            <%
                                }
                            %>
                        </ul>
                    </li>
                    <li class="open">
                        <span><%=Strings._partTemplates.html(locale)%></span>
                        <div class="icons">
                            <a class="icon fa fa-plus" href=""
                               onclick="return openModalDialog('/template/openCreateTemplate?templateType=<%=TemplateData.TYPE_PART%>');" title="<%=Strings._new.html(locale)%>">
                            </a>
                        </div>
                        <ul>
                            <%
                                for (String templateName : templateNames.get(TemplateData.TYPE_PART)) {
                            %>
                            <li>
                                <span><%=StringUtil.toHtml(templateName)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-pencil" href=""
                                       onclick="return openModalDialog('/template/openEditTemplate?templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=templateName%>');" title="<%=Strings._edit.html(locale)%>">
                                    </a>
                                    <a class="icon fa fa-trash-o" href=""
                                       onclick="if (confirmDelete()) return linkTo('/template/deleteTemplate?templateType=<%=TemplateData.TYPE_PART%>&templateName=<%=templateName%>');" title="<%=Strings._delete.html(locale)%>">
                                    </a>
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
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>

    
