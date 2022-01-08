<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.SessionRequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.page.PagePartData" %>
<%@ page import="de.elbe5.page.PagePartFactory" %>
<%@ page import="de.elbe5.layout.LayoutCache" %>
<%@ page import="de.elbe5.layout.LayoutData" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    SessionRequestData rdata = SessionRequestData.getRequestData(request);
    Locale locale = rdata.getLocale();
    PageData contentData = rdata.getCurrentContent(PageData.class);
    assert contentData != null;
    PagePartData partData = rdata.get(PagePartData.KEY_PART, PagePartData.class);
    assert partData != null;
    List<LayoutData> partLayouts = LayoutCache.getLayouts(PagePartData.LAYOUT_TYPE);
%>
            <input type="hidden" name="<%=partData.getPartPositionName()%>" value="<%=partData.getPosition()%>" class="partPos"/>
            <div class="partEditButtons">
                <div class="btn-group btn-group-sm" role="group">
                    <div class="btn-group btn-group-sm" role="group">
                        <button type="button" class="btn btn-secondary fa fa-plus dropdown-toggle" data-toggle="dropdown" title="<%=$SH("_newPart",locale)%>"></button>
                        <div class="dropdown-menu">
                            <% for (String partType : PagePartFactory.getTypes()) {
                                if (PagePartFactory.useLayouts(partType)){
                                    for (LayoutData layout : partLayouts){%>
                            <a class="dropdown-item" href="" onclick="return addPart(<%=partData.getId()%>,'<%=$H(partData.getSectionName())%>','<%=partType%>','<%=$H(layout.getName())%>');"><%=$SH(layout.getKey(),locale)%>
                            </a>
                                    <%}
                                } else {%>
                            <a class="dropdown-item" href="" onclick="return addPart(<%=partData.getId()%>,'<%=$H(partData.getSectionName())%>','<%=partType%>');"><%=$SH("class."+partType, locale)%>
                            </a>
                            <%}
                            }%>
                        </div>
                    </div>
                    <div class="btn-group btn-group-sm" role="group">
                        <button type="button" class="btn  btn-secondary dropdown-toggle fa fa-ellipsis-h" data-toggle="dropdown" title="<%=$SH("_more",locale)%>"></button>
                        <div class="dropdown-menu">
                            <a class="dropdown-item" href="" onclick="return movePart(<%=partData.getId()%>,-1);"><%=$SH("_up", locale)%>
                            </a>
                            <a class="dropdown-item" href="" onclick="return movePart(<%=partData.getId()%>,1);"><%=$SH("_down", locale)%>
                            </a>
                            <a class="dropdown-item" href="" onclick="if (confirmDelete()) return deletePart(<%=partData.getId()%>);"><%=$SH("_delete", locale)%>
                            </a>
                        </div>
                    </div>
                </div>
            </div>








