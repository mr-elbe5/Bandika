<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.cms.HtmlPartData" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.data.StringFormat" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  boolean editMode = rdata.getInt("partEditMode", 0) == 1;
  HtmlPartData pdata = (HtmlPartData) rdata.get("pagePartData");
%>
<div>
  <%
    if (editMode) {
      SessionData sdata = RequestHelper.getSessionData(request);
      PageData data = (PageData) sdata.get("pageData");
      int pageId = data == null ? 0 : data.getId();
  %>
  <div class="ckeditField" id="htmlArea" contenteditable="true"><%=pdata.getHtml()%></div>
  <input type="hidden" name="htmlArea" value="<%=StringFormat.toHtml(pdata.getHtml())%>" />
  <script type="text/javascript">
  CKEDITOR.disableAutoInline=true;
  CKEDITOR.inline('htmlArea',{
  customConfig : '/_statics/script/editorConfig.js',
  toolbar: 'Full',
  filebrowserBrowseUrl : '/page.srv?act=openSelectAsset&assetUsage=LINK&forHTML=1&activeType=page&availableTypes=page,document,image&pageId=<%=pageId%>',
  filebrowserImageBrowseUrl : '/page.srv?act=openSelectAsset&assetUsage=FILE&forHTML=1&activeType=image&availableTypes=image&pageId=<%=pageId%>'
  });
  </script>
  <%} else {%>
  <%=pdata.getHtmlForOutput()%>
  <%}%>
</div>
