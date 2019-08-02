<%--
  Elbe 5 CMS - A Java based modular Content Management System
  Copyright (C) 2009-2019 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@ page import="de.elbe5.cms.application.Strings" %>
<%@ page import="de.elbe5.cms.page.templatepage.issuepagepart.IssuePagePartData" %>
<%@ page import="de.elbe5.cms.request.RequestData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.cms.user.GroupBean" %>
<%@ page import="de.elbe5.cms.user.GroupData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%@ page import="de.elbe5.cms.application.Statics" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    IssuePagePartData part = (IssuePagePartData) rdata.get(Statics.KEY_PART);
    GroupBean gbean = GroupBean.getInstance();
    List<GroupData> groups = gbean.getAllGroups();
    String label;
%>
<cms:line label="<%=Strings._id.toString()%>"><%=Integer.toString(part.getId())%>
</cms:line>
<cms:text name="name" label="<%=Strings._name.toString()%>" required="true"
          value="<%=StringUtil.toHtml(part.getName())%>"/>
<cms:textarea name="notes" label="<%=Strings._notes.toString()%>"
              height="5rem"><%=StringUtil.toHtml(part.getNotes())%>
</cms:textarea>
<cms:line label="<%=Strings._group.toString()%>"></cms:line>
<% for (GroupData gdata : groups) {%>
<%label = gdata.getName();%>
<cms:line label="<%=label%>" padded="true">
    <cms:radio name="groupId" value="<%=Integer.toString(gdata.getId())%>"
               checked="<%=part.getGroupId()==gdata.getId()%>"/>
</cms:line>
<%}%>