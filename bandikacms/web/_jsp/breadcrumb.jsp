<%--
	Bandika! - A Java based Content Management System
	Copyright (C) 2009-2011 Michael Roennau

	This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
	This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
	You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

	Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.MenuController" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.RequestData" %>
<%
	RequestData rdata = RequestHelper.getRequestData(request);
	int id = rdata.getParamInt("id", RequestData.ROOT_PAGE_ID);
  ArrayList<PageData> list = MenuController.getInstance().getBreadcrumbList(id);
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
							<a href="/_page?id=<%=node.getId()%>"><%=FormatHelper.toHtml(node.getName())%></a>
				    <%}
          }%>
			</div>

