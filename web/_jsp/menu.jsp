<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.StdServlet" %>
<%@ page import="de.bandika.menu.MenuController" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="java.io.IOException" %>
<%@ page import="de.bandika.base.Controller" %>
<%@ page import="de.bandika.base.UserStrings" %>
<%@ page import="de.bandika.base.BaseConfig" %>
<%@ page import="de.bandika.admin.AdminData" %>
<%@ page import="de.bandika.http.RequestData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.user.UserController" %>
<%
	SessionData sdata = HttpHelper.ensureSessionData(request);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  int id=rdata.getCurrentPageId();
  MenuController mc=((MenuController) Controller.getController(MenuController.KEY_MENU));
  PageData rootNode= mc.getRootPage();
  ArrayList<PageData> parents=mc.getParentList(id);
%>
<%!
  protected static void addNode(PageData page, int level, int currentId, ArrayList<PageData> parents, SessionData sdata, JspWriter writer) throws IOException {
    if (level>0){
      writer.write("<li");
      if (parents.contains(page)){
        writer.write(" class=\"open\"");
      }
      writer.write("><a href=\"/index.jsp?id=");
      writer.write(Integer.toString(page.getId()));
      if (page.getId()==currentId)
        writer.write("\" class=\"selected\">");
      else
        writer.write("\">");
      writer.write(Formatter.toHtml(page.getName()));
      writer.write("</a>");
    }
    ArrayList<PageData> children=page.getChildPages();
    if (children != null) {
      int count=0;
			for (PageData child : children) {
        if (!child.isInMenu() || (child.isRestricted() && !sdata.isEditor() && !sdata.hasUserReadRight(child.getId())))
					continue;
        if (count==0 && level>0)
          writer.write("<ul>");
        addNode(child,level+1,currentId,parents, sdata,  writer);
        count++;
			}
      if (count>0 && level>0)
        writer.write("</ul>");
		}
    if (level>0){
      writer.write("</li>");
    }
  }
%>
    <div class="menu">
	    <div class="menuHeader">&nbsp;</div>
      <div class="menuContent">
        <div class="m_header"><a href="/index.jsp?id=<%=rootNode.getId()%>"><%=rootNode.getName()%></a></div>
        <div id="menuDiv">
          <ul id="navigation">
            <%addNode(rootNode,0,id,parents,sdata,out);%>
	        </ul>
        </div>
        <div>&nbsp;</div>
      </div>
      <div class="menuFooter">&nbsp;</div>
      <% if (sdata.isLoggedIn()) {%>
      <div class="menuHeader">&nbsp;</div>
      <div class="menuContent">
        <div class="m_header"><%=UserStrings.system%></div>
        <ul id="userNavigation">
          <li><%=UserStrings.user%>
            <ul>
              <li><a href="/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=logout"><%=UserStrings.logout%></a></li>
              <li><a href="/index.jsp?ctrl=<%=UserController.KEY_USER%>&method=openChangePassword"><%=UserStrings.changepassword%></a></li>
            </ul>
          </li>
          <% if (sdata.isEditor() || sdata.isAdmin()) {%>
          <li><%=UserStrings.administration%>
            <ul>
              <% if (sdata.isEditor()) {%>
              <li><a href="/_jsp/pageEditContent.jsp?ctrl=page&method=openEdit&id=<%=id%>">Edit Page</a></li>
              <li><a href="/_jsp/pageEditMetaData.jsp?ctrl=page&method=openCreate&parent=<%=id%>">Add Child Page</a></li>
              <li><a href="/_jsp/pageSortChildren.jsp?ctrl=page&method=openSortChildren&id=<%=id%>">Sort Child Pages</a></li>
              <%if (id!= BaseConfig.ROOT_PAGE_ID){%>
              <li><a href="/_jsp/pageDelete.jsp?ctrl=page&method=openDeletePage&id=<%=id%>">Delete Page</a></li>
              <%}
              } if (sdata.isAdmin()){
              for (AdminData ad : AdminData.getLinks()) {%>
              <li><a href="<%=ad.getUrl()%>"><%=ad.getName()%></a></li>
              <%}
              }
            }%>
            </ul>
          </li>
        </ul>
        <div>&nbsp;</div>
      </div>
      <div class="menuFooter">&nbsp;</div>
      <%}%>
      <div class="imprint">
        &copy; www.bandika.de
      </div>
    </div>



