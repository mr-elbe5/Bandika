<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.BrowseData" %>
<%@ page import = "de.elbe5.cms.file.FileData" %>
<%@ page import = "de.elbe5.cms.page.PageData" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.cms.site.SiteData" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    BrowseData browseData = (BrowseData) SessionHelper.getSessionObject(request, "browseData");
    CmsTreeCache tc = CmsTreeCache.getInstance();
    SiteData site = tc.getSite(browseData.getBrowsedSiteId());
    List<PageData> pages = site.getPages();
    List<FileData> files = site.getFiles();
%>
<table class = "listTable" id = "siteTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_site", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <tr>
        <td><a href = "#" onclick = "<%=browseData.getCallbackLink(site.getUrl())%>"><%=StringUtil.toHtml(site.getDisplayName())%>
        </a>
        </td>
    </tr>
    </tbody>
</table>
<table class = "listTable topspace" id = "pagesTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_pages", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%for (PageData pageData : pages) {%>
    <tr>
        <td><a href = "#" onclick = "<%=browseData.getCallbackLink(pageData.getUrl())%>"><%=StringUtil.toHtml(pageData.getDisplayName())%>
        </a>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
<table class = "listTable topspace" id = "fileTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_files", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%for (FileData fileData : files) {%>
    <tr>
        <td>
            <a href = "#" onclick = "<%=browseData.getCallbackLink(fileData.getUrl())%>"><%=StringUtil.toHtml(fileData.getDisplayName())%>
            </a>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
