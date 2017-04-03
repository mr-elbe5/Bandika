<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.FieldPartData" %>
<%@ page import = "de.elbe5.cms.field.TextLinkField" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.RequestHelper" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.Locale" %>
<%@ taglib uri = "/WEB-INF/cmstags.tld" prefix = "cms" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    PageData data = (PageData) SessionHelper.getSessionObject(request, "pageData");
    FieldPartData pdata = (FieldPartData) data.getEditPagePart();
    String fieldName = RequestHelper.getString(request, "fieldName");
    TextLinkField field = (TextLinkField) pdata.getField(fieldName);
%>
            <fieldset>
                <table class = "form">
                    <tr>
                        <td>
                            <label for = "<%=field.getIdentifier()%>SelText"><%=StringUtil.getHtml("_text", locale)%>&nbsp;*</label></td>
                        <td>
                            <input type = "text" id = "<%=field.getIdentifier()%>SelText" value = "<%=StringUtil.toHtml(field.getText())%>" maxlength = "255"/>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for = "<%=field.getIdentifier()%>SelLink"><%=StringUtil.getHtml("_link", locale)%>&nbsp;*</label></td>
                        <td>
                            <input type = "text" id = "<%=field.getIdentifier()%>SelLink" value = "<%=StringUtil.toHtml(field.getLink())%>" maxlength = "255"/>&nbsp;<a href = "#"
                                class = "editField"
                                onclick = "return openSetLink('<%=field.getIdentifier()%>Sel',<%=data.getId()%>);"><%=StringUtil.getHtml("_browse", SessionHelper.getSessionLocale(request))%>
                        </a>
                        </td>
                    </tr>
                    <tr>
                        <td>
                            <label for = "<%=field.getIdentifier()%>SelTarget"><%=StringUtil.getHtml("_target", locale)%>
                            </label></td>
                        <td>
                            <input type = "text" id = "<%=field.getIdentifier()%>SelTarget" value = "<%=StringUtil.toHtml(field.getTarget())%>" maxlength = "100"/>
                        </td>
                    </tr>
                </table>
            </fieldset>
            <div class = "buttonset topspace">
                <button class = "primary" onclick = "setTextLink('<%=field.getIdentifier()%>');return closeEditLayer();"><%=StringUtil.getHtml("_ok", locale)%>
                </button>
            </div>


