<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.field.FieldActions" %>
<%@ page import="de.elbe5.cms.page.CkCallbackData" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.cms.site.SiteData" %>
<%@ page import="de.elbe5.cms.tree.TreeCache" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
    assert browseData!=null;
    int siteId = browseData.getSiteId();
    TreeCache tc = TreeCache.getInstance();
    if (siteId == 0)
        siteId = tc.getLanguageRootSiteId(locale);
    SiteData siteData = tc.getSite(siteId);
    List<Integer> activeIds = new ArrayList<>();
    if (siteData != null)
        activeIds.addAll(siteData.getParentIds());
    activeIds.add(siteId);
    request.setAttribute("activeIds",activeIds);
    request.setAttribute("functionName",FieldActions.showSelectableBrowserImages);
%>
<script type="text/javascript">
    $("#browserLayer").setLayerHeader("<%=StringUtil.getHtml("_selectImage",locale)%>&nbsp;<span style=\"font-size:80%;\">(<%=StringUtil.getHtml("_selectImageHint",locale)%>)</span>");
</script>
<section class="selectorMain">
    <div class="tree">
        <div class="icn iimage clickable" onclick="$('#browserView').load('/field.srv?act=<%=FieldActions.showSelectableBrowserImages%>')"><%=StringUtil.getHtml("_all", locale)%>
            <br>
        </div>
        <h3 class="treeHeader">
            <%=StringUtil.getHtml("_structure", locale)%>
        </h3>
        <ul id="browseNavigation" class="treeRoot">
            <% request.setAttribute("siteData",TreeCache.getInstance().getRootSite()); %>
            <jsp:include  page="/WEB-INF/_jsp/field/browserSites.inc.jsp" flush="true"/>
        </ul>
    </div>
    <div id="browserView"></div>
</section>
<script type="text/javascript">
    $("#browserView").load('/field.srv?act=<%=FieldActions.showSelectableBrowserImages%>&siteId=<%=siteId%>');
    $("#browseNavigation").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".tree").initContextMenus();
</script>