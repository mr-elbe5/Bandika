<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.*" %>
<%@ page import="de.bandika.team.file.TeamFileData" %>
<%@ page import="de.bandika.team.file.TeamFileBean" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="de.bandika.team.file.TeamFilePartData" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  RequestData rdata = RequestHelper.getRequestData(request);
  SessionData sdata = RequestHelper.getSessionData(request);
  TeamFilePartData cpdata = (TeamFilePartData) rdata.getParam("pagePartData");
  ArrayList<TeamFileData> files = TeamFileBean.getInstance().getFileList(cpdata.getId(), sdata.getUserId());
  int fid = rdata.getParamInt("fid");
  Locale locale = sdata.getLocale();
%>
<script type="text/javascript">
  function submitFileMethod(method) {
    document.teamfileform.method.value = method;
    document.teamfileform.submit();
    return false;
  }
</script>
<form class="form-horizontal" action="/_teamfile" method="post" name="teamfileform" accept-charset="UTF-8">
  <input type="hidden" name="method" value=""/>
  <input type="hidden" name="id" value="<%=cpdata.getPageId()%>"/>
  <input type="hidden" name="pid" value="<%=cpdata.getId()%>"/>
  <legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
  </legend>
  <bandika:dataTable id="fileTable" checkId="fid" formName="teamfileform" sort="true" paging="true" headerKeys="name,version,owner,author,checkedoutby"
                     locale="<%=locale.getLanguage()%>">
    <% for (TeamFileData data : files) {%>
    <tr>
      <td><input type="checkbox" name="fid" value="<%=data.getId()%>" <%=fid == data.getId() ? "checked" : ""%>/></td>
      <td>
        <a href="/_teamfile?method=show&fid=<%=data.getId()%>&version=<%=data.getVersion()%>&pid=<%=cpdata.getId()%>&id=<%=cpdata.getPageId()%>" target="_blank"><%=FormatHelper.toHtml(data.getShortName())%>
        </a></td>
      <td><%=data.getVersion()%>
      </td>
      <td><%=FormatHelper.toHtml(data.getOwnerName())%>
      </td>
      <td><%=FormatHelper.toHtml(data.getAuthorName())%>
      </td>
      <td><%=FormatHelper.toHtml(data.getCheckoutName())%>
      </td>
    </tr>
    <%}%>
  </bandika:dataTable>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitFileMethod('openCreateFile');"><%=StringCache.getHtml("new", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('checkoutFile');"><%=StringCache.getHtml("checkout", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('openEditFile');"><%=StringCache.getHtml("edit", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('undoCheckoutFile');"><%=StringCache.getHtml("undoCheckout", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('checkinFile');"><%=StringCache.getHtml("checkin", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('openFileHistory');"><%=StringCache.getHtml("previousVersions", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('openDeleteFile');"><%=StringCache.getHtml("delete", locale)%>
    </button>
  </div>
</form>

