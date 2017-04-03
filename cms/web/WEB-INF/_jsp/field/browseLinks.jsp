<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.BrowseData" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "java.util.ArrayList" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import = "de.elbe5.cms.tree.CmsTreeViewHelper" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    BrowseData browseData = (BrowseData) SessionHelper.getSessionObject(request, "browseData");
    int siteId = browseData.getBrowsedSiteId();
    CmsTreeCache tc = CmsTreeCache.getInstance();
    if (siteId == 0) siteId = tc.getLanguageRootSiteId(locale);
    SiteData site = tc.getSite(siteId);
    List<Integer> activeIds = new ArrayList<>();
    if (site != null) activeIds.addAll(site.getParentIds());
    activeIds.add(siteId);
%><section class = "mainSection popup flexBox">
    <section class = "contentSection flexItemOne">
        <div class = "sectionInner">
            <div class = "treeHeader">
                <%=StringUtil.getHtml("_structure", locale)%>
            </div>
            <ul id = "navigation" class = "treeRoot">
                <%CmsTreeViewHelper.addBrowserSiteNode(tc.getRootSite(), siteId, activeIds, "showBrowsedLinks", request, locale, out);%>
            </ul>
        </div>
    </section>
    <aside class = "asideSection flexItemTwo">
        <div class = "sectionInner">
            <div id = "browserView"></div>
        </div>
    </aside>
</section>
<section class = "footerSection popup">
    <div class = "sectionInner">
        <div class = "buttonset">
            <button onclick = "window.close();"><%=StringUtil.getHtml("_close", locale)%>
            </button>
        </div>
    </div>
</section>
<script type = "text/javascript">
    $("#browserView").load('/field.srv?act=showBrowsedLinks&siteId=<%=siteId%>');
    $("#navigation").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initTreeForm();
</script>