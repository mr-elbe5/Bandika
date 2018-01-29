<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.rights.Right" %>
<%@ page import="de.bandika.webbase.rights.SystemZone" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.search.SearchActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    if (SessionReader.hasSystemRight(request, SystemZone.APPLICATION, Right.EDIT)) {
%><!--search-->
<li>
    <div class="contextSource icn isearch" onclick="$('#details').load('/search.ajx?act=<%=SearchActions.showAdminSearchDetails%>')"><%=StringUtil.getHtml("_search", locale)%>
    </div>
    <div class="contextMenu">
        <div class="icn isync" onclick="linkTo('/search.srv?act=<%=SearchActions.indexAllContent%>');"><%=StringUtil.getHtml("_indexAllContent", locale)%>
        </div>
        <div class="icn isync" onclick="linkTo('/search.srv?act=<%=SearchActions.indexAllUsers%>');"><%=StringUtil.getHtml("_indexAllUsers", locale)%>
        </div>
    </div>
</li>
<%}%>