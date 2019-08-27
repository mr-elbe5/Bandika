<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="de.elbe5.file.FolderData" %>
<%@ page import="de.elbe5.file.FileCache" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    FolderData folder;
    folder = FileCache.getInstance().getRootFolder();
%>
<div id="pageContent">
    <cms:message/>
    <section class="treeSection">
        <% if (rdata.hasAnyContentRight()) { %>
        <ul class="tree filetree">
            <%
                if (folder != null) {
                    rdata.put("folderData", folder);
            %>
            <jsp:include page="/WEB-INF/_jsp/file/fileTreeFolder.inc.jsp" flush="true"/>
            <%}%>
        </ul>
        <%}%>
    </section>
</div>
<script type="text/javascript">

    let $folderDrag = $(".folderdrag");
    $.each($folderDrag, function () {
        $(this).setDraggable('folderdrag', 'move', '.folderdrag', moveFolder);
    });
    $.each($(".filedrag"), function () {
        $(this).setDraggable('filedrag', 'move', '.folderdrag', moveFile);
    });
    $.each($folderDrag, function () {
        let $dropArea = $(this);
        $dropArea.on('dragenter', function (e) {
            e.preventDefault();
        });
        $dropArea.on('dragover', function (e) {
            e.preventDefault();
        });
        $dropArea.on('dragleave', function (e) {
            e.preventDefault();
        });
    });

    function moveFolder(ev) {
        let parentid = ev.originalEvent.currentTarget.dataset.dragid;
        let id = ev.originalEvent.dataTransfer.getData('dragId');
        $.ajax({
            type: 'POST',
            url: '/ctrl/file/moveFolder/' + id,
            data: {'parentId': parentid},
            dataType: 'html',
            success: function (data) {
                linkTo('/ctrl/admin/openFileStructure?folderId=' + parentid);
            }
        });
    }

    function moveFile(ev) {
        let folderid = ev.originalEvent.currentTarget.dataset.dragid;
        let id = ev.originalEvent.dataTransfer.getData('dragId');
        $.ajax({
            type: 'POST',
            url: '/ctrl/file/moveFile/' + id,
            data: {'folderId': folderid},
            dataType: 'html',
            success: function (data) {
                linkTo('/ctrl/admin/openFileStructure?folderId=' + folderid);
            }
        });
    }

    function dropFiles(ev) {
        console.log('dropped');
        if (isFileDrop(ev)) {
            let folderid = ev.originalEvent.currentTarget.dataset.dragid;
            let files = ev.originalEvent.dataTransfer.files;
            let fd = new FormData();
            fd.append('folderId', folderid);
            fd.append('numFiles', files.length.toString());
            for (let i = 0; i < files.length; i++) {
                let file = files[i];
                fd.append('file_' + i, file);
            }
            $.ajax({
                type: 'POST',
                url: '/ctrl/file/addFiles',
                data: fd,
                processData: false,
                contentType: false,
                success: function (data) {
                    linkTo('/ctrl/admin/openFileStructure?folderId=' + folderid);
                }
            });
        }
    }

    $('.tree').treed('fa fa-minus-square-o', 'fa fa-plus-square-o');
</script>

