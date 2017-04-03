<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.cms.field.BrowseData" %>
<%@ page import = "de.elbe5.cms.file.FileData" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "java.util.List" %>
<%@ page import = "java.util.Locale" %>
<%@ page import="de.elbe5.cms.tree.CmsTreeCache" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    BrowseData browseData = (BrowseData) SessionHelper.getSessionObject(request, "browseData");
    int siteId = browseData.getBrowsedSiteId();
    CmsTreeCache tc = CmsTreeCache.getInstance();
    List<FileData> files = siteId==0 ? tc.getAllFiles() : tc.getSite(siteId).getFiles();
%>
<table class = "listTable" id = "fileTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_name", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_preview", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_image", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%for (FileData fileData : files) {
      if (!fileData.isImage())
        continue;
    %>
    <tr>
        <td>
            <a href = "#" onclick = "<%=browseData.getImageCallbackLink(fileData.getId())%>"><%=StringUtil.toHtml(fileData.getDisplayName())%>
            </a>
        </td>
        <td>
            <a href = "#" onclick = "<%=browseData.getImageCallbackLink(fileData.getId())%>"> <img src = "/file.srv?act=showPreview&fileId=<%=fileData.getId()%>" border = '0'
                    alt = "" id = "img<%=fileData.getId()%>"/> </a>
        </td>
        <td>
            <a href = "#"
                    onClick = "window.open('/file.srv?act=show&fileId=<%=fileData.getId()%>','FileViewer','width=800,height=600,resizable=yes');return false;"><%=StringUtil.getHtml("_view", locale)%>
            </a></td>
    </tr>
    <%}%>
    </tbody>
</table>
