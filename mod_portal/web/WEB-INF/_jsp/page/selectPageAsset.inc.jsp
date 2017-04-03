<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.page.PageAssetSelectData" %>
<%@ page import="java.io.IOException" %>
<%@ page import="java.util.List" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    RequestData rdata = RequestHelper.getRequestData(request);
    SessionData sdata = RequestHelper.getSessionData(request);
    PageAssetSelectData selectData = (PageAssetSelectData) sdata.get("selectData");
    if (selectData.isForHtmlEditor()) {
        int callbackFuncNum = rdata.getInt("CKEditorFuncNum", -1);
        if (callbackFuncNum != -1)
            selectData.setCallbackFuncNum(callbackFuncNum);
    }
    MenuCache mc = MenuCache.getInstance();
    MenuData rootNode = mc.getHomePage(sdata.getLocale());
%>
<%!
    protected static void addNode(MenuData data, int level, SessionData sdata, JspWriter writer, boolean isForHtmlEditor) throws IOException {
        if (level > 0) {
            writer.print("<li class=\"open\"><a ");
            if (data.isEditableForBackendUser(sdata)) {
                if (isForHtmlEditor) {
                    writer.write("href=\"#\" onclick=\"callEditorCallback(");
                } else {
                    writer.write("href=\"#\" onclick=\"callFieldCallback(");
                }
                writer.write(Integer.toString(data.getId()));
                writer.write(");\">");
            } else
                writer.write(">");
            writer.write(StringFormat.toHtml(data.getName()));
            writer.write("</a>");
        }
        List<MenuData> children = data.getChildren();
        if (children != null) {
            int count = 0;
            for (MenuData child : children) {
                if (!child.isVisibleForBackendUser(sdata))
                    continue;
                if (count == 0 && level > 0)
                    writer.write("<ul>");
                addNode(child, level + 1, sdata, writer, isForHtmlEditor);
                count++;
            }
            if (count > 0 && level > 0)
                writer.write("</ul>");
        }
        if (level > 0) {
            writer.write("</li>");
        }
    }
%>
<script type="text/javascript">
    var ImageUploadWindow;
    var callIndex = null;
    function callEditorCallback(pageId) {
        window.opener.CKEDITOR.tools.callFunction(<%=selectData.getCallbackFuncNum()%>, '/page.srv?act=show&pageId=' + pageId);
        window.close();
    }
    function callFieldCallback(pageId) {
        opener.setSelLink('/page.srv?act=show&pageId=' + pageId);
        window.close();
    }
</script>
<div class="well">
    <div class="fileTree" id="treeWrapper">
        <div class="menuContent">
            <div class="menuHeader">
                <%if (selectData.isForHtmlEditor()) {%>
                <a href="#" onClick="callEditorCallback(<%=rootNode.getId()%>);return false;"><%=rootNode.getName()%>
                </a>
                <%} else {%>
                <a href="#" onClick="callFieldCallback(<%=rootNode.getId()%>);return false;"><%=rootNode.getName()%>
                </a>
                <%}%>
            </div>
            <div id="menuDiv">
                <ul id="navigation">
                    <%addNode(rootNode, 0, sdata, out, selectData.isForHtmlEditor());%>
                </ul>
            </div>
            <div>&nbsp;</div>
        </div>
        <div class="menuFooter">&nbsp;</div>
        <script type="text/javascript">
            $("#navigation").treeview({
                persist: "location",
                collapsed: false,
                unique: true
            });
        </script>
    </div>
</div>
