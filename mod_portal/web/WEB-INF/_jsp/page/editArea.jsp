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
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.page.AreaData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PagePartData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    PageData data = (PageData) sdata.get("pageData");
    String areaName = rdata.getString("areaName");
    PagePartData editPagePart = data.getEditPagePart();
    AreaData area = data.getArea(areaName);%>
  <div class="area">
    <div class="areaHeader"><%=StringFormat.toHtml(areaName)%>
    </div>
    <% if (area != null) {
        for (PagePartData pdata : area.getParts()) {
            if (pdata == null)
                continue;
            rdata.put("pagePartData", pdata);
            if (pdata == editPagePart)
                rdata.put("partEditMode", "1");
    %>
    <div class="<%=pdata == editPagePart ? "editPagePart" : "viewPagePart"%>">
        <div class="areatools">
            <div class="btn-group">
                <% if (pdata == editPagePart) {%>
                <button class="btn btn-mini btn-primary" onclick="evaluateEditFields();savePagePart(<%=pdata.getId()%>,'<%=areaName%>');"><%=StringCache.getHtml("webapp_ok", sdata.getLocale())%></button>
                <button class="btn btn-mini" onclick="submitAction('cancelEditPagePart');"><%=StringCache.getHtml("webapp_cancel", sdata.getLocale())%></button>&nbsp;<%=StringFormat.toHtml(pdata.getPartTemplate())%>
                <%} else if (editPagePart==null){%>
                <button  class="btn btn-mini btn-icon" onclick="document.form.partId.value = <%=pdata.getId()%>;$('#selectPart<%=areaName%>').modal();return false;" title="<%=StringCache.getHtml("webapp_new", sdata.getLocale())%>"><i class="icon-plus"></i></button >
                <button  class="btn btn-mini btn-icon" onclick="return linkTo('/page.srv?act=editPagePart&pageId=<%=data.getId()%>&partId=<%=pdata.getId()%>&areaName=<%=areaName%>')" title="<%=StringCache.getHtml("webapp_change", sdata.getLocale())%>"><i class="icon-pencil"></i></button >
                <% if (pdata.isShared()){%>
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("webapp_share", sdata.getLocale())%>" disabled><i class="icon-share"></i></button >
                <%}else{%>
                <button  class="btn btn-mini btn-icon" onclick="document.form.partId.value=<%=pdata.getId()%>;$('#sharePart<%=areaName%>').modal();return false;" title="<%=StringCache.getHtml("webapp_share", sdata.getLocale())%>"><i class="icon-share"></i></button >
                <%}%>
                <button  class="btn btn-mini btn-icon" onclick="return linkTo('/page.srv?act=movePagePart&pageId=<%=data.getId()%>&partId=<%=pdata.getId()%>&areaName=<%=areaName%>&dir=-1')" title="<%=StringCache.getHtml("portal_up", sdata.getLocale())%>"><i class="icon-arrow-up"></i></button >
                <button  class="btn btn-mini btn-icon" onclick="return linkTo('/page.srv?act=movePagePart&pageId=<%=data.getId()%>&partId=<%=pdata.getId()%>&areaName=<%=areaName%>&dir=1')" title="<%=StringCache.getHtml("portal_down", sdata.getLocale())%>"><i class="icon-arrow-down"></i></button >
                <button  class="btn btn-mini btn-icon" onclick="return linkTo('/page.srv?act=deletePagePart&pageId=<%=data.getId()%>&partId=<%=pdata.getId()%>&areaName=<%=areaName%>')" title="<%=StringCache.getHtml("webapp_delete", sdata.getLocale())%>"><i class="icon-remove"></i></button >
                <%} else{%>
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("webapp_new", sdata.getLocale())%>" disabled><i class="icon-plus"></i></button >
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("webapp_change", sdata.getLocale())%>" disabled><i class="icon-pencil"></i></button >
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("webapp_share", sdata.getLocale())%>" disabled><i class="icon-share"></i></button >
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("portal_up", sdata.getLocale())%>" disabled><i class="icon-arrow-up"></i></button >
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("portal_down", sdata.getLocale())%>" disabled><i class="icon-arrow-down"></i></button >
                <button  class="btn btn-mini btn-icon disabled" title="<%=StringCache.getHtml("webapp_delete", sdata.getLocale())%>" disabled><i class="icon-remove"></i></button >
                <%}%>
            </div>
        </div>
        <jsp:include page="<%=pdata.getPartTemplateUrl()%>" flush="true"/>
    </div>
    <%
                rdata.remove("pagePartData");
                rdata.remove("partEditMode");
            }
        }
    %>
    <div>
        <div class="areatools">
            <div class="btn-group">
                <%if (editPagePart == null) {%>
                <button  class="btn btn-mini" onclick="document.form.partId.value = -1;$('#selectPart<%=areaName%>').modal();return false;" title="<%=StringCache.getHtml("webapp_new", sdata.getLocale())%>"><i class="icon-plus"></i></button >
                <%}else{%>
                <button  class="btn btn-mini disabled" title="<%=StringCache.getHtml("webapp_new", sdata.getLocale())%>" disabled><i class="icon-plus"></i></button >
                <%}%>
            </div>
        </div>
    </div>
</div>

<div id="selectPart<%=areaName%>" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("portal_selectTemplate", locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <legend><%=StringCache.getHtml("portal_selectTemplate", locale)%>
        </legend>
    </div>
    <div class="modal-body"></div>
</div>

<div id="sharePart<%=areaName%>" class="modal hide iframeLayer" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("webapp_share", locale)%>" aria-hidden="true">
    <div class="modal-header">
        <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
        <legend><%=StringCache.getHtml("webapp_share", locale)%>
        </legend>
    </div>
    <div class="modal-body">
        <iframe src="/blank.jsp" class="layerIframe" scrolling="no"></iframe>
    </div>
</div>

<script type="text/javascript">

    $(function () {
        $("#selectPart<%=areaName%>").modal({
            show: false,
            backdrop: "static"
        });
    });

    $(function () {
        $("#sharePart<%=areaName%>").modal({
            show: false,
            backdrop: "static"
        });
    });

    var $selector=$('#selectPart<%=areaName%>');
    $selector.on('show', function () {
        var partId = document.form.partId.value;
        var $modalBody=$(this).find($(".modal-body"));
        if (!$modalBody.html())
            $modalBody.load('/page.srv?act=openAddPagePart&pageId=<%=data.getId()%>&areaName=<%=areaName%>&partId=' + partId);
    });
    $selector.on('hide', function () {
        $(this).find(".modal-body").html('');
    });

    $selector=$('#editPartRights<%=areaName%>');
    $selector.on('show', function () {
        var partId = document.form.partId.value;
        $(this).find('iframe').attr('src', '/page.srv?act=openEditPagePartRights&pageId=<%=data.getId()%>&areaName=<%=areaName%>&partId=' + partId);
    });
    $selector.on('hide', function () {
        $(this).find('iframe').attr('src', '/blank.jsp');
    });

    $selector=$('#sharePart<%=areaName%>');
    $selector.on('show', function () {
        var partId = document.form.partId.value;
        $(this).find('iframe').attr('src', '/page.srv?act=openSharePagePart&pageId=<%=data.getId()%>&areaName=<%=areaName%>&partId=' + partId);
    });
    $selector.on('hide', function () {
        $(this).find('iframe').attr('src', '/blank.jsp');
    });
</script>