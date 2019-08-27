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
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();%>
<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <ul class="tree">
            <li class="open">
                <a class="treeRoot"><%=Strings.html("_system",locale)%>
                </a>
                <ul>
                    <%if (rdata.hasSystemRight(SystemZone.APPLICATION, Right.EDIT)) {%>
                    <li>
                        <a href="" onclick="return openModalDialog('/ctrl/admin/openExecuteDatabaseScript');"><%=Strings.html("_executeDatabaseScript",locale)%>
                        </a>
                    </li>
                    <li>
                        <a href="" onclick="if (confirmExecute()) return openModalDialog('/ctrl/admin/restart');"><%=Strings.html("_restart",locale)%>
                        </a>
                    <li class="open">
                        <a><%=Strings.html("_caches",locale)%>
                        </a>
                        <ul>
                            <li>
                                <span><%=Strings.html("_binaryFileCache",locale)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-trash-o" href="/ctrl/admin/clearFileCache" title="<%=Strings.html("_clear",locale)%>"></a>
                                </div>
                            </li>
                            <li>
                                <span><%=Strings.html("_fileCache",locale)%></span>
                                <div class="icons">
                                    <a class="icon fa fa-refresh" href="/ctrl/admin/reloadCache" title="<%=Strings.html("_reload",locale)%>"></a>
                                </div>
                            </li>
                        </ul>
                    </li>
                    <jsp:include page="../search/searchAdministration.inc.jsp" flush="true"/>
                    <jsp:include page="../timer/timerAdministration.inc.jsp" flush="true"/>
                    <jsp:include page="../user/userAdministration.inc.jsp" flush="true"/>
                    <%}%>
                </ul>
            </li>
        </ul>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>
