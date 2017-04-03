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
<%@ page import="de.bandika.template.PartTemplateData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    PartTemplateData data = (PartTemplateData) sdata.get("templateData");
%>
<form class="form-horizontal" action="/template.srv" method="post" name="form" accept-charset="UTF-8"
      enctype="multipart/form-data">
    <input type="hidden" name="act" value="savePartTemplate"/>

    <div class="well">
        <legend><%=StringCache.getHtml("portal_template",locale)%>
        </legend>
        <table class="table">
            <%if (!data.isNew()) {%>
            <bandika:controlGroup labelKey="portal_name" padded="true"><%=StringFormat.toHtml(data.getName())%>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_className" padded="true"><%=StringFormat.toHtml(data.getClassName())%>
            </bandika:controlGroup>
            <%} else {%>
            <bandika:controlGroup labelKey="portal_name" name="name" mandatory="true">
                <input class="input-block-level" type="text" id="name" name="name"
                       value="<%=StringFormat.toHtml(data.getName())%>" maxlength="60"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_className" name="className" mandatory="false">
                <input class="input-block-level" type="text" id="className" name="className"
                       value="<%=StringFormat.toHtml(data.getClassName())%>" maxlength="120"/>
            </bandika:controlGroup>
            <%}%>
            <bandika:controlGroup labelKey="portal_file" name="file" mandatory="false">
                <bandika:fileUpload name="file"/>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_download" name="download" padded="true">
                <a href="/template.srv?act=downloadPartTemplate&name=<%=data.getName()%>"><%=StringCache.getHtml("portal_download",locale)%>
                </a>
            </bandika:controlGroup>
            <bandika:controlGroup labelKey="portal_description" name="description" mandatory="false">
                <textarea class="input-block-level" id="description" name="description" rows="5"
                          cols=""><%=StringFormat.toHtmlInput(data.getDescription())%>
                </textarea>
            </bandika:controlGroup>
        </table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/template.srv?act=openEditPartTemplates');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>
