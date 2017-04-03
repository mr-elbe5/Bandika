<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.pagepart.CkCallbackData" %>
<%@ page import="de.elbe5.file.FileData" %>
<%@ page import="de.elbe5.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.tree.TreeCache" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
    int siteId = browseData.getSiteId();
    TreeCache tc = TreeCache.getInstance();
    List<FileData> files = siteId == 0 ? tc.getAllFiles() : tc.getSite(siteId).getFiles();%>
<table class="padded listTable" id="fileTable">
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
        String callbackLink = String.format("if (CKEDITOR) CKEDITOR.tools.callFunction(%s, '/file.srv?fileId=%s'); closeBrowserLayer();", browseData.getCkCallbackNum(), fileData.getId());
    %>
    <tr>
        <td>
            <a href="#" onclick="<%=callbackLink%>"><%=StringUtil.toHtml(fileData.getDisplayName())%>
            </a>
        </td>
        <td>
            <a href="#" onclick="<%=callbackLink%>"> <img src="/file.srv?act=showPreview&fileId=<%=fileData.getId()%>" border='0' alt="" id="img<%=fileData.getId()%>"/> </a>
        </td>
        <td>
            <a href="#" onClick="window.open('<%=fileData.getUrl()%>', 'FileViewer', 'width=800,height=600,resizable=yes');return false;"><%=StringUtil.getHtml("_preview", locale)%>
            </a></td>
    </tr>
    <%}%>
    </tbody>
</table>
