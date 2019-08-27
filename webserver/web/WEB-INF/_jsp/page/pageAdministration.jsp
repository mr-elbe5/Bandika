<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.page.PageCache" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    PageData rootPage = PageCache.getInstance().getRootPage();
%>
<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <% if (rdata.hasAnyContentRight()) { %>
        <ul class="tree pagetree">
            <li class="open">
                <a><%=Strings.html("_pages",locale)%>
                </a>
                <ul>
                    <%
                        if (rootPage != null) {
                            for (PageData pageData : rootPage.getSubPages()) {
                                if (rdata.hasContentRight(pageData.getId(), Right.READ)) {
                                    rdata.put("treePage", pageData);
                    %>
                    <jsp:include page="/WEB-INF/_jsp/page/pageTreePage.inc.jsp" flush="true"/>
                    <%
                                }
                            }
                        }
                    %>
                </ul>
            </li>
        </ul>
        <%}%>
    </section>
</div>
<script type="text/javascript">
    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
    $.each($(".pagedrag"), function () {
        $(this).setDraggable('pagedrag', 'move', '.pagedrag', movePage);
    });

    function movePage(ev) {
        let parentid = ev.originalEvent.currentTarget.dataset.dragid;
        let id = ev.originalEvent.dataTransfer.getData('dragId');
        $.ajax({
            type: 'POST',
            url: '/ctrl/page/movePage/' + id,
            data: {'parentId': parentid},
            dataType: 'html',
            success: function (data) {
                linkTo('/ctrl/page/openPageAdministration/' + parentid);
            }
        });
    }
</script>
    
