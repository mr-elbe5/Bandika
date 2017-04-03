<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.Formatter" %>
<%@ page import="de.bandika.http.SessionData" %>
<%@ page import="de.bandika.http.HttpHelper" %>
<%@ page import="de.bandika.page.PageController" %>
<%@ page import="de.bandika.page.PageSortData" %>
<%@ page import="de.bandika.base.AdminStrings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
  HttpHelper.startJsp(request,response);
%>
<bnd:setMaster master="/_jsp/master.jsp">
<%
	SessionData sdata=HttpHelper.getSessionData(request);
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
	<form action="/index.jsp" method="post" name="form" accept-charset="<%=HttpHelper.ISOCODE%>">
		<input type="hidden" name="ctrl" value="<%=PageController.KEY_PAGE%>"/>
		<input type="hidden" name="method" value="saveSortChildren"/>
		<input type="hidden" name="childIdx" value="0"/>
		<input type="hidden" name="childRanking" value="0"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>

		<div class="adminTopHeader"><%=AdminStrings.sortchildpages%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderRightCol"><%=AdminStrings.name%>
        </td>
      </tr>
      <% boolean otherLine = false;
        int idx = 0;
        for (PageSortData child : data.getChildren()) {
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
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=AdminStrings.back%></button>
      <button	onclick="return submitMethod('saveSortChildren');"><%=AdminStrings.save%></button>
		</div>
	</form>
</bnd:setMaster>
