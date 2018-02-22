<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.field.FieldActions" %>
<%@ page import="de.bandika.cms.page.CkCallbackData" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.site.SiteData" %>
<%@ page import="de.bandika.cms.tree.TreeCache" %>
<%@ page import="de.bandika.cms.tree.TreeHelper" %>
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
    SiteData site = tc.getSite(siteId);
    List<Integer> activeIds = new ArrayList<>();
    if (site != null)
        activeIds.addAll(site.getParentIds());
    activeIds.add(siteId);
%>
<script type="text/javascript">
    $("#browserLayer").setLayerHeader("<%=StringUtil.getHtml("_selectLink",locale)%>&nbsp;<span style=\"font-size:80%;\">(<%=StringUtil.getHtml("_selectLinkHint",locale)%>)</span>");
</script>
<section class="selectorMain">
    <div class="tree">
        <h3 class="treeHeader">
            <%=StringUtil.getHtml("_structure", locale)%>
        </h3>
        <ul id="browseNavigation" class="treeRoot">
            <%TreeHelper.addBrowserSiteNode(out, request, tc.getRootSite(), siteId, activeIds, FieldActions.showSelectableBrowserLinks, locale);%>
        </ul>
    </div>
    <div id="browserView"></div>
</section>
<script type="text/javascript">
    $("#browserView").load('/field.srv?act=<%=FieldActions.showSelectableBrowserLinks%>&siteId=<%=siteId%>');
    $("#browseNavigation").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".tree").initContextMenus();
</script>