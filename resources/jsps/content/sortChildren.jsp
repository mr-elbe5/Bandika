<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.net25.base.Formatter" %>
<%@ page import="de.net25.http.StdServlet" %>
<%@ page import="de.net25.http.RequestData" %>
<%@ page import="de.net25.http.SessionData" %>
<%@ page import="de.net25.resources.statics.Statics" %>
<%@ page import="de.net25.resources.statics.Strings" %>
<%@ page import="de.net25.content.SortData" %>
<%
  SessionData sdata = (SessionData) session.getAttribute(StdServlet.SESSION_DATA);
  RequestData rdata = (RequestData) request.getAttribute(StdServlet.REQUEST_DATA);
  SortData data = (SortData) sdata.getParam("sortData");
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
<form action="srv25" method="post" name="form" accept-charset="<%=Statics.ISOCODE%>">
  <input type="hidden" name="ctrl" value="<%=Statics.KEY_CONTENT%>"/>
  <input type="hidden" name="method" value="saveSortChildren"/>
  <input type="hidden" name="id" value="<%=data.getId()%>"/>
  <input type="hidden" name="childIdx" value="0"/>
  <input type="hidden" name="childRanking" value="0"/>

  <div class="adminTopHeader"><%=Strings.getHtml("sortChildPages", sdata.getLocale())%>
  </div>
  <div class="hline">&nbsp;</div>
  <div class="admin">
    <table class="adminTable">
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("name", sdata.getLocale())%>
        </td>
      </tr>
      <% boolean otherLine = false;
        int idx = 0;
        for (SortData child : data.getChildren()) {
          otherLine = !otherLine;
      %>
      <tr class="<%=otherLine? "adminWhiteLine" : "adminGreyLine"%>">
        <td><select id="select<%=idx%>" onchange="setRanking(<%=idx%>);">
          <% for (int i = 0; i < nChildren; i++) {%>
          <option value="<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
          </option>
          }             <%}%>
        </select></td>
        <td><%=Formatter.toHtml(child.getName())%>
        </td>
      </tr>
      <% idx++;
      }%>
    </table>
  </div>
  <div class="hline">&nbsp;</div>
  <ul class="adminButtonList">
    <li class="adminButton"><a href="#"
                               onClick="submitMethod('saveSortChildren');"><%=Strings.getHtml("save", sdata.getLocale())%>
    </a></li>
    <li class="adminButton"><a
        href="srv25?ctrl=<%=Statics.KEY_CONTENT%>&method=show&id=<%=data.getId()%>"><%=Strings.getHtml("cancel", sdata.getLocale())%>
    </a></li>
  </ul>
</form>
