<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.data.StringFormat" %>
<%@ page import="de.bandika.data.StringCache" %>
<%@ page import="de.bandika.template.TemplateBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.template.PartTemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.servlet.RequestHelper" %>
<%@ page import="de.bandika.servlet.SessionData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    TemplateBean ts = TemplateBean.getInstance();
    List<PartTemplateData> templates = ts.getAllPartTemplates();
%>
<form class="form-horizontal" action="/template.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value=""/>

    <div class="well">
        <legend><%=StringCache.getHtml("portal_templates",locale)%>
        </legend>
        <bandika:table id="templateTable" checkId="tname" formName="form" headerKeys="portal_name">
            <% for (PartTemplateData template : templates) { %>
            <tr>
                <td><input type="checkbox" name="tname" value="<%=template.getName()%>"/>
                </td>
                <td>
                    <a href="/template.srv?act=openEditPartTemplate&tname=<%=StringFormat.encode(template.getName())%>"><%=StringFormat.toHtml(template.getName())%>
                    </a></td>
            </tr>
            <%
                }
            %>
        </bandika:table>
    </div>
    <div class="btn-toolbar">
        <button class="btn btn-primary"
                onclick="return linkTo('/template.srv?act=openCreatePartTemplate');"><%=StringCache.getHtml("webapp_new",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openEditPartTemplate');"><%=StringCache.getHtml("webapp_change",locale)%>
        </button>
        <button class="btn btn-primary"
                onclick="return submitAction('openDeletePartTemplates');"><%=StringCache.getHtml("webapp_delete",locale)%>
        </button>
    </div>
</form>
