<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.cms.CmsPartData" %>
<%@ page import="de.bandika.cms.TextLinkField" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  Locale locale=sdata.getLocale();
  PageData data = (PageData) sdata.get("pageData");
  CmsPartData pdata = (CmsPartData) rdata.get("pagePartData");
  String fieldName=rdata.getString("fieldName");
  String className=rdata.getString("className");
  String activeType=rdata.getString("activeType");
  String availableTypes=rdata.getString("availableTypes");
  TextLinkField field = (TextLinkField) pdata.getField(fieldName);
%>
<div>
  <input type="hidden" id="<%=field.getIdentifier()%>Text" name="<%=field.getIdentifier()%>Text" value="<%=StringFormat.toHtml(field.getText())%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Link" name="<%=field.getIdentifier()%>Link" value="<%=StringFormat.toHtml(field.getLink())%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Target" name="<%=field.getIdentifier()%>Target" value="<%=StringFormat.toHtml(field.getTarget())%>" />
  <a href="#" class="editField <%=StringFormat.isNullOrEmtpy(className) ? "" : className%>" onclick="$('#selectTextLink<%=field.getIdentifier()%>').modal();return false;" id="<%=field.getIdentifier()%>">
      <% if (!StringFormat.isNullOrEmtpy(field.getText())){%><%=StringFormat.toHtml(field.getText())%>
      <%}else{%>
      <%=StringCache.getHtml("cms_dummyLinkText", locale)%>
      <%}%>
  </a>
</div>
<div id="selectTextLink<%=field.getIdentifier()%>" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("cms_textlink", locale)%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <legend><%=StringCache.getHtml("cms_textlink", locale)%></legend>
  </div>
  <div class="modal-body">
    <div class="layerContent">
      <div>
        <bandika:controlGroup labelKey="cms_text" mandatory="true">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelText" value="<%=StringFormat.toHtml(field.getText())%>" maxlength="255"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="cms_link" mandatory="true">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelLink" value="<%=StringFormat.toHtml(field.getLink())%>" maxlength="255"/>&nbsp;<a href="#" class="editField" onclick="return openSetLink('<%=field.getIdentifier()%>Sel','<%=activeType%>','<%=availableTypes%>',<%=data.getId()%>);"><%=StringCache.getHtml("cms_browse", sdata.getLocale())%></a>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="cms_target" mandatory="false">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelTarget" value="<%=StringFormat.toHtml(field.getTarget())%>" maxlength="100"/>
        </bandika:controlGroup>
      </div>
    </div>
  </div>
  <div class="modal-footer">
    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="setTextLink('<%=field.getIdentifier()%>');"><%=StringCache.getHtml("webapp_ok", locale)%></a>
    <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><%=StringCache.getHtml("webapp_cancel", locale)%></a>
  </div>
</div>


