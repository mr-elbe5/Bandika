<%--
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika._base.FormatHelper" %>
<%@ page import="de.bandika._base.RequestHelper" %>
<%@ page import="de.bandika.application.StringCache" %>
<%@ page import="de.bandika._base.SessionData" %>
<%@ page import="de.bandika.page.PageSortData" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
  SessionData sdata = RequestHelper.getSessionData(request);
  PageSortData data = (PageSortData) sdata.getParam("sortData");
  int nChildren = data.getChildren().size();
%>
<script type="text/javascript">
  function setRanking(idx) {
    var sel = document.getElementById('select' + idx);
    if (sel) {
      document.form.childIdx.value = idx;
      document.form.childRanking.value = sel.selectedIndex;
      document.form.method.value = 'changeRanking';
      document.form.submit();
    }
  }
</script>
<form class="form-horizontal" action="/_page" method="post" name="form" accept-charset="UTF-8">
  <input type="hidden" name="method" value="saveSortChildren"/>
  <input type="hidden" name="childIdx" value="0"/>
  <input type="hidden" name="childRanking" value="0"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>

  <div class="well">
    <legend><%=StringCache.getHtml("sortChildPages")%>
    </legend>
    <table class="table">
      <tr class="formTableHeader">
        <th><%=StringCache.getHtml("ranking")%>
        </th>
        <th><%=StringCache.getHtml("name")%>
        </th>
      </tr>
      <%
        int idx = 0;
        for (PageSortData child : data.getChildren()) {
      %>
      <tr>
        <td><select id="select<%=idx%>" onchange="setRanking(<%=idx%>);">
          <%for (int i = 0; i < nChildren; i++) {%>
          <option value="<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
          </option>
          <%}%>
        </select></td>
        <td><%=FormatHelper.toHtml(child.getName())%>
        </td>
      </tr>
      <%
          idx++;
        }
      %>
    </table>
  </div>
  <div class="btn-toolbar">
    <button class="btn btn-primary" onclick="document.form.submit();"><%=StringCache.getHtml("save")%>
    </button>
    <button class="btn" onclick="return linkTo('/_page?method=openPageSettings&id=<%=data.getId()%>');"><%=StringCache.getHtml("back")%>
    </button>
  </div>
</form>
