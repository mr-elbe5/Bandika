<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.page.PageAssetSelectData" %>
<%@ page import="de.bandika.file.DocumentData" %>
<%@ page import="de.bandika.file.ImageData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
    String type = selectData.getActiveType();
    if (!selectData.isSingleType()) {%>
<ul class="nav nav-tabs" id="tabSelect">
    <% for (String s : selectData.getAvailableTypes()) {%>
    <li <%=s.equals(selectData.getActiveType()) ? "class=\"active\"" : ""%>><a href="#" onclick="document.location.href='/page.srv?act=changeAssetType&activeType=<%=s%>&pageId=<%=selectData.getPageId()%>';"><%=StringCache.getHtml("portal_"+s, locale)%></a></li>
    <%}%>
</ul>
<%}
    if (type.equals(DocumentData.FILETYPE)) {%>
<jsp:include page="/WEB-INF/_jsp/file/selectDocumentAsset.inc.jsp"/>
<%} else if (type.equals(ImageData.FILETYPE)) {%>
<jsp:include page="/WEB-INF/_jsp/file/selectImageAsset.inc.jsp"/>
<%} else if (type.equals(PageData.FILETYPE)) {%>
<jsp:include page="/WEB-INF/_jsp/page/selectPageAsset.inc.jsp"/>
<%}%>