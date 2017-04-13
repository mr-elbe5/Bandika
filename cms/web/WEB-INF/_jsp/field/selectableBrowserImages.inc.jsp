<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.file.FileData" %>
<%@ page import="de.bandika.pagepart.CkCallbackData" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.tree.TreeCache" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    CkCallbackData browseData = (CkCallbackData) SessionReader.getSessionObject(request, "browseData");
    int siteId = browseData.getSiteId();
    TreeCache tc = TreeCache.getInstance();
    List<FileData> files = siteId == 0 ? tc.getAllFiles() : tc.getSite(siteId).getFiles();
%>
<table class="padded listTable" id="fileTable">
    <thead>
    <tr>
        <th><%=StringUtil.getHtml("_name", locale)%>
        </th>
        <th><%=StringUtil.getHtml("_preview", locale)%>
        </th>
    </tr>
    </thead>
    <tbody>
    <%
        for (FileData fileData : files) {
            if (!fileData.isImage())
                continue;
            String callbackLink = String.format("if (CKEDITOR) CKEDITOR.tools.callFunction(%s, '%s'); closeBrowserLayer();", browseData.getCkCallbackNum(), StringUtil.toHtml(fileData.getUrl()));
    %>
    <tr>
        <td>
            <a href="#" onclick="<%=callbackLink%>"><%=StringUtil.toHtml(fileData.getDisplayName())%>
            </a>
        </td>
        <td>
            <a href="#" onclick="window.open('<%=fileData.getUrl()%>', 'FileViewer', 'width=800,height=600,resizable=yes');return false;">
                <img src="/file.srv?act=showPreview&fileId=<%=fileData.getId()%>" border='0' alt="" id="img<%=fileData.getId()%>"/>
            </a>
        </td>
    </tr>
    <%}%>
    </tbody>
</table>
