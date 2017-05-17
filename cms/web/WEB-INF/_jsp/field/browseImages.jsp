<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.cms.field.FieldAction" %>
<%@ page import="de.bandika.cms.pagepart.CkCallbackData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.site.SiteData" %>
<%@ page import="de.bandika.cms.tree.TreeCache" %>
<%@ page import="de.bandika.cms.tree.TreeHelper" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
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
    $("#browserLayer").setLayerHeader("<%=StringUtil.getHtml("_selectImage",locale)%>&nbsp;<span style=\"font-size:80%;\">(<%=StringUtil.getHtml("_selectImageHint",locale)%>)</span>");
</script>
<section class="mainSection flexRow">
    <section class="contentSection flexItemOne">
        <div class="sectionInner">
            <div class="icn iimage clickable" onclick="$('#browserView').load('/field.srv?act=showSelectableBrowserImages')"><%=StringUtil.getHtml("_all", locale)%>
                <br>
            </div>
            <h3 class="treeHeader">
                <%=StringUtil.getHtml("_structure", locale)%>
            </h3>
            <ul id="browseNavigation" class="treeRoot">
                <%TreeHelper.addBrowserSiteNode(pageContext, out, request, tc.getRootSite(), siteId, activeIds, FieldAction.showSelectableBrowserImages.name(), locale);%>
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
    $("#browserView").load('/field.srv?act=showSelectableBrowserImages&siteId=<%=siteId%>');
    $("#browseNavigation").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initContextMenus();
</script>