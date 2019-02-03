<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    FolderData folderData = (FolderData) SessionReader.getSessionObject(request, "folderData");
    assert(folderData !=null);
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._addFiles.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/file.ajx" name="fileform" act="<%=FileActions.dropFiles%>" multi="true">
            <div class="modal-body">
                <cms:message/>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(folderData.getId())%></cms:line>
                <cms:line label="<%=Strings._name.toString()%>"><%=StringUtil.toHtml(folderData.getName())%></cms:line>
                <div class="form-group row">
                    <label class="col-md-3 col-form-label"><%=Strings._dragFilesHere.html(locale)%></label>
                    <div id="dropArea" class="col-md-9"></div>
                </div>
                <div class="form-group row">
                    <label class="col-md-3 col-form-label"><%=Strings._files.html(locale)%></label>
                    <div id="fileNames" class="col-md-9"></div>
                </div>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
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

    var $dropArea=$('#dropArea');
    var dropFiles = [];

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
            var files = e.originalEvent.dataTransfer.files;
            for (var i = 0; i < files.length; i++) {
                dropFiles.push(files[i]);
            }
            var fileNames="";
            dropFiles.forEach(function(value, index, array){
                fileNames+=value.name+'<br>';
            });
            $('#fileNames').html(fileNames);
        }
    });

    $('#fileform').submit(function (event) {
        event.preventDefault();
        var fd = new FormData();
        fd.append('act', 'dropFiles');
        fd.append('folderId', <%=Integer.valueOf(folderData.getId())%>);
        fd.append('numFiles', dropFiles.length.toString());
        for (i = 0; i < dropFiles.length; i++) {
            var file = dropFiles[i];
            fd.append('file_' + i, file);
        }
        $.ajax({
            type: 'POST',
            url: '/file.srv?',
            data: fd,
            processData: false,
            contentType: false,
            success: function (data) {
                linkTo('admin.srv?act=<%=AdminActions.openFileStructure%>&folderId=' + <%=folderData.getId()%>);
            }
        });
    });


</script>


