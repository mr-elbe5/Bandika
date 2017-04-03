<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%Statics.setDocAndContentType(out, response);%>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.base.controller.PageResponse" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.base.RequestError" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.content.ContentData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.net25.content.MenuCache" %>
<%
  Statics.setNoCache(response);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  int id = rdata.getParamInt("id", Statics.getContentHomeId(sdata.getLocale()));
  PageResponse respdata = (PageResponse) request.getAttribute(StdServlet.RESPONSE_DATA);
  String jsp = respdata.getJspInclude();
  String title = Strings.getString("title", sdata.getLocale());
  String pageTitle;
  if (respdata.getTitle() != null) {
    title += " - " + respdata.getTitle();
    pageTitle = respdata.getTitle();
  } else
    pageTitle = " ";
  String keywords = respdata.getKeywords();
  RequestError error = (RequestError) rdata.getParam("error");
  ArrayList<ContentData> breadcrumbList = MenuCache.getInstance(sdata.getLocale()).getBreadcrumbList(id);
  ContentData node;
  ArrayList<ArrayList<ContentData>> lists = MenuCache.getInstance(sdata.getLocale()).getUserTopTrees(sdata.getUserId(), sdata.isEditor());
%>
<html <%=Statics.HTML_TYPE%>>
<head>
  <%=Statics.HTML_HEADERS%>
  <title><%=title%>
  </title>
  <meta name="keywords" content="<%=keywords%>">
  <meta name="robots" content="index,follow"/>
  <link rel="stylesheet" href="<%=Statics.STYLE_PATH%>std.css">
  <script src="<%=Statics.JS_PATH%>std.js" type="text/javascript"></script>
</head>
<body>
<div class="wrapper">
  <table class="maintable" cellspacing="0" cellpadding="0" border="0">
    <tr>
      <td class="leftmaintd">&nbsp;</td>
      <td class="centralmaintd">
        <div class="logoheader"><img src="<%=Statics.IMG_PATH%>header.jpg" alt=""></div>
        <table cellpadding="0" cellspacing="0" border="0" class="systemmenutable">
          <tr>
            <td class="usermenutd">
              <% if (sdata.isLoggedIn()) {%>
              <div class="usermenu">
                <a href="#" onmouseover="showLayer('userlayer')"
                   onmouseout="hideLayerWait('userlayer',50)"><%=Formatter.toHtml(sdata.getUserName())%>
                </a>

                <div class="userlayer" id="userlayer" onmouseover="showLayer('userlayer')"
                     onmouseout="hideLayerWait('userlayer',50)">
                  <div class="userlayerlinks">
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openChangePassword"><%=Strings.getHtml("changePassword", sdata.getLocale())%>
                      </a>
                    </div>
                  </div>
                </div>
              </div>
              <%} else {%>
              &nbsp;
              <%}%>
            </td>
            <td class="systemmenutd">
              <div class="systemmenu">
                <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>"><%=Strings.getHtml("home", sdata.getLocale())%>
                </a>&nbsp;|&nbsp;
                <% if (Statics.LOCALES.length > 1) {
                  for (int i = 0; i < Statics.LOCALES.length; i++) {
                    if (Statics.LOCALES[i].equals(sdata.getLocale()))
                      continue;
                %>
                <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=changeLanguage&language=<%=Statics.LANGUAGES[i]%>"><%=Strings.getHtml(Statics.LANGUAGES[i], sdata.getLocale())%>
                </a>&nbsp;|&nbsp;
                <%
                    }
                  }
                %>
                <% if (!sdata.isLoggedIn()) {%>
                <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openLogin"><%=Strings.getHtml("login", sdata.getLocale())%>
                </a>
                <%} else {%>
                <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=logout"><%=Strings.getHtml("logout", sdata.getLocale())%>
                </a>
                <%}%>
              </div>
            </td>
            <td class="adminmenutd">
              <% if (sdata.isEditor() || sdata.isAdmin()) {%>
              <div class="adminmenu"><a href="#" onmouseover="showLayer('adminlayer')"
                                        onmouseout="hideLayerWait('adminlayer',50)"><%=Strings.getHtml("administration", sdata.getLocale())%>
              </a>

                <div class="adminlayer" id="adminlayer" onmouseover="showLayer('adminlayer')"
                     onmouseout="hideLayerWait('adminlayer',50)">
                  <div class="adminlayerlinks">
                    <% if (sdata.isEditor()) {%>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openCreate&parent=<%=id%>"><%=Strings.getHtml("newPage", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openEdit&id=<%=id%>"><%=Strings.getHtml("changePage", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openSortChildren&id=<%=id%>"><%=Strings.getHtml("sortChildPages", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=openDelete&id=<%=id%>"><%=Strings.getHtml("deletePage", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_IMAGE%>&method=openEditImages"><%=Strings.getHtml("imageAdministration", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_DOCUMENT%>&method=openEditDocuments"><%=Strings.getHtml("documentAdministration", sdata.getLocale())%>
                      </a></div>
                    <%
                      }
                      if (sdata.isAdmin()) {
                    %>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openEditUsers"><%=Strings.getHtml("userAdministration", sdata.getLocale())%>
                      </a></div>
                    <div>
                      <a href="srv25?ctrl=<%=Statics.KEY_USER%>&method=openEditGroups"><%=Strings.getHtml("groupAdministration", sdata.getLocale())%>
                      </a></div>
                    <div><a
                        href="srv25?ctrl=<%=Statics.KEY_TEMPLATE%>&method=openEditTemplates"><%=Strings.getHtml("templateAdministration", sdata.getLocale())%>
                    </a></div>
                    <div><a
                        href="srv25?ctrl=<%=Statics.KEY_CACHE%>&method=openEditCaches"><%=Strings.getHtml("cacheAdministration", sdata.getLocale())%>
                    </a></div>
                    <%}%>
                  </div>
                </div>
              </div>
              <%} else {%>
              &nbsp;
              <%}%>
            </td>
          </tr>
        </table>
        <div class="centralmainhline">&nbsp;</div>
        <div class="mainmenu">
          <table cellpadding="0" cellspacing="0" border="0">
            <tr>
              <%
                for (int i = 0; i < lists.size(); i++) {
                  ArrayList<ContentData> list = lists.get(i);
                  node = list.get(0);%>
              <td<% if (i < lists.size() - 1) {%> class="borderright"<%}%>>
                <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=node.getId()%>"
                    <%if (list.size() > 1) {%>
                   onmouseover="showLayer('menulayer<%=i%>')"
                   onmouseout="hideLayerWait('menulayer<%=i%>',50)"<%}%>><%=Formatter.toHtml(node.getName())%>
                </a>
                <%if (list.size() > 1) {%>
                <div class="menulayer" id="menulayer<%=i%>" onmouseover="showLayer('menulayer<%=i%>')"
                     onmouseout="hideLayerWait('menulayer<%=i%>',50)">
                  <div class="menulayerlinks">
                    <%
                      for (int j = 1; j < list.size(); j++) {
                        node = list.get(j);
                    %>
                    <div class="menulink<%=node.getLevel()%>"><a
                        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=node.getId()%>"><%=Formatter.toHtml(node.getName())%>
                    </a></div>
                    <%}%>
                  </div>
                </div>
                <%}%>
              </td>
              <%}%>
            </tr>
          </table>
        </div>
        <div class="centralmainhline">&nbsp;</div>
        <div class="breadcrumb">
          <% for (int i = 1; i < breadcrumbList.size(); i++) {
            node = breadcrumbList.get(i);
            if (i > 1) {%>
          &nbsp;&gt;&nbsp;
          <%}%>
          <a href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=node.getId()%>"><%=Formatter.toHtml(node.getName())%>
          </a>
          <%}%>&nbsp;
        </div>
        <h1 class="topHeader">
          <%=Formatter.toHtml(pageTitle)%>
        </h1>
        <% if (error != null) {%>
        <div class="toperror"><%=Formatter.toHtml(error.getErrorString(sdata))%>
        </div>
        <%}%>
        <div class="content">
          <jsp:include page="<%=jsp%>" flush="true"/>
        </div>
        <div class="spacer20">&nbsp;</div>
        <div class="centralmainhline">&nbsp;</div>
        <div class="footer"><a
            href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=Statics.getContentImprintId(sdata.getLocale())%>"><%=Strings.getHtml("imprint", sdata.getLocale())%>
        </a> | <a
            href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=Statics.getContentContactId(sdata.getLocale())%>"><%=Strings.getHtml("contact", sdata.getLocale())%>
        </a>
        </div>
      </td>
      <td class="rightmaintd">&nbsp;</td>
    </tr>
  </table>
</div>
</body>
</html>
