<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.base.cache.Strings" %>
<%@ page import="de.elbe5.file.FileCache" %>
<%@ page import="de.elbe5.file.FolderData" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="de.elbe5.rights.Right" %>
<%@ page import="de.elbe5.user.GroupBean" %>
<%@ page import="de.elbe5.user.GroupData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    FolderData folderData = (FolderData) rdata.getSessionObject("folderData");
    assert (folderData != null);
    FolderData parentFolder = null;
    if (folderData.getParentId() != 0) {
        FileCache tc = FileCache.getInstance();
        parentFolder = tc.getFolder(folderData.getParentId());
    }
    List<GroupData> groups = GroupBean.getInstance().getAllGroups();
    String label;
    String name;
    String url = "/ctrl/file/saveFolder/" + folderData.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_editFolder",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="folderform" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <h3><%=Strings.html("_settings",locale)%>
                </h3>
                <cms:line label="_id"><%=Integer.toString(folderData.getId())%>
                </cms:line>
                <cms:line label="_creationDate"><%=StringUtil.toHtmlDateTime(folderData.getCreationDate(), locale)%>
                </cms:line>
                <cms:line label="_changeDate"><%=StringUtil.toHtmlDateTime(folderData.getChangeDate(), locale)%>
                </cms:line>
                <cms:line label="_parentFolder"><%=(parentFolder == null) ? "-" : StringUtil.toHtml(parentFolder.getName()) + "&nbsp;(" + parentFolder.getId() + ')'%>
                </cms:line>

                <cms:text name="name" label="_name" required="true" value="<%=StringUtil.toHtml(folderData.getName())%>"/>
                <cms:text name="description" label="_description" value="<%=StringUtil.toHtml(folderData.getDescription())%>"/>
                <cms:line label="_anonymous" padded="true">
                    <cms:check name="anonymous" value="true" checked="<%=folderData.isAnonymous()%>"/>
                </cms:line>
                <h3><%=Strings.html("_rights",locale)%>
                </h3>
                <cms:line label="_inheritsRights" padded="true">
                    <cms:check name="inheritsRights" value="true" checked="<%=folderData.inheritsRights()%>"/>
                </cms:line>
                <%
                    for (GroupData group : groups) {
                        if (group.getId() <= GroupData.ID_MAX_FINAL)
                            continue;
                        {
                %><%
                label = StringUtil.toHtml(group.getName());
                name = "groupright_" + group.getId();%>
                <cms:line label="<%=label%>" padded="true">
                    <cms:radio name="<%=name%>" value="" checked="<%=!folderData.hasAnyGroupRight(group.getId())%>"><%=Strings.html("_rightnone",locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.READ.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.READ)%>"><%=Strings.html("_rightread",locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.EDIT.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.EDIT)%>"><%=Strings.html("_rightedit",locale)%>
                    </cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.APPROVE.name()%>" checked="<%=folderData.isGroupRight(group.getId(), Right.APPROVE)%>"><%=Strings.html("_rightapprove",locale)%>
                    </cms:radio>
                </cms:line>
                <%
                        }
                    }
                %>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_close",locale)%>
                </button>
                <button type="submit" class="btn btn-outline-primary"><%=Strings.html("_save",locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>


