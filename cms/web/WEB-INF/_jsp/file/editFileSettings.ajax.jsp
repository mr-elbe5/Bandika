<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.file.FileData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    FileData data = (FileData) SessionReader.getSessionObject(request, "fileData");
    request.setAttribute("treeNode", data);%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/file.srv" method="post" id="filesettingsform" name="filesettingsform" accept-charset="UTF-8">
    <fieldset>
        <input type="hidden" name="fileId" value="<%=data.getId()%>"/> <input type="hidden" name="act" value="saveFileSettings"/>
        <table class="padded form">
            <jsp:include page="../tree/editNode.inc.jsp" flush="true"/>
            <jsp:include page="../tree/editResource.inc.jsp" flush="true"/>
            <tr>
                <td><label><%=StringUtil.getHtml("_mediaType", locale)%>
                </label></td>
                <td>
          <span><%=StringUtil.toHtml(data.getMediaType())%>
          </span>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_contentType", locale)%>
                </label></td>
                <td>
          <span><%=StringUtil.toHtml(data.getContentType())%>
          </span>
                </td>
            </tr>
            <tr>
                <td><label><%=StringUtil.getHtml("_fileSize", locale)%>
                </label></td>
                <td>
          <span><%=Integer.toString(data.getFileSize())%>
          </span>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="width"><%=StringUtil.getHtml("_width", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="width" name="width" value="<%=Integer.toString(data.getWidth())%>" maxlength="10"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="height"><%=StringUtil.getHtml("_height", locale)%>
                    </label></td>
                <td>
                    <input type="text" id="height" name="height" value="<%=Integer.toString(data.getHeight())%>" maxlength="10"/>
                </td>
            </tr>
            <% if (data.hasPreview()) {%>
            <tr>
                <td><label><%=StringUtil.getHtml("_preview", locale)%>
                </label></td>
                <td>
          <span><img src="/file.srv?act=showPreview&fileId=<%=data.getId()%>" alt="">
          </span>
                </td>
            </tr>
            <%}%>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#filesettingsform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/file.ajx', params);
    });
</script>

