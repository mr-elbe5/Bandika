<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="java.io.IOException" %>
<%@ page import="de.bandika.application.Configuration" %>
<%@ page import="java.io.File" %>
<%@ page import="de.bandika.application.StringCache" %>
<%
  String basePath = Configuration.getBasePath();
  File root = new File(basePath);
%>
<%!
  protected static void addNodes(File[] files, String baseUrl, JspWriter writer) throws IOException {
    if (files != null && files.length > 0) {
      writer.write("<ul>");
      for (File file : files) {
        writer.print("<li><a ");
        if (!file.isDirectory()) {
          writer.write("href=\"/_application?method=downloadFile&path=");
          writer.write(FormatHelper.toHtml(baseUrl + "/"));
          writer.write(FormatHelper.toHtml(file.getName()));
          writer.write("\">");
        } else
          writer.write(">");
        writer.write(FormatHelper.toHtml(file.getName()));
        writer.write("</a>");
        if (!file.isDirectory()) {
          writer.write("&nbsp;<a href=\"#\" onclick=\"filePath='");
          writer.write(FormatHelper.toHtml(file.getPath().replace("\\", "/")));
          writer.write("'; $('#replaceFile').modal();return false;\">");
          writer.write(StringCache.getHtml("replace_"));
          writer.write("</a>");
          writer.write("&nbsp;<a href=\"#\" onclick=\"filePath='");
          writer.write(FormatHelper.toHtml(file.getPath().replace("\\", "/")));
          writer.write("'; $('#deleteFile').modal();return false;\">");
          writer.write(StringCache.getHtml("delete_"));
          writer.write("</a>");
        }
        addNodes(file.listFiles(), baseUrl + "/" + file.getName(), writer);
        writer.write("</li>");
      }
      writer.write("</ul>");
    }
  }
%>
<div class="well">
  <legend><%=StringCache.getHtml("fileTree")%>
  </legend>
  <div class="fileTree" id="treeWrapper" style="display:none">
    <div class="menuContent">
      <div class="menuHeader">
        <%=root.getName()%>
      </div>
      <div id="menuDiv">
        <ul id="filetree">
          <%addNodes(root.listFiles(), "", out);%>
        </ul>
      </div>
      <div>&nbsp;</div>
    </div>
    <div class="menuFooter">&nbsp;</div>
    <script type="text/javascript">
      var filePath = "";
      $("#filetree").treeview({
        persist: "location",
        collapsed: true,
        unique: true
      });
      document.getElementById('treeWrapper').style.display = 'block';
    </script>
  </div>
</div>

<div id="replaceFile" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("replaceFile")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("replaceFile")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>
<div id="deleteFile" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("deleteFile")%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">x</button>
    <legend><%=StringCache.getHtml("deleteFile")%>
    </legend>
  </div>
  <div class="modal-body">
  </div>
</div>

<script type="text/javascript">
  $(function () {
    $("#replaceFile").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#replaceFile').on('show', function () {
    $("#replaceFile").find($(".modal-body")).load("/_application?method=openReplaceFile&path=" + filePath);
  });
  $(function () {
    $("#deleteFile").modal({
      show: false,
      backdrop: "static"
    });
  });
  $('#deleteFile').on('show', function () {
    $("#deleteFile").find($(".modal-body")).load("/_application?method=openDeleteFile&path=" + filePath);
  });
</script>



