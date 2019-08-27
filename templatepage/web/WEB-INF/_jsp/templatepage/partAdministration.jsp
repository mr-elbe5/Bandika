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
<%@ page import="de.elbe5.templatepage.PagePartData" %>
<%@ page import="de.elbe5.templatepage.TemplatePageBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    List<PagePartData> parts = TemplatePageBean.getInstance().getSharedPageParts();
    List<PagePartData> orphanedParts = TemplatePageBean.getInstance().getOrphanedPageParts();
    int partId = rdata.getInt("partId");
%>

<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=Strings.html("_pageParts",locale)%>
                </a>
                <ul>
                    <% if (rdata.hasSystemRight(SystemZone.CONTENT, Right.EDIT)) {%>
                    <li class="open">
                        <%=Strings.html("_sharedParts",locale)%>
                        <ul>
                            <%
                                if (parts != null) {
                                    for (PagePartData part : parts) {
                            %>
                            <li class="<%=partId==part.getId() ? "open" : ""%>">
                                <span><%=part.getId()%>&nbsp;<%=StringUtil.toHtml(part.getName())%>&nbsp;(<%=StringUtil.toHtml(part.getEditTitle(locale))%>)</span>
                                <div class="icons">
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return linkTo('/ctrl/templatepage/deletePagePart?partId=<%=part.getId()%>');" title="<%=Strings.html("_delete",locale)%>"> </a>
                                </div>
                            </li>
                            <%
                                    }
                                }
                            %>
                        </ul>
                    </li>
                    <li class="open">
                        <span><%=Strings.html("_orphanedParts",locale)%></span>
                        <div class="icons">
                            <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return openModalDialog('/ctrl/templatepage/deleteAllOrphanedPageParts');" title="<%=Strings.html("_deleteAll",locale)%>"> </a>
                        </div>
                        <ul>
                            <%
                                if (orphanedParts != null) {
                                    for (PagePartData part : orphanedParts) {
                            %>
                            <li class="<%=partId==part.getId() ? "open" : ""%>">
                                <span><%=part.getId()%>&nbsp;(<%=StringUtil.toHtml(part.getName())%> <%=StringUtil.toHtml(part.getEditTitle(locale))%>)</span>
                                <div class="icons">
                                    <a class="icon fa fa-trash-o" href="" onclick="if (confirmDelete()) return openModalDialog('/ctrl/templatepage/deletePagePart?partId=<%=part.getId()%>');" title="<%=Strings.html("_delete",locale)%>"> </a>
                                </div>
                            </li>
                            <%
                                    }
                                }
                            %>
                        </ul>
                    </li>
                    <%}%>
                </ul>
            </li>
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>

    
