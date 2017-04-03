<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.pagepart.CkCallbackData" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="de.elbe5.site.SiteData" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.tree.TreeCache" %>
<%@ page import="de.elbe5.tree.TreeHelper" %>
<%@ page import="de.elbe5.field.FieldAction" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
    int siteId = browseData.getSiteId();
    TreeCache tc = TreeCache.getInstance();
    if (siteId == 0)
        siteId = tc.getLanguageRootSiteId(locale);
    SiteData site = tc.getSite(siteId);
    List<Integer> activeIds = new ArrayList<>();
    if (site != null)
        activeIds.addAll(site.getParentIds());
    activeIds.add(siteId);%>
<script type="text/javascript">
    $("#browserLayer").setLayerHeader("<%=StringUtil.getHtml("_selectLink",locale)%>&nbsp;<span style=\"font-size:80%;\">(<%=StringUtil.getHtml("_selectLinkHint",locale)%>)</span>");
</script>
<section class="mainSection flexRow">
    <section class="contentSection flexItemOne">
        <div class="sectionInner">
            <h3 class="treeHeader">
                <%=StringUtil.getHtml("_structure", locale)%>
            </h3>
            <ul id="navigation" class="treeRoot">
                <%TreeHelper.addBrowserSiteNode(tc.getRootSite(), siteId, activeIds, FieldAction.showSelectableBrowserLinks.name(), request, locale, out);%>
            </ul>
        </div>
    </section>
    <aside class="asideSection flexItemOne">
        <div class="sectionInner">
            <div id="browserView"></div>
        </div>
    </aside>
</section>
<script type="text/javascript">
    $("#browserView").load('/field.srv?act=showSelectableBrowserLinks&siteId=<%=siteId%>');
    $("#navigation").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initSelectables();
</script>