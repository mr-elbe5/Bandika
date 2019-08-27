<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.file.FolderData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    FolderData folderData = (FolderData) rdata.getSessionObject("folderData");
    assert (folderData != null);
    String url = "/ctrl/file/dropFiles/" + folderData.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_addFiles",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="fileform" multi="true">
            <div class="modal-body">
                <cms:formerror/>
                <cms:line label="_id"><%=Integer.toString(folderData.getId())%>
                </cms:line>
                <cms:line label="_name"><%=StringUtil.toHtml(folderData.getName())%>
                </cms:line>
                <div class="form-group row">
                    <label class="col-md-3 col-form-label"><%=Strings.html("_dragFilesHere",locale)%>
                    </label>
                    <div class="col-md-9">
                        <div id="dropArea" class="form-control"></div>
                    </div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 col-form-label"><%=Strings.html("_files",locale)%>
                    </label>
                    <div id="fileNames" class="col-md-9"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings.html("_save",locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>
<script type="text/javascript">

    function isFileDrag(e) {
        return e.originalEvent &&
            e.originalEvent.dataTransfer &&
            e.originalEvent.dataTransfer.types &&
            e.originalEvent.dataTransfer.types[1] === 'Files';
    }

    function isFileDrop(e) {
        return e.originalEvent &&
            e.originalEvent.dataTransfer &&
            e.originalEvent.dataTransfer.files &&
            e.originalEvent.dataTransfer.files.length > 0;
    }

    let $dropArea = $('#dropArea');
    let dropFiles = [];

    $dropArea.on('dragenter', function (e) {
        e.preventDefault();
        if (isFileDrag(e))
            $dropArea.addClass('active');
    });

    $dropArea.on('dragover', function (e) {
        e.preventDefault();
    });

    $dropArea.on('dragleave', function (e) {
        e.preventDefault();
        if (isFileDrag())
            $dropArea.removeClass('active');
    });

    $dropArea.on('drop dragend', function (e) {
        e.preventDefault();
        if (isFileDrop(e)) {
            let files = e.originalEvent.dataTransfer.files;
            for (let i = 0; i < files.length; i++) {
                dropFiles.push(files[i]);
            }
            let fileNames = "";
            dropFiles.forEach(function (value, index, array) {
                fileNames += value.name + '<br>';
            });
            $('#fileNames').html(fileNames);
        }
    });

    $('#fileform').submit(function (event) {
        event.preventDefault();
        let fd = new FormData();
        fd.append('numFiles', dropFiles.length.toString());
        for (let i = 0; i < dropFiles.length; i++) {
            let file = dropFiles[i];
            fd.append('file_' + i, file);
        }
        $.ajax({
            type: 'POST',
            url: '/ctrl/file/dropFiles/<%=folderData.getId()%>',
            data: fd,
            processData: false,
            contentType: false,
            success: function (data) {
                linkTo('/ctrl/admin/openFileStructure?folderId=' + <%=folderData.getId()%>);
            }
        });
    });

</script>

