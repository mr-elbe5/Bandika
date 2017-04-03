<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.user.GroupData" %>
<%@ page import="de.bandika.user.UserBean" %>
<%@ page import="de.bandika.page.PageData" %>
<%@ page import="de.bandika.menu.MenuData" %>
<%@ page import="de.bandika.menu.MenuCache" %>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.page.PageRightsProvider" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  PageData data = (PageData) sdata.getParam("pageData");
  ArrayList<GroupData> groups = UserBean.getInstance().getAllGroups();
  MenuData parentPage = null;
  if (data.getParentId() != 0) {
    parentPage = MenuCache.getInstance().getNode(data.getParentId());
  }
%>

<form class="form-horizontal" action="/_page" method="post" name="form" accept-charset="UTF-8">
  <div class="well">
    <input type="hidden" name="id" value="<%=data.getId()%>"/>
    <input type="hidden" name="method" value="savePageFromSettings"/>

    <legend><%=StringCache.getHtml("pageSettings")%>
    </legend>
    <bandika:controlGroup labelKey="id" padded="true"><%=Integer.toString(data.getId())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="parentPage" padded="true"><%=(parentPage == null) ? "none" : FormatHelper.toHtml(parentPage.getName()) + "&nbsp;(" + parentPage.getId() + ")"%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="author" padded="true"><%=FormatHelper.toHtml(data.getAuthorName())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="changeDate" padded="true"><%=FormatHelper.toHtmlDateTime(data.getChangeDate())%>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="name" name="name" mandatory="true">
      <input class="input-block-level" type="text" id="name" name="name" value="<%=FormatHelper.toHtml(data.getName())%>" maxlength="60"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="path" name="path" mandatory="true">
      <input class="input-block-level" type="text" id="path" name="path" value="<%=FormatHelper.toHtml(data.getPath())%>" maxlength="60"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="redirectId" name="redirectId" mandatory="false">
      <input class="input-block-level" type="text" id="redirectId" name="redirectId" value="<%=data.getRedirectId()%>" maxlength="10"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="description" name="description" mandatory="false">
      <input class="input-block-level" type="text" id="description" name="description" value="<%=FormatHelper.toHtml(data.getDescription())%>" maxlength="200"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="metaKeywords" name="metaKeywords" mandatory="false">
      <input class="input-block-level" type="text" id="metaKeywords" name="metaKeywords" value="<%=FormatHelper.toHtml(data.getKeywords())%>" maxlength="500"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="locale" name="locale">
      <input class="input-block-level" type="text" id="locale" name="locale" value="<%=FormatHelper.toHtml(data.getLanguage())%>" maxlength="10"/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="inheritsLocale" name="inheritsLocale">
      <input class="input-block-level" type="checkbox" id="inheritsLocale" name="inheritsLocale" value="1" <%=data.inheritsLocale() ? "checked" : ""%>/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="restricted" name="restricted">
      <input class="input-block-level" type="checkbox" id="restricted" name="restricted" value="1" <%=data.isRestricted() ? "checked" : ""%>/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="inheritsRights" name="inheritsRights">
      <input class="input-block-level" type="checkbox" id="inheritsRights" name="inheritsRights" value="1" <%=data.inheritsRights() ? "checked" : ""%>/>
    </bandika:controlGroup>
    <bandika:controlGroup labelKey="visible" name="visible">
      <input class="input-block-level" type="checkbox" id="visible" name="visible" value="1" <%=data.isVisible() ? "checked" : ""%>/>
    </bandika:controlGroup>
  </div>
  <div class="well">
    <legend><%=StringCache.getHtml("rights")%>
    </legend>
    <table class="table">
      <tr class="formTableHeader">
        <th><%=StringCache.getHtml("group")%>
        </th>
        <th><%=StringCache.getHtml("rightnone")%>
        </th>
        <th><%=StringCache.getHtml("rightread")%>
        </th>
        <th><%=StringCache.getHtml("rightedit")%>
        </th>
        <th><%=StringCache.getHtml("rightapprove")%>
        </th>
      </tr>
      <%
        if (groups != null) {
          for (GroupData group : groups) {%>
      <tr>
        <td><%=FormatHelper.toHtml(group.getName())%>
        </td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=IRights.RIGHT_NONE%>"
          <%=!data.hasAnyGroupRight(group.getId()) ? "checked=\"checked\"" : ""%> /></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=IRights.ROLE_READER%>"
          <%=data.hasGroupRight(group.getId(), IRights.ROLE_READER) ? "checked=\"checked\"" : ""%> /></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=IRights.ROLE_EDITOR%>"
          <%=data.hasGroupRight(group.getId(), IRights.ROLE_EDITOR) ? "checked=\"checked\"" : ""%> /></td>
        <td><input type="radio" name="groupright_<%=group.getId()%>" value="<%=IRights.ROLE_APPROVER%>"
          <%=data.hasGroupRight(group.getId(), IRights.ROLE_APPROVER) ? "checked=\"checked\"" : ""%> /></td>
      </tr>
      <%
          }
        }
      %>
    </table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <% if (sdata.hasRight(PageRightsProvider.RIGHTS_TYPE_PAGE, data.getId(), IRights.RIGHT_APPROVE)) {%>
    <button class="btn btn-primary" onclick="return submitMethod('saveAndPublishPageFromSettings');"><%=StringCache.getHtml("publish")%>
    </button>
    <%}%>
    <button class="btn btn-primary" onclick="return submitMethod('openEditPageContentFromPageSettings');"><%=StringCache.getHtml("editContent")%>
    </button>
    <button class="btn" onclick="return linkTo('/_page?method=openPageSettings&id=<%=data.getId()%>');"><%=StringCache.getHtml("cancel")%>
    </button>
  </div>
</form>

