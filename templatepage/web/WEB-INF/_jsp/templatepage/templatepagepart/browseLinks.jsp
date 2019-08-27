<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.file.FileCache" %>
<%@ page import="de.elbe5.page.PageCache" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    int callbackNum = rdata.getInt("CKEditorFuncNum", -1);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_selectLink",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <cms:message/>
            <ul class="nav nav-tabs" id="selectTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true"><%=Strings.html("_pages",locale)%>
                    </a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="files-tab" data-toggle="tab" href="#files" role="tab" aria-controls="files" aria-selected="false"><%=Strings.html("_files",locale)%>
                    </a>
                </li>
            </ul>

            <div class="tab-content" id="pageTabContent">
                <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                    <section class="treeSection">
                        <% if (rdata.hasAnyContentRight()) { %>
                        <ul class="tree filetree">
                            <%rdata.put("treePage", PageCache.getInstance().getHomePage(locale));%>
                            <jsp:include page="/WEB-INF/_jsp/templatepage/templatepagepart/pageLinkBrowserFolder.inc.jsp" flush="true"/>
                        </ul>
                        <%
                                rdata.remove("treePage");
                            }%>
                    </section>
                </div>
                <div class="tab-pane fade" id="files" role="tabpanel" aria-labelledby="files-tab">
                    <section class="treeSection">
                        <% if (rdata.hasAnyContentRight()) { %>
                        <ul class="tree filetree">
                            <%rdata.put("folderData", FileCache.getInstance().getRootFolder());%>
                            <jsp:include page="/WEB-INF/_jsp/templatepage/templatepagepart/fileLinkBrowserFolder.inc.jsp" flush="true"/>
                        </ul>
                        <%
                                rdata.remove("folderData");
                            }%>
                    </section>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_cancel",locale)%>
            </button>
        </div>
    </div>
    <script type="text/javascript">
        $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');

        function ckLinkCallback(url) {
            if (CKEDITOR)
                CKEDITOR.tools.callFunction(<%=callbackNum%>, url);
            return closeModalDialog();
        }
    </script>
</div>
