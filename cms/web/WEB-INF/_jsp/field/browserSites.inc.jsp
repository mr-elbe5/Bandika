<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.site.SiteData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.TreeNode" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.webbase.rights.Right" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    List<Integer> activeIds = (List<Integer>) request.getAttribute("activeIds");
    assert activeIds !=null;
    int nodeId = activeIds.get(activeIds.size()-1);
    SiteData siteData = (SiteData) request.getAttribute("siteData");
    assert siteData !=null;
    boolean isOpen = siteData.getId() == TreeNode.ID_ROOT || activeIds.contains(siteData.getId());
    String functionName = RequestReader.getString(request,"functionName");
%>
<li class="<%=isOpen ? "open" : ""%>">
    <div class="contextSource icn isite <%=nodeId==siteData.getId() ? " selected" : ""%>"
         onclick="$('#browserView').load('/field.srv?act=<%=functionName%>&siteId=<%=siteData.getId()%>')">
        <%=siteData.getDisplayName()%>
    </div>
    <%if (SessionReader.hasContentRight(request, siteData.getId(), Right.EDIT)) {%>
    <div class="contextMenu">
        <div class="icn ifile" onclick="return openBrowserLayerDialog('<%=StringUtil.getHtml("_createFile", locale)%>', '/field.ajx?act=openCreateImageInBrowser&siteId=<%=siteData.getId()%>');">
            <%=StringUtil.getHtml("_newFile", locale)%>
        </div>
    </div>
    <%}%>
    <%if (siteData.getSites().size() + siteData.getPages().size() + siteData.getFiles().size() > 0) {%>
    <ul>
    <%for (SiteData subSite : siteData.getSites()) {
        if (SessionReader.hasContentRight(request, subSite.getId(), Right.READ)){
            request.setAttribute("siteData",subSite); %>
        <jsp:include  page="/WEB-INF/_jsp/field/browserSites.inc.jsp" flush="true"/>
        <%}
    }
    request.setAttribute("siteData",siteData);%>
    </ul>
<%}%>
</li>