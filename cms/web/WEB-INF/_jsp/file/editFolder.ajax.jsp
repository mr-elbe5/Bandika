<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileCache" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    FolderData folderData = (FolderData) SessionReader.getSessionObject(request, FileActions.KEY_FOLDER);
    assert(folderData !=null);
    FolderData parentFolder = null;
    if (folderData.getParentId() != 0) {
        FileCache tc = FileCache.getInstance();
        parentFolder = tc.getFolder(folderData.getParentId());
    }
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    String label;
    String name;
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._editFolder.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/file.ajx" name="folderform" act="<%=FileActions.saveFolder%>" ajax="true">
            <input type="hidden" name="folderId" value="<%=folderData.getId()%>"/>
            <div class="modal-body">
                <cms:requesterror/>
                <h3><%=Strings._settings.html(locale)%></h3>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(folderData.getId())%></cms:line>
                <cms:line label="<%=Strings._creationDate.toString()%>"><%=StringUtil.toHtmlDateTime(folderData.getCreationDate(), locale)%></cms:line>
                <cms:line label="<%=Strings._changeDate.toString()%>"><%=StringUtil.toHtmlDateTime(folderData.getChangeDate(), locale)%></cms:line>
                <cms:line label="<%=Strings._parentFolder.toString()%>"><%=(parentFolder == null) ? "-" : StringUtil.toHtml(parentFolder.getName()) + "&nbsp;(" + parentFolder.getId() + ')'%></cms:line>

                <cms:text name="name" label="<%=Strings._name.toString()%>" required="true"><%=StringUtil.toHtml(folderData.getName())%></cms:text>
                <cms:line label="<%=Strings._url.toString()%>"><%=StringUtil.toHtml(folderData.getUrl())%></cms:line>
                <cms:text name="description" label="<%=Strings._description.toString()%>"><%=StringUtil.toHtml(folderData.getDescription())%></cms:text>
                <cms:line label="<%=Strings._anonymous.toString()%>" padded="true">
                    <cms:check name="anonymous" value="true" checked="<%=folderData.isAnonymous()%>" />
                </cms:line>
                <h3><%=Strings._rights.html(locale)%></h3>
                <cms:line label="<%=Strings._inheritsRights.toString()%>" padded="true">
                    <cms:check name="inheritsRights" value="true" checked="<%=folderData.inheritsRights()%>" />
                </cms:line>
                <%for (GroupData group : groups) {
                    if (group.getId() <= GroupData.ID_MAX_FINAL)
                        continue; {%>
                <%
                    label = StringUtil.toHtml(group.getName());
                    name = "groupright_" + group.getId();
                %>
                <cms:line label="<%=label%>" padded="true">
                    <cms:radio name="<%=name%>" value="" checked="<%=!folderData.hasAnyGroupRight(group.getId())%>"><%=Strings._rightnone.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.READ.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.READ)%>"><%=Strings._rightread.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.EDIT.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.EDIT)%>"><%=Strings._rightedit.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.APPROVE.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.APPROVE)%>"><%=Strings._rightapprove.html(locale)%></cms:radio>
                </cms:line>
                <%}}%>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-secondary"
                        data-dismiss="modal"><%=Strings._close.html(locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings._save.html(locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>


