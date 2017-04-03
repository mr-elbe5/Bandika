<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.content.MenuCache" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  int id = rdata.getParamInt("id", Statics.getContentHomeId(sdata.getLocale()));
  ArrayList<ContentData> list = MenuCache.getInstance(sdata.getLocale()).getUserTree(sdata.getUserId(), sdata.isEditor());
  ContentData nodeHome = MenuCache.getInstance(sdata.getLocale()).getNode(Statics.getContentHomeId(sdata.getLocale()));
  ContentData node = nodeHome;
  String cls = "";
%>
<script type="text/javascript">
  <% if (list!=null){
	  for (int i=1;i<list.size();i++){
		node=list.get(i);%>
  var item = addMenuItem(<%=node.getId()%>, <%=node.getLevel()-1%>, '<%=Formatter.toJS(node.getName())%>', <%=node.getId()==id ? "true":"false"%>);
  if (item.id ==<%=id%>)
    item.active = true;
  <%}
    cls=Statics.getContentHomeId(sdata.getLocale())==id?"activeLink":"link";
  }%>
</script>
<div class="menuHeader">&nbsp;</div>
<div class="menuContent">
  <div class="m_header"><%=Strings.getHtml("menu", sdata.getLocale())%>
  </div>
  <div id="menuDiv">
  </div>
  <script type="text/javascript">
    initMenuHtml();
  </script>
  <div>&nbsp;</div>
</div>
<div class="menuFooter">&nbsp;</div>
<% if (sdata.isLoggedIn()) {%>
<div class="menuHeader">&nbsp;</div>
<div class="menuContent">
  <div class="m_header"><%=Strings.getHtml("ownData", sdata.getLocale())%>
  </div>
  <div class="small">
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openChangePassword"><%=Strings.getHtml("changePassword", sdata.getLocale())%>
    </a></div>
  </div>
  <div>&nbsp;</div>
</div>
<div class="menuFooter">&nbsp;</div>
<%}%>
<% if (sdata.isEditor() || sdata.isAdmin()) {%>
<div class="menuHeader">&nbsp;</div>
<div class="menuContent">
  <div class="m_header"><%=Strings.getHtml("administration", sdata.getLocale())%>
  </div>
  <div class="small">
    <% if (sdata.isEditor()) {%>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openCreate&parent=<%=id%>"><%=Strings.getHtml("newPage", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openEdit&id=<%=id%>"><%=Strings.getHtml("changePage", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openSortChildren&id=<%=id%>"><%=Strings.getHtml("sortChildPages", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openDelete&id=<%=id%>"><%=Strings.getHtml("deletePage", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=openEditImages"><%=Strings.getHtml("imageAdministration", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openEditDocuments"><%=Strings.getHtml("documentAdministration", sdata.getLocale())%>
    </a></div>
    <%
      }
      if (sdata.isAdmin()) {
    %>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openEditUsers"><%=Strings.getHtml("userAdministration", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openEditGroups"><%=Strings.getHtml("groupAdministration", sdata.getLocale())%>
    </a></div>
    <div><a
        href="srv25?ctrl=<%=Statics.KEY_TEMPLATE%>&method=openEditTemplates"><%=Strings.getHtml("templateAdministration", sdata.getLocale())%>
    </a></div>
    <%}%>
  </div>
  <div>&nbsp;</div>
</div>
<div class="menuFooter">&nbsp;</div>
<%}%>



