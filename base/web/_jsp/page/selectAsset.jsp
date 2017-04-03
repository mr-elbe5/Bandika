<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.page.PageAssetSelectData" %>
<%@ page import="de.bandika.file.*" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  PageAssetSelectData selectData = (PageAssetSelectData) sdata.getParam("selectData");
  String type = selectData.getType();
  if (!selectData.isSingleType()) {%>
<div class="well">
  <form class="form-horizontal" action="">
    <bandika:controlGroup labelKey="assetType" padded="true">
      <% for (String s : selectData.getAvailableTypes()) {%>
      <span><input type="radio" name="assetSwitch" onclick="document.location.href='/_page?method=changeAssetType&type=<%=s%>';" <%=s.equals(selectData.getType()) ? "checked" : ""%>/>&nbsp;<%=StringCache.getHtml(s)%></span>
      <%}%>
    </bandika:controlGroup>
  </form>
</div>
<%
  }
  if (type.equals(DocumentData.FILETYPE)) {
    FileController.getInstance().ensureFileFilterData(selectData.getPageId(), type, sdata);
    selectData.setDimensioned(false);
%>
<jsp:include page="/_jsp/file/document/selectFile.inc.jsp"/>
<%
} else if (type.equals(ImageData.FILETYPE)) {
  selectData.setDimensioned(true);
  FileController.getInstance().ensureFileFilterData(selectData.getPageId(), type, sdata);
%>
<jsp:include page="/_jsp/file/image/selectFile.inc.jsp"/>
<%
} else if (type.equals(PageData.FILETYPE)) {
  selectData.setDimensioned(false);
%>
<jsp:include page="/_jsp/page/selectPage.inc.jsp"/>
<%}%>