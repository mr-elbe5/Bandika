<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.FieldPartData" %>
<%@ page import = "de.elbe5.cms.field.ImageField" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%Locale locale = SessionHelper.getSessionLocale(request);
    PageData data = (PageData) SessionHelper.getSessionObject(request, "pageData");
    FieldPartData pdata = (FieldPartData) data.getEditPagePart();
    String fieldName = RequestHelper.getString(request, "fieldName");
    String className = RequestHelper.getString(request, "className");
    ImageField field = (ImageField) pdata.getField(fieldName);
%>
            <fieldset>
                <table class = "form">
                    <tr>
                        <td>
                            <input type = "hidden" id = "<%=field.getIdentifier()%>SelImgId" value = "<%=field.getImgId()%>"/> <a href = "#" class = "editField"
                                onclick = "return openSetImage('<%=field.getIdentifier()%>Sel',<%=data.getParentId()%>,<%=data.getId()%>);"> <img
                                class = "editField <%=StringUtil.isNullOrEmtpy(className) ? "" : className%>" id = "<%=field.getIdentifier()%>Sel"<% if (field.getImgId() > 0) {%>
                                src = "/file.srv?act=showPreview&fileId=<%=field.getImgId()%>"<% } else {%> src = "/_statics/img/dummy.gif"<%}%> /> </a>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for = "<%=field.getIdentifier()%>SelAlt"><%=StringUtil.getHtml("_alt", locale)%>
                            </label></td>
                        <td>
                            <input type = "text" id = "<%=field.getIdentifier()%>SelAlt" value = "<%=StringUtil.toHtml(field.getAltText())%>" maxlength = "100"/>
                        </td>
                    </tr>
                </table>
            </fieldset>
            <div class = "buttonset topspace">
                <button class="primary" onclick = "setImage('<%=field.getIdentifier()%>');closeEditLayer();"><%=StringUtil.getHtml("_ok", locale)%></button>
            </div>



