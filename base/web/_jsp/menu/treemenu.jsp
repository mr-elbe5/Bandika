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
<%@ page import="de.bandika.link.LinkData" %>
<%@ page import="de.bandika.link.LinkCache" %>
<%@ page import="de.bandika.application.StringCache" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  RequestData rdata = RequestHelper.getRequestData(request);
  int id = rdata.getCurrentPageId();
  MenuCache mc = MenuCache.getInstance();
  LinkCache lc = LinkCache.getInstance();
  MenuData rootNode = mc.getRootPage();
  ArrayList<Integer> parentIds = mc.getParentIds(id);
  ArrayList<LinkData> links = lc.getBackendLinks(sdata);
%>
<%!
  protected static void addNode(MenuData data, int level, int currentId, ArrayList<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
    if (level > 0) {
      writer.print("<li");
      if (parentIds.contains(data.getId())) {
        writer.write(" class=\"open\"");
      }
      writer.write("><a ");
      if (data.getId() == currentId)
        writer.write(" class=\"selectedlinks\" ");
      if (data.isEditableForEditor(sdata)) {
        writer.write("href=\"/_page?method=openPageSettings&id=");
        writer.write(Integer.toString(data.getId()));
        writer.write("\">");
      } else
        writer.write(">");
      writer.write(FormatHelper.toHtml(data.getName()));
      writer.write("</a>");
    }
    ArrayList<MenuData> children = data.getChildren();
    if (children != null) {
      int count = 0;
      for (MenuData child : children) {
        if (!child.isVisibleForEditor(sdata))
          continue;
        if (count == 0 && level > 0)
          writer.write("<ul>");
        addNode(child, level + 1, currentId, parentIds, sdata, writer);
        count++;
      }
      if (count > 0 && level > 0)
        writer.write("</ul>");
    }
    if (level > 0) {
      writer.write("</li>");
    }
  }
%>
<% if (sdata.hasAnyPageEditRight()) {%>
<div class="well">
  <div class="menuHeader">
    <a href="/_page?method=openPageSettings&id=<%=rootNode.getId()%>"><%=rootNode.getName()%>
    </a>
  </div>
  <div id="menuDiv">
    <ul id="navigation">
      <%addNode(rootNode, 0, id, parentIds, sdata, out);%>
    </ul>
  </div>
</div>
<%
  }
  if (sdata.hasAnyBackendLinkRight()) {
%>
<div class="well">
  <ul class="nav nav-list">
    <% for (LinkData link : links) {%>
    <li><a href="<%=FormatHelper.toHtml(link.getLink())%>"><%=StringCache.getHtml(link.getLinkKey())%>
    </a></li>
    <%}%>
  </ul>
</div>
<%}%>
<script type="text/javascript">
  $("#navigation").treeview({
    persist: "location",
    collapsed: true,
    unique: true
  });
</script>



