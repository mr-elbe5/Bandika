<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    int siteId = RequestHelper.getInt(request, "siteId", 0);
%>
<form action = "/field.srv" method = "post" id = "createform" name = "createform" accept-charset = "UTF-8" enctype = "multipart/form-data">
    <input type = "hidden" name = "act" value = "saveBrowsedImage"/>
    <input type = "hidden" name = "siteId" value = "<%=siteId%>"/>
    <fieldset>
        <table class = "form">
            <tr>
                <td>
                    <label for = "file"><%=StringUtil.getHtml("_file", locale)%>&nbsp;*</label></td>
                <td>
                    <input type = "file" id = "file" name = "file"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "name"><%=StringUtil.getHtml("_name", locale)%></label></td>
                <td>
                    <input type = "text" id = "name" name = "name" value = "" maxlength = "60"/>
                </td>
            </tr>
            <tr>
                <td>
                    <label for = "displayName"><%=StringUtil.getHtml("_displayName", locale)%>
                    </label></td>
                <td>
                    <input type = "text" id = "displayName" name = "displayName" value = "" maxlength = "60"/>
                </td>
            </tr>
        </table>
    </fieldset>
    <div class = "buttonset topspace">
        <button onclick = "closeModalLayerDialog();"><%=StringUtil.getHtml("_close", locale)%>
        </button>
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
    </div>
</form>
<script type = "text/javascript">
    $('#createform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var data = $this.serializeFiles();
        postMulti2ModalDialog('/field.srv', data);
    });
</script>
