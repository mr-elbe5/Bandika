<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.cms.page.PagePartData" %>
<%@ page import="de.elbe5.cms.page.PagePartBean" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata=RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    List<PagePartData> parts = PagePartBean.getInstance().getSharedPageParts();
    List<PagePartData> orphanedParts = PagePartBean.getInstance().getOrphanedPageParts();
    int partId = rdata.getInt("partId");
%>

                            <li class="open">
                                <%=Strings._sharedParts.html(locale)%>
                                <ul>
                                    <%
                                        if (parts != null) {
                                            for (PagePartData part : parts) {
                                    %>
                                    <li class="<%=partId==part.getId() ? "open" : ""%>">
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=part.getId()%>&nbsp;<%=StringUtil.toHtml(part.getName())%>&nbsp;(<%=StringUtil.toHtml(part.getTemplateName())%>)</span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return linkTo('/page/deletePagePart?partId=<%=part.getId()%>');"><%=Strings._delete.html(locale)%></a>
                                        </div>
                                    </li>
                                    <%
                                            }
                                        }
                                    %>
                                </ul>
                            </li>
                            <li class="open">
                                <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._orphanedParts.html(locale)%></span>
                                <div class="dropdown-menu">
                                    <a class="dropdown-item" href="" onclick="if (confirmDelete()) return openModalDialog('/page/deleteAllOrphanedPageParts');"><%=Strings._deleteAll.html(locale)%></a>
                                </div>
                                <ul>
                                    <%
                                        if (orphanedParts != null) {
                                            for (PagePartData part : orphanedParts) {
                                    %>
                                    <li class="<%=partId==part.getId() ? "open" : ""%>">
                                        <span class="dropdown-toggle" data-toggle="dropdown"><%=part.getId()%>&nbsp;(<%=StringUtil.toHtml(part.getName())%> <%=StringUtil.toHtml(part.getTemplateName())%>)</span>
                                        <div class="dropdown-menu">
                                            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return openModalDialog('/page/deletePagePart?partId=<%=part.getId()%>');"><%=Strings._delete.html(locale)%></a>
                                        </div>
                                    </li>
                                    <%
                                            }
                                        }
                                    %>
                                </ul>
                            </li>
