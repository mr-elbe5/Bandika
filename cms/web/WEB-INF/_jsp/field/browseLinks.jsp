<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileCache" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.page.PageCache" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int callbackNum=RequestReader.getInt(request, "CKEditorFuncNum", -1);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._selectLink.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <cms:message/>
            <ul class="nav nav-tabs" id="selectTab" role="tablist">
                <li class="nav-item">
                    <a class="nav-link active" id="pages-tab" data-toggle="tab" href="#pages" role="tab" aria-controls="pages" aria-selected="true"><%=Strings._pages.html(locale)%></a>
                </li>
                <li class="nav-item">
                    <a class="nav-link" id="files-tab" data-toggle="tab" href="#files" role="tab" aria-controls="files" aria-selected="false"><%=Strings._files.html(locale)%></a>
                </li>
            </ul>

            <div class="tab-content" id="pageTabContent">
                <div class="tab-pane fade show active" id="pages" role="tabpanel" aria-labelledby="pages-tab">
                    <section class="treeSection">
                        <% if (SessionReader.hasAnyContentRight(request)) { %>
                        <ul class="tree filetree">
                            <%
                                request.setAttribute("pageData", PageCache.getInstance().getHomePage(locale));
                            %>
                            <jsp:include page="/WEB-INF/_jsp/field/pageLinkBrowserFolder.inc.jsp" flush="true"/>
                        </ul>
                        <%  request.removeAttribute("pageData");
                        }%>
                    </section>
                </div>
                <div class="tab-pane fade" id="files" role="tabpanel" aria-labelledby="files-tab">
                    <section class="treeSection">
                        <% if (SessionReader.hasAnyContentRight(request)) { %>
                        <ul class="tree filetree">
                            <%
                                request.setAttribute("folderData",FileCache.getInstance().getRootFolder());
                            %>
                            <jsp:include page="/WEB-INF/_jsp/field/fileLinkBrowserFolder.inc.jsp" flush="true"/>
                        </ul>
                        <%  request.removeAttribute("folderData");
                        }%>
                    </section>
                </div>
            </div>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary"
                    data-dismiss="modal"><%=Strings._cancel.html(locale)%>
            </button>
        </div>
    </div>
    <script type="text/javascript">
        $('.tree').treed('fa fa-folder-open-o','fa fa-folder-o');

        function ckLinkCallback(url) {
            if (CKEDITOR)
                CKEDITOR.tools.callFunction(<%=callbackNum%>,url);
            return closeModalDialog();
        }
    </script>
</div>
