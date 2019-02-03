<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%><%response.setContentType("text/html;charset=UTF-8");%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.application.AdminActions" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    FolderData folder = FolderData.getRequestedFolder(request);
    int folderId = folder == null ? 0 : folder.getId();
    List<Integer> activeIds = (folder == null) ? new ArrayList<Integer>() : folder.getParentIds();
    activeIds.add(folderId);
    request.setAttribute("activeIds",activeIds);
%>
    <script type="text/javascript">
        var $folderDrag=$(".folderdrag");
        $.each($folderDrag, function () {
            $(this).setDraggable('folderdrag','move','.folderdrag',moveFolder);
        });
        $.each($(".filedrag"), function () {
            $(this).setDraggable('filedrag','move','.folderdrag',moveFile);
        });
        $.each($folderDrag, function () {
            $(this).setDropArea(dropFiles);
        });
        function moveFolder(ev) {
            var fd = new FormData();
            var parentid=ev.originalEvent.currentTarget.dataset.dragid;
            fd.append('act', 'moveFolder');
            fd.append('folderId', ev.originalEvent.dataTransfer.getData('dragId'));
            fd.append('parentId', parentid);
            $.ajax({
                type: 'POST',
                url: '/file.srv?',
                data: fd,
                processData: false,
                contentType: false,
                success: function (data) {
                    linkTo('admin.srv?act=<%=AdminActions.openFileStructure%>&folderId=' + parentid);
                }
            });
        }

        function moveFile(ev) {
            var fd = new FormData();
            var folderid=ev.originalEvent.currentTarget.dataset.dragid;
            fd.append('act', 'moveFile');
            fd.append('fileId', ev.originalEvent.dataTransfer.getData('dragId'));
            fd.append('folderId', folderid);
            $.ajax({
                type: 'POST',
                url: '/file.srv?',
                data: fd,
                processData: false,
                contentType: false,
                success: function (data) {
                    linkTo('admin.srv?act=<%=AdminActions.openFileStructure%>&folderId=' + folderid);
                }
            });
        }

        function dropFiles(ev){
            console.log('dropped');
            if (isFileDrop(ev)){
                var folderid=ev.originalEvent.currentTarget.dataset.dragid;
                var files=ev.originalEvent.dataTransfer.files;
                var fd = new FormData();
                fd.append('act', 'addFiles');
                fd.append('folderId', folderid);
                fd.append('numFiles',files.length.toString());
                for (var i=0;i<files.length;i++){
                    var file=files[i];
                    fd.append('file_'+i, file);
                }
                $.ajax({
                    type: 'POST',
                    url: '/file.srv?',
                    data: fd,
                    processData: false,
                    contentType: false,
                    success: function (data) {
                        linkTo('admin.srv?act=openFileStructure&folderId=' + folderid);
                    }
                });
            }
        }
    </script>

