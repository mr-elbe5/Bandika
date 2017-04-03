<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.page.PageBean" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    int pageId = rdata.getInt("pageId");
    PageData data = (PageData) rdata.get("pageData");
    MenuData parentPage = null;
    if (data.getParentId() != 0) {
        parentPage = MenuCache.getInstance().getNode(data.getParentId());
    }
    int version = MenuCache.getInstance().getNodeVersionForUser(data.getId(), sdata);
    List<Integer> usages = PageBean.getInstance().getPageUsages(data.getId());
    rdata.put("adminLayer", "1");
%>
<div class="well">
    <legend><%=StringCache.getHtml("portal_pageActions",locale)%>
    </legend>
    <div class="btn-toolbar">
        <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#"
               href="#"><%=StringCache.getHtml("portal_page",locale)%>
                &nbsp;<span class="caret"> </span></a>
            <ul class="dropdown-menu">
                <li>
                    <a href="/page.srv?act=openEditPageSettings&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_editSettings",locale)%>
                    </a></li>
                <li><a href="#" onclick="$('#changeLayout').modal();"><%=StringCache.getHtml("portal_changeLayout",locale)%>
                </a></li>
                <li><a href="#" onclick="$('#changeMaster').modal();"><%=StringCache.getHtml("portal_changeMaster",locale)%>
                </a></li>
                <li>
                    <a href="/page.srv?act=publishPage&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_publish",locale)%>
                    </a></li>
                <% if (!MenuCache.getInstance().isHomePageId(data.getId())) {%>
                <li><a href="/page.srv?act=cutPage&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_cutPage",locale)%>
                </a></li>
                <li>
                    <a href="/page.srv?act=openDelete&pageId=<%=data.getId()%>"><%=StringCache.getHtml("webapp_delete",locale)%>
                    </a></li>
                <%}%>
                <li>
                    <a href="/page.srv?act=openPageHistory&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_previousVersions",locale)%>
                    </a></li>
            </ul>
        </div>
        <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#"
               href="#"><%=StringCache.getHtml("portal_content",locale)%>
                &nbsp;<span class="caret"> </span></a>
            <ul class="dropdown-menu">
                <li><a href="/page.srv?act=show&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_viewPage",locale)%>
                </a></li>
                <li>
                    <a href="/page.srv?act=openEditPageContentFromPageSettings&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_editPage",locale)%>
                    </a></li>
            </ul>
        </div>
        <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#"
               href="#"><%=StringCache.getHtml("portal_childPages",locale)%>&nbsp;<span class="caret"> </span></a>
            <ul class="dropdown-menu">
                <li><a href="#" onclick="$('#selectLayout').modal();"><%=StringCache.getHtml("portal_newPage",locale)%>
                </a></li>
                <% if (sdata.getInt("cutPageId", 0) > 0) {%>
                <li>
                    <a href="/page.srv?act=pastePage&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_pastePage",locale)%>
                    </a></li>
                <%}%>
                <% if (data.getNumChildren() > 1) {%>
                <li>
                    <a href="/page.srv?act=openSortChildren&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_sortChildPages",locale)%>
                    </a></li>
                <%}%>
            </ul>
        </div>
        <div class="btn-group">
            <a class="btn dropdown-toggle" data-toggle="dropdown" data-target="#"
               href="#"><%=StringCache.getHtml("portal_files",locale)%>
                &nbsp;<span class="caret"> </span></a>
            <ul class="dropdown-menu">
                <li>
                    <a href="/document.srv?act=openEditPageDocuments&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_documents",locale)%>
                    </a></li>
                <li>
                    <a href="/image.srv?act=openEditPageImages&pageId=<%=data.getId()%>"><%=StringCache.getHtml("portal_images",locale)%>
                    </a></li>
            </ul>
        </div>
    </div>
    <script type="text/javascript">
        $('.dropdown-toggle').dropdown();
    </script>
</div>
<div class="well">
    <legend><%=StringCache.getHtml("portal_currentSettings",locale)%>
    </legend>
    <form class="form-horizontal" action="" class="form-horizontal">
        <bandika:controlGroup labelKey="portal_pageid" padded="true"><%=Integer.toString(data.getId())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_publishedVersion"
                              padded="true"><%=data.getPublishedVersion() != 0 ? Integer.toString(data.getPublishedVersion()) : "-"%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_draftVersion"
                              padded="true"><%=data.getDraftVersion() != 0 ? Integer.toString(data.getDraftVersion()) : "-"%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_currentVersion" padded="true"><%=Integer.toString(version)%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_parentPage"
                              padded="true"><%=(parentPage == null) ? "none" : StringFormat.toHtml(parentPage.getName()) + "&nbsp;(" + parentPage.getId() + ")"%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_author" padded="true"><%=StringFormat.toHtml(data.getAuthorName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_changeDate"
                              padded="true"><%=StringFormat.toHtmlDateTime(data.getChangeDate(),locale)%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_name" padded="true"><%=StringFormat.toHtml(data.getName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_path" padded="true"><%=StringFormat.toHtml(data.getPath())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_description"
                              padded="true"><%=StringFormat.toHtml(data.getDescription())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_metaKeywords" padded="true"><%=StringFormat.toHtml(data.getKeywords())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_masterTemplate"
                              padded="true"><%=StringFormat.toHtml(data.getMasterTemplate())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_layoutTemplate"
                              padded="true"><%=StringFormat.toHtml(data.getLayoutTemplate())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_restricted" padded="true"><%=Boolean.toString(data.isRestricted())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_inheritsRights"
                              padded="true"><%=Boolean.toString(data.inheritsRights())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_visible" padded="true"><%=Boolean.toString(data.isVisible())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_usages" padded="true"><%=StringFormat.getIntString(usages)%>
        </bandika:controlGroup>
    </form>
</div>

<div id="changeLayout" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("portal_selectTemplate",locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <legend><%=StringCache.getHtml("portal_selectTemplate",locale)%>
        </legend>
    </div>
    <div class="modal-body">
    </div>
</div>
<div id="changeMaster" class="modal hide" tabindex="-1" role="dialog"
     aria-labelledby="<%=StringCache.getHtml("portal_selectTemplate",locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
        <legend><%=StringCache.getHtml("portal_selectTemplate",locale)%>
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
        $("#changeLayout").find($(".modal-body")).load("/page.srv?act=openChangeLayout&pageId=<%=pageId%>");
    });
    $(function () {
        $("#changeMaster").modal({
            show: false,
            backdrop: "static"
        });
    });
    $('#changeMaster').on('show', function () {
        $("#changeMaster").find($(".modal-body")).load("/page.srv?act=openChangeMaster&pageId=<%=pageId%>");
    });
</script>

<jsp:include page="/WEB-INF/_jsp/page/createPageLayer.inc.jsp"/>

