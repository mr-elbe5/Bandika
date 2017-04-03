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
  int fileId = rdata.getParamInt("fid");
  ArrayList<TeamFileData> files = TeamFileBean.getInstance().getFileHistory(fileId);
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
  <input type="hidden" name="fid" value="<%=fileId%>"/>
  <legend><%=FormatHelper.toHtml(cpdata.getTitle())%>
  </legend>
  <bandika:dataTable id="fileTable" checkId="version" formName="teamfileform" sort="true" paging="true" headerKeys="name,version,owner,author"
                     locale="<%=locale.getLanguage()%>">
    <%
      for (TeamFileData data : files) {%>
    <tr>
      <td><input type="checkbox" name="version" value="<%=data.getVersion()%>"/></td>
      <td>
        <a href="/_teamfile?method=show&fid=<%=data.getId()%>&version=<%=data.getVersion()%>&pid=<%=cpdata.getId()%>&id=<%=cpdata.getPageId()%>" target="_blank"><%=FormatHelper.toHtml(data.getShortName())%>
        </a></td>
      <td><%=data.getVersion()%>
      </td>
      <td><%=FormatHelper.toHtml(data.getOwnerName())%>
      </td>
      <td><%=FormatHelper.toHtml(data.getAuthorName())%>
      </td>
    </tr>
    <%}%>
  </bandika:dataTable>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="return submitFileMethod('restoreHistoryFile');"><%=StringCache.getHtml("restore", locale)%>
    </button>
    <button class="btn btn-primary" onclick="return submitFileMethod('openDeleteHistoryFile');"><%=StringCache.getHtml("delete", locale)%>
    </button>
    <button class="btn" onclick="return linkTo('/_page?method=show&id=<%=cpdata.getPageId()%>&fid=<%=fileId%>');"><%=StringCache.getHtml("back", locale)%>
    </button>
  </div>
</form>

