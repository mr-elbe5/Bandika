<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileCache" %>
<%@ page import="de.elbe5.cms.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int callbackNum=RequestReader.getInt(request, "CKEditorFuncNum", -1);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._selectImage.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <div class="modal-body">
            <cms:message/>
            <section class="treeSection">
                <% if (SessionReader.hasAnyContentRight(request)) { %>
                <ul class="tree filetree">
                    <%
                        request.setAttribute("folderData",FileCache.getInstance().getRootFolder());
                    %>
                    <jsp:include page="/WEB-INF/_jsp/field/imageBrowserFolder.inc.jsp" flush="true"/>
                </ul>
                <%  request.removeAttribute("folderData");
                }%>
            </section>
        </div>
        <div class="modal-footer">
            <button type="button" class="btn btn-secondary"
                    data-dismiss="modal"><%=Strings._cancel.html(locale)%>
            </button>
        </div>
    </div>
    <script type="text/javascript">
        $('.tree').treed('fa fa-folder-open-o','fa fa-folder-o');

        function ckImgCallback(url) {
            if (CKEDITOR)
                CKEDITOR.tools.callFunction(<%=callbackNum%>,url);
            return closeModalDialog();
        }
    </script>
</div>
