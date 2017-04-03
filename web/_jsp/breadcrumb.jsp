<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

	Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.base.BaseConfig" %>
<%@ page import="de.bandika.menu.MenuController" %>
<%@ page import="de.bandika.base.Controller" %>
<%
	RequestData rdata = HttpHelper.ensureRequestData(request);
	int id = rdata.getParamInt("id", BaseConfig.ROOT_PAGE_ID);
  ArrayList<PageData> list = ((MenuController) Controller.getController(MenuController.KEY_MENU)).getBreadcrumbList(id);
	PageData node;
%>
			<div class="breadcrumb">
				<%
					if (list!=null){
						for (int i=0;i<list.size();i++){
							node=list.get(i);
				      if (i>0){%>
				        &nbsp;&gt;&nbsp;
				      <%}%>
							<a href="/index.jsp?ctrl=<%=PageController.KEY_PAGE%>&id=<%=node.getId()%>"><%=Formatter.toHtml(node.getName())%></a>
				    <%}
          }%>
			</div>

