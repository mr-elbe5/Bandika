<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2018 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.rights.Right" %>
<%@ page import="de.elbe5.cms.rights.SystemZone" %>

<%@ page import="java.util.Locale" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="de.elbe5.cms.user.UserBean" %>
<%@ page import="de.elbe5.cms.user.UserData" %>
<%@ page import="de.elbe5.cms.servlet.RequestData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata= RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    GroupData group = (GroupData) rdata.getSessionObject("groupData");
    assert group != null;
    UserBean ubean = UserBean.getInstance();
    List<UserData> users = ubean.getAllUsers();
    String name,label;
    String url="/user/saveGroup/"+group.getId();
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings._editGroup.html(locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="groupform" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <h3><%=Strings._settings.html(locale)%></h3>
                <cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(group.getId())%></cms:line>
                <cms:text name="name" label="<%=Strings._name.toString()%>" required="true" value="<%=StringUtil.toHtml(group.getName())%>" />
                <cms:textarea name="notes" label="<%=Strings._notes.toString()%>" height="5rem"><%=StringUtil.toHtml(group.getNotes())%></cms:textarea>
                <h3><%=Strings._rights.html(locale)%></h3>
                <cms:line label="<%=Strings._id.toString()%>"><%=Strings._rights.html(locale)%></cms:line>
                <%for (SystemZone zone : SystemZone.values()) {%>
                <%
                    label = zone.name();
                    name = "zoneright_" + zone.name();
                %>
                <cms:line label="<%=label%>" padded="true">
                    <cms:radio name="<%=name%>" value="" checked="<%=!group.getRights().hasAnySystemRight(zone)%>"><%=Strings._rightnone.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.READ.name()%>" checked="<%=group.getRights().isSystemRight(zone, Right.READ)%>"><%=Strings._rightread.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.EDIT.name()%>" checked="<%=group.getRights().isSystemRight(zone, Right.EDIT)%>"><%=Strings._rightedit.html(locale)%></cms:radio><br/>
                    <cms:radio name="<%=name%>" value="<%=Right.APPROVE.name()%>" checked="<%=group.getRights().isSystemRight(zone, Right.APPROVE)%>"><%=Strings._rightapprove.html(locale)%></cms:radio>
                </cms:line>
                <%}%>
                <h3><%=Strings._users.html(locale)%></h3>
                <cms:line label="<%=Strings._user.toString()%>"><%=Strings._inGroup.html(locale)%></cms:line>
                <% for (UserData udata : users) {%>
                <%label = udata.getName();%>
                <cms:line label="<%=label%>"  padded="true">
                    <cms:check name="userIds" value="<%=Integer.toString(udata.getId())%>" checked="<%=group.getUserIds().contains(udata.getId())%>" />
                </cms:line>
                <%}%>
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


