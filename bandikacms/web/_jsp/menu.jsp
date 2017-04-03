<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>
<%@ page import="de.bandika.page.MenuController" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.io.IOException" %>
<%@ page import="de.bandika.base.*" %>
<%@ page import="de.bandika.data.*" %>
<%
	SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
  int id=rdata.getCurrentPageId();
  MenuController mc= MenuController.getInstance();
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
      writer.write(FormatHelper.toHtml(page.getName()));
      writer.write("</a>");
    }
    ArrayList<PageData> children=page.getChildPages();
    if (children != null) {
      int count=0;
			for (PageData child : children) {
        if (!child.isVisible() || (child.isRestricted() && !sdata.hasRight(child.getId(), RightData.RIGHT_READ)))
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
      <div class="menuContent menuAdmin">
        <div class="m_header"><%=Strings.getHtml("user")%></div>
        <div id="userMenuDiv">
          <ul id="userNavigation">
            <li><a href="/_user?method=logout"><%=Strings.getHtml("logout")%></a></li>
            <li><a href="/_user?method=openChangePassword"><%=Strings.getHtml("changepassword")%></a></li>
          </ul>
        </div>
        <div>&nbsp;</div>
      </div>
          <% if (sdata.hasRight(id,RightData.RIGHT_EDIT) || sdata.hasAnyEditRight()) {%>
      <div class="menuContent menuAdmin">
        <div class="m_header"><%=Strings.getHtml("editor")%></div>
        <div id="editorMenuDiv">
          <ul id="editorNavigation">
            <% if (sdata.hasRight(id,RightData.RIGHT_EDIT)) {%>
            <li><a href="/_page?method=openEdit&id=<%=id%>">Edit Page</a></li>
            <li><a href="/_page?method=openCreate&id=<%=id%>">Add Child Page</a></li>
            <li><a href="/_page?method=openSortChildren&id=<%=id%>">Sort Child Pages</a></li>
            <%if (id!= RequestData.ROOT_PAGE_ID){%>
            <li><a href="/_page?method=openDelete&id=<%=id%>">Delete Page</a></li>
            <%}
            }
            if (sdata.hasAnyEditRight()){%>
            <li><a href="/_image?method=openEditImages">Edit Images</a></li>
            <li><a href="/_doc?method=openEditDocuments">Edit Documents</a></li>
            <%}%>
          </ul>
        </div>
        <div>&nbsp;</div>
      </div>
          <% }
            if (sdata.isAdmin()){%>
      <div class="menuContent menuAdmin">
        <div class="m_header"><%=Strings.getHtml("administrator")%></div>
        <div id="adminMenuDiv">
          <ul id="adminNavigation">
            <li><a href="/_admin?method=openConfig">Configuration</a></li>
            <li><a href="/_user?method=openEditUsers">Edit Users</a></li>
            <li><a href="/_user?method=openEditGroups">Edit Groups</a></li>
            <li><a href="/_admin?method=openEditCaches">Edit Caches</a></li>
          </ul>
        </div>
        <div>&nbsp;</div>
      </div>
      <div class="menuFooter">&nbsp;</div>
      <%}}%>
      <div class="imprint">
        &copy; www.bandika.de
      </div>
    </div>



