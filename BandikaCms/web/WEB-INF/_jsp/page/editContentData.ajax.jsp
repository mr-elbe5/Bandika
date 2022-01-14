<%--
  Bandika CMS - A Java based modular Content Management System
  Copyright (C) 2009-2021 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%response.setContentType("text/html;charset=UTF-8");%>
<%@ page trimDirectiveWhitespaces="true" %>
<%@include file="/WEB-INF/_jsp/_include/_functions.inc.jsp" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.content.ContentData" %>
<%@ page import="de.elbe5.user.UserCache" %>
<%@ page import="de.elbe5.page.PageData" %>
<%@ page import="de.elbe5.layout.LayoutData" %>
<%@ page import="de.elbe5.layout.LayoutCache" %>
<%@ page import="de.elbe5.application.Configuration" %>
<%@ page import="de.elbe5.request.ContentRequestKeys" %>
<%@ taglib uri="/WEB-INF/formtags.tld" prefix="form" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    PageData contentData = rdata.getCurrentDataInRequestOrSession(ContentRequestKeys.KEY_CONTENT, PageData.class);
    assert (contentData != null);
    List<LayoutData> pageLayouts = LayoutCache.getLayouts(PageData.LAYOUT_TYPE);
    String url = "/ctrl/page/saveContentData/" + contentData.getId();%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=$SH("_editContentData")%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <form:form url="<%=url%>" name="pageform" ajax="true" multi="true">
            <div class="modal-body">
                <form:formerror/>
                <h3><%=$SH("_settings")%>
                </h3>
                <form:line label="_idAndUrl"><%=$I(contentData.getId())%> - <%=$H(contentData.getUrl())%>
                </form:line>
                <form:line label="_creation"><%=$DT(contentData.getCreationDate())%> - <%=$H(UserCache.getUser(contentData.getCreatorId()).getName())%>
                </form:line>
                <form:line label="_lastChange"><%=$DT(contentData.getChangeDate())%> - <%=$H(UserCache.getUser(contentData.getChangerId()).getName())%>
                </form:line>

                <form:text name="displayName" label="_name" required="true" value="<%=$H(contentData.getDisplayName())%>"/>
                <form:textarea name="description" label="_description" height="5em"><%=$H(contentData.getDescription())%></form:textarea>
                <form:text name="keywords" label="_keywords" value="<%=$H(contentData.getKeywords())%>"/>
                <form:select name="accessType" label="_accessType">
                    <option value="<%=ContentData.ACCESS_TYPE_OPEN%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_OPEN) ? "selected" : ""%>><%=$SH("system.accessTypeOpen")%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INHERITS%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INHERITS) ? "selected" : ""%>><%=$SH("system.accessTypeInherits")%>
                    </option>
                    <option value="<%=ContentData.ACCESS_TYPE_INDIVIDUAL%>" <%=contentData.getNavType().equals(ContentData.ACCESS_TYPE_INDIVIDUAL) ? "selected" : ""%>><%=$SH("system.accessTypeIndividual")%>
                    </option>
                </form:select>
                <form:select name="navType" label="_navType">
                    <option value="<%=ContentData.NAV_TYPE_NONE%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_NONE) ? "selected" : ""%>><%=$SH("system.navTypeNone")%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_HEADER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_HEADER) ? "selected" : ""%>><%=$SH("system.navTypeHeader")%>
                    </option>
                    <option value="<%=ContentData.NAV_TYPE_FOOTER%>" <%=contentData.getNavType().equals(ContentData.NAV_TYPE_FOOTER) ? "selected" : ""%>><%=$SH("system.navTypeFooter")%>
                    </option>
                </form:select>
                <form:line label="_active" padded="true">
                    <form:check name="active" value="true" checked="<%=contentData.isActive()%>"/>
                </form:line>
                <form:select name="layout" label="_pageLayout" required="true">
                    <option value="" <%=contentData.getLayout().isEmpty() ? "selected" : ""%>><%=$SH("_pleaseSelect")%>
                    </option>
                    <% for (LayoutData layout : pageLayouts) {
                        String layoutName=layout.getName();
                    %>
                    <option value="<%=$H(layoutName)%>" <%=layoutName.equals(contentData.getLayout()) ? "selected" : ""%>><%=$SH(layout.getKey())%>
                    </option>
                    <%}%>
                </form:select>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=$SH("_close")%>
                </button>
                <button type="submit" class="btn btn-primary"><%=$SH("_save")%>
                </button>
            </div>
        </form:form>
    </div>
</div>


