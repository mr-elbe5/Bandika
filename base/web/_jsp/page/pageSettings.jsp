<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  int id = rdata.getCurrentPageId();
  PageData data = (PageData) rdata.getParam("pageData");
  MenuData parentPage = null;
  if (data.getParentId() != 0) {
    parentPage = MenuCache.getInstance().getNode(data.getParentId());
  }
  int version = MenuCache.getInstance().getNodeVersionForUser(data.getId(), sdata);
  ArrayList<Integer> usages = PageBean.getInstance().getPageUsages(data.getId());
  rdata.setParam("adminLayer", "1");
%>
<div class="well">
  <legend><%=StringCache.getHtml("pageActions")%>
  </legend>
  <div class="btn-toolbar">
    <div class="btn-group">
      <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#" href="#"><%=StringCache.getHtml("page")%>
        &nbsp;<span class="caret"> </span></a>
      <ul class="dropdown-menu">
        <li><a href="/_page?method=openEditPageSettings&id=<%=data.getId()%>"><%=StringCache.getHtml("editSettings")%>
        </a></li>
        <li><a href="#" onclick="$('#changeLayout').modal();"><%=StringCache.getHtml("changeLayout")%>
        </a></li>
        <li><a href="#" onclick="$('#changeMaster').modal();"><%=StringCache.getHtml("changeMaster")%>
        </a></li>
        <li><a href="/_page?method=publishPage&id=<%=data.getId()%>"><%=StringCache.getHtml("publish")%>
        </a></li>
        <% if (data.getId() != RequestData.ROOT_PAGE_ID) {%>
        <li><a href="/_page?method=cutPage&id=<%=data.getId()%>"><%=StringCache.getHtml("cutPage")%>
        </a></li>
        <li><a href="/_page?method=openDelete&id=<%=data.getId()%>"><%=StringCache.getHtml("delete")%>
        </a></li>
        <%}%>
        <li><a href="/_page?method=openPageHistory&id=<%=data.getId()%>"><%=StringCache.getHtml("previousVersions")%>
        </a></li>
      </ul>
    </div>
    <div class="btn-group">
      <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#" href="#"><%=StringCache.getHtml("content")%>
        &nbsp;<span class="caret"> </span></a>
      <ul class="dropdown-menu">
        <li><a href="/_page?method=show&id=<%=data.getId()%>"><%=StringCache.getHtml("viewPage")%>
        </a></li>
        <li><a href="/_page?method=openEditPageContent&id=<%=data.getId()%>"><%=StringCache.getHtml("editPage")%>
        </a></li>
      </ul>
    </div>
    <div class="btn-group">
      <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#"
         href="#"><%=StringCache.getHtml("childPages")%>&nbsp;<span class="caret"> </span></a>
      <ul class="dropdown-menu">
        <li><a href="#" onclick="$('#selectLayout').modal();"><%=StringCache.getHtml("newPage")%>
        </a></li>
        <% if (sdata.getParamInt("cutPageId", 0) > 0) {%>
        <li><a href="/_page?method=pastePage&id=<%=data.getId()%>"><%=StringCache.getHtml("pastePage")%>
        </a></li>
        <%}%>
        <% if (data.getNumChildren() > 1) {%>
        <li><a href="/_page?method=openSortChildren&id=<%=data.getId()%>"><%=StringCache.getHtml("sortChildPages")%>
        </a></li>
        <%}%>
      </ul>
    </div>
    <div class="btn-group">
      <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#" href="#"><%=StringCache.getHtml("files")%>
        &nbsp;<span class="caret"> </span></a>
      <ul class="dropdown-menu">
        <li><a
          href="/_file?method=openEditPageFiles&id=<%=data.getId()%>&type=document"><%=StringCache.getHtml("documents")%>
        </a></li>
        <li><a href="/_file?method=openEditPageFiles&id=<%=data.getId()%>&type=image"><%=StringCache.getHtml("images")%>
        </a></li>
      </ul>
    </div>
  </div>
  <script type="text/javascript">
    $('.dropdown-toggle').dropdown();
  </script>
</div>
<div class="well">
  <legend><%=StringCache.getHtml("currentSettings")%>
  </legend>
  <form class="form-horizontal" action="" class="form-horizontal">
    <bandika:controlGroup labelKey="id" padded="true"><%=Integer.toString(data.getId())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="publishedVersion"
                          padded="true"><%=data.getPublishedVersion() != 0 ? Integer.toString(data.getPublishedVersion()) : "-"%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="draftVersion"
                          padded="true"><%=data.getDraftVersion() != 0 ? Integer.toString(data.getDraftVersion()) : "-"%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="currentVersion" padded="true"><%=Integer.toString(version)%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="parentPage"
                          padded="true"><%=(parentPage == null) ? "none" : FormatHelper.toHtml(parentPage.getName()) + "&nbsp;(" + parentPage.getId() + ")"%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="author" padded="true"><%=FormatHelper.toHtml(data.getAuthorName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="changeDate" padded="true"><%=FormatHelper.toHtmlDateTime(data.getChangeDate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="name" padded="true"><%=FormatHelper.toHtml(data.getName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="path" padded="true"><%=FormatHelper.toHtml(data.getPath())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="redirectId" padded="true"><%=Integer.toString(data.getRedirectId())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="description" padded="true"><%=FormatHelper.toHtml(data.getDescription())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="metaKeywords" padded="true"><%=FormatHelper.toHtml(data.getKeywords())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="masterTemplate" padded="true"><%=FormatHelper.toHtml(data.getMasterTemplate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="layoutTemplate" padded="true"><%=FormatHelper.toHtml(data.getLayoutTemplate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="locale" padded="true"><%=data.getLanguage()%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="inheritsLocale" padded="true"><%=Boolean.toString(data.inheritsLocale())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="restricted" padded="true"><%=Boolean.toString(data.isRestricted())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="inheritsRights" padded="true"><%=Boolean.toString(data.inheritsRights())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="visible" padded="true"><%=Boolean.toString(data.isVisible())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="usages" padded="true"><%=StringHelper.getIntString(usages)%>
    </bandika:controlGroup>
  </form>
</div>

<div id="changeLayout" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("selectTemplate")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("selectTemplate")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>
<div id="changeMaster" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("selectTemplate")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("selectTemplate")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $("#changeLayout").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#changeLayout').on('show', function () {
    $("#changeLayout").find($(".modal-body")).load("/_page?method=openChangeLayout&id=<%=id%>");
  });
  $(function () {
    $("#changeMaster").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#changeMaster').on('show', function () {
    $("#changeMaster").find($(".modal-body")).load("/_page?method=openChangeMaster&id=<%=id%>");
  });
</script>

<jsp:include page="/_jsp/page/createPageLayer.inc.jsp"/>

