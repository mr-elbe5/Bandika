<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.CmsPartData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.cms.LinkField" %>
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
  String activeType=rdata.getString("activeType");
  String availableTypes=rdata.getString("availableTypes");
  LinkField field = (LinkField) pdata.getField(fieldName);
%>
  </a>
</div>
<div id="selectLink<%=field.getIdentifier()%>" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("cms_link", locale)%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <legend><%=StringCache.getHtml("cms_link", locale)%></legend>
  </div>
  <div class="modal-body">
    <div class="layerContent">
      <div>
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
    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="setLink('<%=field.getIdentifier()%>');"><%=StringCache.getHtml("webapp_ok", locale)%></a>
    <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><%=StringCache.getHtml("webapp_cancel", locale)%></a>
  </div>
</div>


