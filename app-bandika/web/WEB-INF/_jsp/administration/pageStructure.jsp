<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.cms.page.PageData" %>
<%@ page import="de.elbe5.cms.page.PageCache" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.page.PageActions" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int pageId = RequestReader.getInt(request, PageActions.KEY_PAGE_ID);
    if (pageId == 0)
        pageId = PageCache.getInstance().getHomePageId(locale);
    List<Integer> activeIds = PageCache.getInstance().getParentPageIds(pageId);
    activeIds.add(0,pageId);
    request.setAttribute("activeIds", activeIds);
    PageData rootPage = PageCache.getInstance().getRootPage();
%>
        <div id="pageContent">
            <cms:message/>
            <section class="treeSection">
                <% if (SessionReader.hasAnyContentRight(request)) { %>
                <ul class="tree pagetree">
                    <li class="open">
                        <a><%=Strings._pages.html(locale)%>
                        </a>
                        <ul>
                            <%
                                if (rootPage != null) {
                                    for (PageData pageData : rootPage.getSubPages()) {
                                        if (SessionReader.hasContentRight(request, pageData.getId(), Right.READ)) {
                                            request.setAttribute("pageData", pageData);%>
                            <jsp:include page="/WEB-INF/_jsp/page/treepage.inc.jsp" flush="true"/>
                            <%
                                        }
                                    }
                                }
                            %>
                        </ul>
                    </li>
                </ul>
                <%
                    }
                %>
            </section>
        </div>
        <script type="text/javascript">
            $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
            $.each($(".pagedrag"), function () {
                $(this).setDraggable('pagedrag', 'move', '.pagedrag', movePage);
            });

            function movePage(ev) {
                var fd = new FormData();
                var parentid = ev.originalEvent.currentTarget.dataset.dragid;
                fd.append('act', 'movePage');
                fd.append('pageId', ev.originalEvent.dataTransfer.getData('dragId'));
                fd.append('parentId', parentid);
                $.ajax({
                    type: 'POST',
                    url: '/page.srv?',
                    data: fd,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        linkTo('admin.srv?act=<%=AdminActions.openPageStructure%>&pageId=' + parentid);
                    }
                });
            }
        </script>
    
