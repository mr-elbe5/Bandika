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
<%@ page import="de.elbe5.templatepage.TemplatePageBean" %>
<%@ page import="de.elbe5.request.RequestData" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.templatepage.PagePartData" %>
<%@ taglib uri="/WEB-INF/cmstags.tld" prefix="cms" %>
<%
    RequestData rdata = RequestData.getRequestData(request);
    Locale locale = rdata.getSessionLocale();
    int pageId = rdata.getId();
    int partId = rdata.getInt("partId");
    String sectionName = rdata.getString("sectionName");
    String sectionType = rdata.getString("sectionType");
    List<PagePartData> parts = TemplatePageBean.getInstance().getSharedPageParts();
    String url = "/ctrl/templatepage/addSharedPagePart/" + pageId;
%>
<div class="modal-dialog modal-lg" role="document">
    <div class="modal-content">
        <div class="modal-header">
            <h5 class="modal-title"><%=Strings.html("_addSharedPagePart",locale)%>
            </h5>
            <button type="button" class="close" data-dismiss="modal" aria-label="Close">
                <span aria-hidden="true">&times;</span>
            </button>
        </div>
        <cms:form url="<%=url%>" name="partform" ajax="true">
            <div class="modal-body">
                <cms:formerror/>
                <input type="hidden" name="partId" value="<%=partId%>"/>
                <input type="hidden" name="sectionName" value="<%=sectionName%>"/>
                <input type="hidden" name="sectionType" value="<%=sectionType%>"/>

                <cms:line label="_position" padded="true">
                    <cms:radio name="below" value="false"><%=Strings.html("_above",locale)%>
                    </cms:radio>&nbsp;<cms:radio name="below" value="true" checked="true"><%=Strings.html("_below",locale)%>
                </cms:radio>
                </cms:line>
                <cms:line label="_sharedPart" padded="true" required="true">
                    <% for (PagePartData data : parts) {%>
                    <cms:radio name="sharedPartId" value="<%=String.valueOf(data.getId())%>"><%=StringUtil.toHtml(data.getName())%>
                    </cms:radio><br/>
                    <%}%>
                </cms:line>
            </div>
            <div class="modal-footer">
                <button type="button" class="btn btn-outline-secondary" data-dismiss="modal"><%=Strings.html("_cancel",locale)%>
                </button>
                <button type="submit" class="btn btn-primary"><%=Strings.html("_add",locale)%>
                </button>
            </div>
        </cms:form>
    </div>
</div>
