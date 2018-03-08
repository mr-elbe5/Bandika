<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.application.DynamicsActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.CONTENT, Right.EDIT)) {
%>
<!--styles-->
<li>
    <div class="contextSource icn idynamics" ><%=StringUtil.getHtml("_dynamics", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn istyle" onclick="return openLayerDialog('<%=StringUtil.getHtml("_styles",locale)%>', '/dynamics.ajx?act=<%=DynamicsActions.openEditCss%>')"><%=StringUtil.getHtml("_styles", locale)%>
        </div>
        <div class="icn iscript" onclick="return openLayerDialog('<%=StringUtil.getHtml("_scripts",locale)%>', '/dynamics.ajx?act=<%=DynamicsActions.openEditJs%>')"><%=StringUtil.getHtml("_scripts", locale)%>
        </div>
    </div>
</li>

<%}%>