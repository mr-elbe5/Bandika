<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.servlet.RequestReader" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    int siteId = RequestReader.getInt(request, "siteId");%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<form action="/file.srv" method="post" id="uploadform" name="uploadform" accept-charset="UTF-8" enctype="multipart/form-data">
    <fieldset>
        <input type="hidden" name="siteId" value="<%=siteId%>"/> <input type="hidden" name="act" value="createFile"/>
        <table class="padded form">
            <tr>
                <td>
                    <label for="file"><%=StringUtil.getHtml("_file", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="file" id="file" name="file"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="displayName"><%=StringUtil.getHtml("_displayName", locale)%>&nbsp;*</label></td>
                <td>
                    <input type="text" id="displayName" name="displayName" value="" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="name"><%=StringUtil.getHtml("_name", locale)%></label></td>
                <td>
                    <input type="text" id="name" name="name" value="" maxlength="60"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for="publish"><%=StringUtil.getHtml("_publish", locale)%>
                    </label></td>
                <td>
                    <input type="checkbox" id="publish" name="publish" value="true"/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class="buttonset topspace">
        <button onclick="closeLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type="submit" class="primary"><%=StringUtil.getHtml("_create", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#uploadform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serializeFiles();
        postMulti2ModalDialog('/file.srv', params);
    });
</script>
