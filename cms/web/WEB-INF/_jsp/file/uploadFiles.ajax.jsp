<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    int siteId = RequestReader.getInt(request, "siteId");
%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
    <fieldset>
        <input type="hidden" name="siteId" value="<%=siteId%>"/>
        <table class="padded form">
            <tr>
                <td>
                    <label><%=StringUtil.getHtml("_dragFilesHere", locale)%></label></td>
                <td>
                    <div class="dropArea"></div>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
    </div>
<script type="text/javascript">
    var $dropArea=$('.dropArea');
    var siteid=<%=siteId%>;
    $dropArea.on('dragenter', function (e) {
        e.preventDefault();
        if (isFileDrag(e)) {
            $dropArea.addClass('dropTarget');
        }
    });
    $dropArea.on('dragover', function (e) {
        e.preventDefault();
    });
    $dropArea.on('dragleave', function (e) {
        e.preventDefault();
        if (isFileDrag(e)) {
            $dropArea.removeClass('dropTarget');
        }
    });
    $dropArea.on('drop', function (e) {
        e.preventDefault();
        if (isFileDrop(e)) {
            var files = e.originalEvent.dataTransfer.files;
            var fd = new FormData();
            for (var i = 0; i < files.length; i++) {
                fd.append('file_' + i, files[i]);
            }
            fd.append('numFiles', files.length);
            fd.append('act', 'uploadFiles');
            fd.append('siteId', siteid);
            $.ajax({
                type: 'POST',
                url: '/file.ajx?',
                data: fd,
                processData: false,
                contentType: false,
                success: function (data) {
                    closeLayerToTree('tree.srv?act=openTree&siteId=' + siteid);
                }
            });
        }
    });
</script>
