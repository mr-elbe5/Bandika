<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.sharing.SharedDocumentData" %>
<%@ page import="de.elbe5.cms.sharing.SharingBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    int userId = SessionReader.getLoginId(request);
    List<SharedDocumentData> documents = SharingBean.getInstance().getFileList(partId, SessionReader.getLoginId(request));
    Locale locale = SessionReader.getSessionLocale(request);
    String tableId="table"+partId;
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<fieldset>
    <table id="<%=tableId%>" class="padded blockheader">
        <tr><th width="15%"><%=StringUtil.getHtml("_name",locale)%></th>
            <th width="15%"><%=StringUtil.getHtml("_owner",locale)%></th>
            <th width="15%"><%=StringUtil.getHtml("_author",locale)%></th>
            <th width="15%"><%=StringUtil.getHtml("_checkedoutby",locale)%></th>
            <th width="40%"></th>
        </tr>
        <% for (SharedDocumentData fileData : documents) {%>
        <tr>
            <td>
                <a href="/sharing.srv?act=showDocument&fileId=<%=fileData.getId()%>" target="_blank"><%=StringUtil.toHtml(fileData.getShortName())%>
                </a></td>
            <td><%=StringUtil.toHtml(fileData.getOwnerName())%>
            </td>
            <td><%=StringUtil.toHtml(fileData.getAuthorName())%>
            </td>
            <td><%=StringUtil.toHtml(fileData.getCheckoutName())%>
            </td>
            <td>
                <% if (userId!=0){
                if (fileData.getCheckoutId()==0){%>
                <a class="icn icheckout" title="<%=StringUtil.getHtml("_checkout", locale)%>" href="" onclick="return sendSharingAction('checkoutDocument',<%=fileData.getId()%>);">&nbsp;</a>
                <%}else if (fileData.getCheckoutId()==userId){%>
                <a class="icn icheckin" title="<%=StringUtil.getHtml("_undoCheckout", locale)%>" href="" onclick="return sendSharingAction('undoCheckoutDocument',<%=fileData.getId()%>);">&nbsp;</a>
                <a class="icn iedit" title="<%=StringUtil.getHtml("_edit", locale)%>" href="" onclick="return sendSharingAction('openEditDocument',<%=fileData.getId()%>);">&nbsp;</a>
                <%}
                if (fileData.getOwnerId()==userId || fileData.getAuthorId()==userId){%>
                <a class="icn idelete" title="<%=StringUtil.getHtml("_delete", locale)%>" href="" onclick="return sendSharingAction('deleteDocument',<%=fileData.getId()%>);">&nbsp;</a>
                <%}}%>
            </td>
        </tr>
        <%}%>
    </table>
</fieldset>
<div class="buttonset topspace">
    <button class="primary" onclick="return sendSharingAction('openCreateDocument',0);"><%=StringUtil.getHtml("_new", locale)%>
    </button>
</div>
<script type="text/javascript">
    function sendSharingAction(action, fileId) {
        var params = {act:action,partId: <%=partId%>,fileId:fileId};
        post2Target('/sharing.ajx', params, $('#<%=tableId%>').closest('.documents'));
        return false;
    }
</script>


