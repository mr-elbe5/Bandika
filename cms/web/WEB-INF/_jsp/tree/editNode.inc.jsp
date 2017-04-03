<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.configuration.Configuration" %>
<%@ page import="de.bandika.servlet.SessionReader" %>
<%@ page import="de.bandika.site.SiteData" %>
<%@ page import="de.bandika.tree.TreeNode" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.tree.TreeCache" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    TreeNode data = (TreeNode) request.getAttribute("treeNode");
    SiteData parentSite = null;
    if (data.getParentId() != 0) {
        TreeCache tc = TreeCache.getInstance();
        parentSite = tc.getSite(data.getParentId());
    }%>
<tr>
    <td><label><%=StringUtil.getHtml("_id", locale)%>
    </label></td>
    <td>
        <%=Integer.toString(data.getId())%>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_creationDate", locale)%>
    </label></td>
    <td>
        <%=Configuration.getInstance().getHtmlDateTime(data.getCreationDate(), locale)%>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_changeDate", locale)%>
    </label></td>
    <td>
        <%=Configuration.getInstance().getHtmlDateTime(data.getChangeDate(), locale)%>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_parentSite", locale)%>
    </label></td>
    <td>
        <%=(parentSite == null) ? "-" : StringUtil.toHtml(parentSite.getName()) + "&nbsp;(" + parentSite.getId() + ')'%>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_ranking", locale)%>
    </label></td>
    <td>
        <div><%=Integer.toString(data.getRanking())%>
        </div>
    </td>
</tr>
<tr>
    <td><label for="name"><%=StringUtil.getHtml("_name", locale)%>&nbsp;*</label></td>
    <td>
        <div>
            <input type="text" id="name" name="name" value="<%=StringUtil.toHtml(data.getName())%>" maxlength="60"/>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_url", locale)%>&nbsp;*</label></td>
    <td>
        <div><%=StringUtil.toHtml(data.getUrl())%>
        </div>
    </td>
</tr>
<tr>
    <td>
        <label for="displayName"><%=StringUtil.getHtml("_displayName", locale)%>
        </label></td>
    <td>
        <div>
            <input type="text" id="displayName" name="displayName" value="<%=StringUtil.toHtml(data.getDisplayName())%>" maxlength="60"/>
        </div>
    </td>
</tr>
<tr>
    <td>
        <label for="description"><%=StringUtil.getHtml("_description", locale)%>
        </label></td>
    <td>
        <div>
            <input type="text" id="description" name="description" value="<%=StringUtil.toHtml(data.getDescription())%>" maxlength="200"/>
        </div>
    </td>
</tr>
<tr>
    <td><label><%=StringUtil.getHtml("_author", locale)%>
    </label></td>
    <td>
        <div><%=StringUtil.toHtml(data.getAuthorName())%>
        </div>
    </td>
</tr>
<tr>
    <td>
        <label for="inNavigation"><%=StringUtil.getHtml("_inNavigation", locale)%>
        </label></td>
    <td>
        <div>
            <input type="checkbox" id="inNavigation" name="inNavigation" value="true" <%=data.isInNavigation() ? "checked" : ""%>/>
        </div>
    </td>
</tr>
<tr>
    <td>
        <label for="anonymous"><%=StringUtil.getHtml("_anonymous", locale)%>
        </label></td>
    <td>
        <div>
            <input type="checkbox" id="anonymous" name="anonymous" value="true" <%=data.isAnonymous() ? "checked" : ""%>/>
        </div>
    </td>
</tr>
<tr>
    <td>
        <label for="inheritsRights"><%=StringUtil.getHtml("_inheritsRights", locale)%>
        </label></td>
    <td>
        <div>
            <input type="checkbox" id="inheritsRights" name="inheritsRights" value="true" <%=data.inheritsRights() ? "checked" : ""%>/>
        </div>
    </td>
</tr>
