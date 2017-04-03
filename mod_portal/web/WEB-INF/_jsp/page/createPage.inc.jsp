<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.servlet.RequestData" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.template.TemplateCache" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.template.LayoutTemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    RequestData rdata = RequestHelper.getRequestData(request);
    List<LayoutTemplateData> layoutTemplates = TemplateCache.getInstance().getLayoutTemplates();
    int pageId = rdata.getInt("pageId");
    boolean adminLayer = rdata.getBoolean("adminLayer");
%>
<div class="layerContent">
    <form class="form-horizontal" action="/page.srv" method="post" name="layoutform" accept-charset="UTF-8">
        <div class="well">
            <input type="hidden" name="pageId" value="<%=pageId%>"/>
            <input type="hidden" name="adminLayer" value="<%=adminLayer ? "1" : "0"%>"/>
            <input type="hidden" name="act" value="createPage"/>
            <bandika:controlGroup labelKey="portal_name" name="name" mandatory="true">
                <input class="input-block-level" type="text" id="name" name="name" value="" maxlength="60"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_path" name="path" mandatory="true">
                <input class="input-block-level" type="text" id="path" name="path" value="" maxlength="60"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_layout" padded="true">
                <%
                    boolean first = true;
                    for (LayoutTemplateData tdata : layoutTemplates) {
                %>
                <div>
                    <input type="radio" name="layout"
                           value="<%=StringFormat.toHtml(tdata.getName())%>" <%=first ? "checked=\"checked\"" : ""%>/><%=StringFormat.toHtml(tdata.getName())%>
                </div>
                <%
                        first = false;
                    }
                %>
            </bandika:controlGroup>
        </div>
        <div class="btn-toolbar">
            <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_create",locale)%>
            </button>
        </div>
    </form>
</div>
