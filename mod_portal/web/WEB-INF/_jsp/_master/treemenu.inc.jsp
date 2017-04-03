<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.application.AppConfiguration" %>
<%@ page import="de.bandika.page.PageRightsData" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    RequestData rdata = RequestHelper.getRequestData(request);
    int pageId = rdata.getInt("pageId");
    MenuCache mc = MenuCache.getInstance();
    List<Integer> parentIds = mc.getParentIds(pageId);
%>
<%!
    protected static void addNode(MenuData data, int level, int currentId, List<Integer> parentIds, SessionData sdata, JspWriter writer) throws IOException {
        if (level > 0) {
            writer.print("<li");
            if (parentIds.contains(data.getId())) {
                writer.write(" class=\"open\"");
            }
            writer.write("><a ");
            if (data.getId() == currentId)
                writer.write(" class=\"selectedlinks\" ");
            if (data.isEditableForBackendUser(sdata)) {
                writer.write("href=\"/page.srv?act=openPageSettings&");
                writer.write("pageId");
                writer.write("=");
                writer.write(Integer.toString(data.getId()));
                writer.write("\">");
            } else
                writer.write(">");
            writer.write(StringFormat.toHtml(data.getName()));
            writer.write("</a>");
        }
        List<MenuData> children = data.getChildren();
        if (children != null) {
            int count = 0;
            for (MenuData child : children) {
                if (!child.isVisibleForBackendUser(sdata))
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
<% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, PageRightsData.RIGHT_EDIT)) {
  for (Locale loc : AppConfiguration.getInstance().getLocales()){
      MenuData homePage = mc.getHomePage(loc);
%>
<div class="well">
    <div class="menuHeader">
        <a href="/page.srv?act=openPageSettings&pageId=<%=homePage.getId()%>"><%=homePage.getName()%>&nbsp;(<%=loc.getDisplayName()%>)
        </a>
    </div>
    <div id="menuDiv">
        <ul id="navigation">
            <%addNode(homePage, 0, pageId, parentIds, sdata, out);%>
        </ul>
    </div>
</div>
<%  }
  }%>
<script type="text/javascript">
    $("#navigation").treeview({
        persist: "location",
        collapsed: true,
        unique: true
    });
</script>



