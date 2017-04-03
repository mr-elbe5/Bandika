<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.cms.CmsPartData" %>
<%@ page import="de.bandika.cms.ImageField" %>
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
  ImageField field = (ImageField) pdata.getField(fieldName);
%>
<div>
  <input type="hidden" id="<%=field.getIdentifier()%>ImgId" name="<%=field.getIdentifier()%>ImgId" value="<%=field.getImgId()%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Width" name="<%=field.getIdentifier()%>Width" value="<%=StringFormat.toHtml(field.getWidth())%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Height" name="<%=field.getIdentifier()%>Height" value="<%=StringFormat.toHtml(field.getHeight())%>" />
  <input type="hidden" id="<%=field.getIdentifier()%>Alt" name="<%=field.getIdentifier()%>Alt" value="<%=StringFormat.toHtml(field.getAltText())%>" />
  <a href="#" class="editField" onclick="$('#selectImage<%=field.getIdentifier()%>').modal();return false;">
  <img class="editField <%=StringFormat.isNullOrEmtpy(className) ? "" : className%>" id="<%=field.getIdentifier()%>"
      <% if (field.getImgId() > 0){%>
       src="/image.srv?act=show&fid=<%=field.getImgId()%>" alt="<%=StringFormat.toHtml(field.getAltText())%>" style="width:<%=StringFormat.toHtml(field.getWidth())%>; height:<%=StringFormat.toHtml(field.getHeight())%>"
      <%}else{%>
       src="/_statics/images/dummy.gif"
      <%}%> />
  </a>
</div>
<div id="selectImage<%=field.getIdentifier()%>" class="modal hide" tabindex="-1" role="dialog" aria-labelledby="<%=StringCache.getHtml("cms_image", locale)%>" aria-hidden="true">
  <div class="modal-header">
    <button type="button" class="close" data-dismiss="modal" aria-hidden="true">&times;</button>
    <legend><%=StringCache.getHtml("cms_image", locale)%></legend>
  </div>
  <div class="modal-body">
    <div class="layerContent">
      <div class="imageSelDiv">
        <input type="hidden" id="<%=field.getIdentifier()%>SelImgId" value="<%=field.getImgId()%>" />
        <a href="#" class="editField" onclick="return openSetImage('<%=field.getIdentifier()%>Sel',<%=data.getId()%>);">
          <img class="editField <%=StringFormat.isNullOrEmtpy(className) ? "" : className%>" id="<%=field.getIdentifier()%>Sel"
          <% if (field.getImgId() > 0){%>
               src="/image.srv?act=showThumbnail&fid=<%=field.getImgId()%>"
          <%}else{%>
             src="/_statics/images/dummy.gif"
          <%}%> />
        </a>
      </div>
      <div>
        <bandika:controlGroup labelKey="cms_width" mandatory="false">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelWidth" value="<%=StringFormat.toHtml(field.getWidth())%>" maxlength="10"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="cms_height" mandatory="false">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelHeight" value="<%=StringFormat.toHtml(field.getHeight())%>" maxlength="10"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="cms_alt" mandatory="false">
          <input class="input-block-level" type="text" id="<%=field.getIdentifier()%>SelAlt" value="<%=StringFormat.toHtml(field.getAltText())%>" maxlength="100"/>
        </bandika:controlGroup>
      </div>
    </div>
  </div>
  <div class="modal-footer">
    <a href="#" class="btn btn-primary" data-dismiss="modal" aria-hidden="true" onclick="setImage('<%=field.getIdentifier()%>');"><%=StringCache.getHtml("webapp_ok", locale)%></a>
    <a href="#" class="btn" data-dismiss="modal" aria-hidden="true"><%=StringCache.getHtml("webapp_cancel", locale)%></a>
  </div>
</div>


