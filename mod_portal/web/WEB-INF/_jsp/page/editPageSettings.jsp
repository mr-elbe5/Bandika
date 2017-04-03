<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.page.PageRightsData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    PageData data = (PageData) sdata.get("pageData");
    List<GroupData> groups = UserBean.getInstance().getAllGroups();
    MenuData parentPage = null;
    if (data.getParentId() != 0) {
        parentPage = MenuCache.getInstance().getNode(data.getParentId());
    }
%>

<form class="form-horizontal" action="/page.srv" method="post" name="form" accept-charset="UTF-8">
    <div class="well">
        <input type="hidden" name="pageId" value="<%=data.getId()%>"/>
        <input type="hidden" name="act" value="savePageFromSettings"/>

        <legend><%=StringCache.getHtml("portal_pageSettings",locale)%>
        </legend>
        <bandika:controlGroup labelKey="portal_id" padded="true"><%=Integer.toString(data.getId())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_parentPage"
                              padded="true"><%=(parentPage == null) ? "none" : StringFormat.toHtml(parentPage.getName()) + "&nbsp;(" + parentPage.getId() + ")"%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_author" padded="true"><%=StringFormat.toHtml(data.getAuthorName())%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_changeDate" padded="true"><%=StringFormat.toHtmlDateTime(data.getChangeDate(), locale)%>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_name" name="name" mandatory="true">
            <input class="input-block-level" type="text" id="name" name="name"
                   value="<%=StringFormat.toHtml(data.getName())%>" maxlength="60"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_path" name="path" mandatory="true">
            <input class="input-block-level" type="text" id="path" name="path"
                   value="<%=StringFormat.toHtml(data.getPath())%>" maxlength="60"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_description" name="description" mandatory="false">
            <input class="input-block-level" type="text" id="description" name="description"
                   value="<%=StringFormat.toHtml(data.getDescription())%>" maxlength="200"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_metaKeywords" name="metaKeywords" mandatory="false">
            <input class="input-block-level" type="text" id="metaKeywords" name="metaKeywords"
                   value="<%=StringFormat.toHtml(data.getKeywords())%>" maxlength="500"/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_restricted" name="restricted">
            <input class="input-block-level" type="checkbox" id="restricted" name="restricted"
                   value="1" <%=data.isRestricted() ? "checked" : ""%>/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_inheritsRights" name="inheritsRights">
            <input class="input-block-level" type="checkbox" id="inheritsRights" name="inheritsRights"
                   value="1" <%=data.inheritsRights() ? "checked" : ""%>/>
        </bandika:controlGroup>
        <bandika:controlGroup labelKey="portal_visible" name="visible">
            <input class="input-block-level" type="checkbox" id="visible" name="visible"
                   value="1" <%=data.isVisible() ? "checked" : ""%>/>
        </bandika:controlGroup>
    </div>
    <div class="well">
        <legend><%=StringCache.getHtml("portal_rights",locale)%>
        </legend>
        <table class="table">
            <tr class="formTableHeader">
                <th><%=StringCache.getHtml("portal_group",locale)%>
                </th>
                <th><%=StringCache.getHtml("portal_rightnone",locale)%>
                </th>
                <th><%=StringCache.getHtml("portal_rightread",locale)%>
                </th>
                <th><%=StringCache.getHtml("portal_rightedit",locale)%>
                </th>
                <th><%=StringCache.getHtml("portal_rightapprove",locale)%>
                </th>
            </tr>
            <%
                if (groups != null) {
                    for (GroupData group : groups) {%>
            <tr>
                <td><%=StringFormat.toHtml(group.getName())%>
                </td>
                <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=PageRightsData.RIGHT_NONE%>"
                        <%=!data.hasAnyGroupRight(group.getId()) ? "checked=\"checked\"" : ""%> /></td>
                <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=PageRightsData.RIGHTS_READER%>"
                        <%=data.hasGroupRight(group.getId(), PageRightsData.RIGHTS_READER) ? "checked=\"checked\"" : ""%> /></td>
                <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=PageRightsData.RIGHTS_EDITOR%>"
                        <%=data.hasGroupRight(group.getId(), PageRightsData.RIGHTS_EDITOR) ? "checked=\"checked\"" : ""%> /></td>
                <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=PageRightsData.RIGHTS_APPROVER%>"
                        <%=data.hasGroupRight(group.getId(), PageRightsData.RIGHTS_APPROVER) ? "checked=\"checked\"" : ""%> />
                </td>
            </tr>
            <%
                    }
                }
            %>
        </table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getId(), PageRightsData.RIGHT_APPROVE)) {%>
        <button class="btn btn-primary"
                onclick="return submitAction('saveAndPublishPageFromSettings');"><%=StringCache.getHtml("portal_publish",locale)%>
        </button>
        <%}%>
        <button class="btn btn-primary"
                onclick="return submitAction('openEditPageContentFromPageSettings');"><%=StringCache.getHtml("portal_editContent",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=openPageSettings&pageId=<%=data.getId()%>');"><%=StringCache.getHtml("webapp_cancel",locale)%>
        </button>
    </div>
</form>

