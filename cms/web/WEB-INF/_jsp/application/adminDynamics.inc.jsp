<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%@ page import="de.elbe5.webbase.rights.SystemZone" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.DynamicsActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
%>
<!--styles-->
<li>
    <div class="contextSource icn idynamics" ><%=StringUtil.getHtml("_dynamics", locale)%>
    </div>
    <ul>
        <li>
            <div class="contextSource icn istyle " ><%=StringUtil.getHtml("_styles",locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_styles",locale)%>', '/dynamics.ajx?act=<%=DynamicsActions.openEditCss%>')"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
            </div>
        </li>
        <li>
            <div class="contextSource icn iscript " ><%=StringUtil.getHtml("_scripts",locale)%>
            </div>
            <div class="contextMenu">
                <div class="icn iedit" onclick="return openLayerDialog('<%=StringUtil.getHtml("_scripts",locale)%>', '/dynamics.ajx?act=<%=DynamicsActions.openEditJs%>')"><%=StringUtil.getHtml("_edit", locale)%>
                </div>
            </div>
        </li>
    </ul>
</li>

<%}%>