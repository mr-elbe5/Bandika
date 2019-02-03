<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.file.FileData" %>
<%@ page import="de.elbe5.cms.servlet.SessionReader" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.file.FileActions" %>
<%@ page import="de.elbe5.cms.file.FileCache" %>
<%@ page import="de.elbe5.cms.file.FolderData" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    Locale locale = SessionReader.getSessionLocale(request);
    FileData fileData = (FileData) SessionReader.getSessionObject(request, "fileData");
    assert(fileData !=null);
    FolderData parentFolder = null;
    if (fileData.getFolderId() != 0) {
        FileCache tc = FileCache.getInstance();
        parentFolder = tc.getFolder(fileData.getFolderId());
    }
    String label;
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._editFileSettings.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="/file.ajx" name="fileform" act="<%=FileActions.saveFile%>" ajax="true" multi="true">
            <input type="hidden" name="fileId" value="<%=fileData.getId()%>"/>
            <div class="modal-body">
                <cms:message/>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(fileData.getId())%></cms:line>
                <cms:line label="<%=Strings._creationDate.toString()%>"><%=StringUtil.toHtmlDateTime(fileData.getCreationDate(), locale)%></cms:line>
                <cms:line label="<%=Strings._changeDate.toString()%>"><%=StringUtil.toHtmlDateTime(fileData.getChangeDate(), locale)%></cms:line>
                <cms:line label="<%=Strings._parentFolder.toString()%>"><%=(parentFolder == null) ? "-" : StringUtil.toHtml(parentFolder.getName()) + "&nbsp;(" + parentFolder.getId() + ')'%></cms:line>

                <cms:file name="file" label="<%=Strings._file.toString()%>" required="true"/>
                <cms:text name="name" label="<%=Strings._name.toString()%>" required="true"><%=StringUtil.toHtml(fileData.getName())%></cms:text>
                <cms:line label="<%=Strings._url.toString()%>"><%=StringUtil.toHtml(fileData.getUrl())%></cms:line>
                <cms:text name="displayName" label="<%=Strings._displayName.toString()%>"><%=StringUtil.toHtml(fileData.getDisplayName())%></cms:text>
                <cms:text name="description" label="<%=Strings._description.toString()%>"><%=StringUtil.toHtml(fileData.getDescription())%></cms:text>
                <cms:text name="keywords" label="<%=Strings._keywords.toString()%>"><%=StringUtil.toHtml(fileData.getKeywords())%></cms:text>
                <cms:line label="<%=Strings._author.toString()%>"><%=StringUtil.toHtml(fileData.getAuthorName())%></cms:line>
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


