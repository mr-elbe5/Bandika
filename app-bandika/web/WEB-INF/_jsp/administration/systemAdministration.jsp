<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.rights.SystemZone" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
%>
            <div id="pageContent">
                <cms:message />
                <section class="treeSection">
                    <ul class="tree">
                        <li class="open">
                            <a class="treeRoot"><%=Strings._system.html(locale)%></a>
                            <ul>
                                <%
                                    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
                                %>
                                <li><a href="" onclick="return openModalDialog('/admin.ajx?act=<%=AdminActions.openExecuteDatabaseScript%>');"><%=Strings._executeDatabaseScript.html(locale)%></a></li>
                                <li><a href="" onclick="if (confirmExecute()) return openModalDialog('/admin.srv?act=<%=AdminActions.restart%>');"><%=Strings._restart.html(locale)%></a>
                                <li><a href="" onclick="return openModalDialog('/admin.ajx?act=<%=AdminActions.openEditConfiguration%>');"><%=Strings._settings.html(locale)%></a>
                                <li class="open">
                                    <a><%=Strings._caches.html(locale)%></a>
                                    <ul>
                                        <li>
                                            <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._binaryFileCache.html(locale)%></span>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item" href="/admin.srv?act=<%=AdminActions.clearFileCache%>"><%=Strings._clear.html(locale)%></a>
                                            </div>
                                        </li>
                                        <li>
                                            <span class="dropdown-toggle" data-toggle="dropdown"><%=Strings._fileCache.html(locale)%></span>
                                            <div class="dropdown-menu">
                                                <a class="dropdown-item" href="/file.srv?act=<%=FileActions.reloadCache%>"><%=Strings._reload.html(locale)%></a>
                                            </div>
                                        </li>
                                    </ul>
                                </li>
                                <jsp:include page="searchAdministration.inc.jsp" flush="true" />
                                <jsp:include page="timerAdministration.inc.jsp" flush="true" />
                                <jsp:include page="userAdministration.inc.jsp" flush="true" />
                                <%}%>
                            </ul>
                        </li>
                    </ul>
                </section>
            </div>
            <script type="text/javascript">
                $('.tree').treed('fa fa-minus-square-o','fa fa-plus-square-o');
            </script>
