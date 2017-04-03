<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika._base.RequestData" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.application.Configuration" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getCurrentPageId();
  MenuData homePage = MenuCache.getInstance().getHomePage(id);
  ArrayList<Integer> parentIds = MenuCache.getInstance().getParentIds(id);
  parentIds.add(id);
%>
<%!
  protected static void addChildren(MenuData data, int currentId, ArrayList<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
    ArrayList<MenuData> children = data.getChildren();
    if (children != null) {
      for (MenuData child : children) {
        if (!child.isVisibleForUser(sdata))
          continue;
        boolean hasSubChildren = child.getChildren().size() > 0;
        if (hasSubChildren) {
          writer.println("<li class=\"dropdown\">");
          writer.print(String.format("<a class=\"dropdown-toggle %s\" data-toggle=\"dropdown\" href=\"%s\">",
            child.getId() == currentId || parentIds.contains(child.getId()) ? "active" : "",
            child.getUrl()));
          writer.print(FormatHelper.toHtml(child.getName()));
          writer.print("<b class=\"caret\"></b>");
          writer.println("</a>");
          writer.println("<ul class=\"dropdown-menu\">");
          addSubChildren(child, 1, currentId, parentIds, sdata, writer);
          writer.println("</ul>");
        } else {
          writer.println("<li>");
          writer.print(String.format("<a class=\"%s\" href=\"%s\">",
            child.getId() == currentId || parentIds.contains(child.getId()) ? "active" : "",
            child.getUrl()));
          writer.print(FormatHelper.toHtml(child.getName()));
          writer.println("</a>");
        }
        writer.println("</li>");
      }
    }
  }

  protected static void addSubChildren(MenuData data, int level, int currentId, ArrayList<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
    ArrayList<MenuData> children = data.getChildren();
    if (children != null) {
      for (MenuData child : children) {
        if (!child.isVisibleForUser(sdata))
          continue;
        writer.print("<li><a href=\"");
        writer.print(child.getUrl());
        if (child.getId() == currentId || parentIds.contains(child.getId()))
          writer.print("\" class=\"active\">");
        else
          writer.print("\">");
        writer.print(FormatHelper.toHtml(child.getName()));
        writer.print("</a>");
        if (child.getChildren().size() > 0) {
          writer.println("<ul>");
          addSubChildren(child, level + 1, currentId, parentIds, sdata, writer);
          writer.println("</ul>");
        }
        writer.println("</li>");
      }
    }
  }

%>
<div class="navbar">
  <div class="navbar-inner">
    <a class="brand" href="<%=homePage==null ? "/" : homePage.getUrl()%>"><%=Configuration.getAppTitle()%>
    </a>
    <ul class="nav">
      <%
        if (homePage!=null)
          addChildren(homePage, id, parentIds, sdata, out);
      %>
    </ul>
  </div>
</div>
<script type="text/javascript">
  $('.dropdown-toggle').dropdown();
</script>
