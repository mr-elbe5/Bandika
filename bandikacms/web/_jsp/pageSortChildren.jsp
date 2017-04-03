<%--
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
--%>

<%@ page import="de.bandika.base.FormatHelper" %>
<%@ page import="de.bandika.page.PageSortData" %>
<%@ page import="de.bandika.base.RequestHelper" %>
<%@ page import="de.bandika.data.SessionData" %>
<%@ page import="de.bandika.base.Strings" %>
<%@ taglib uri="/WEB-INF/btags.tld" prefix="bnd" %>
<%
	SessionData sdata= RequestHelper.getSessionData(request);
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
	<form action="/_page" method="post" name="form" accept-charset="<%=RequestHelper.ISOCODE%>">
		<input type="hidden" name="method" value="saveSortChildren"/>
		<input type="hidden" name="childIdx" value="0"/>
		<input type="hidden" name="childRanking" value="0"/>
		<input type="hidden" name="id" value="<%=data.getId()%>"/>

		<div class="adminTopHeader"><%=Strings.getHtml("sortchildpages")%></div>
		<div class="hline">&nbsp;</div>
		<bnd:adminTable>
      <tr class="adminHeader">
        <td class="adminHeaderLeftCol">&nbsp;</td>
        <td class="adminHeaderRightCol"><%=Strings.getHtml("name")%>
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
        <td><%=FormatHelper.toHtml(child.getName())%>
        </td>
      </tr>
      <% idx++;
      }%>
		</bnd:adminTable>
		<div class="hline">&nbsp;</div>
    <div class="adminTableButtonArea">
			<button	onclick="return linkTo('/index.jsp');"><%=Strings.getHtml("back")%></button>
      <button	onclick="return submitMethod('saveSortChildren');"><%=Strings.getHtml("save")%></button>
		</div>
	</form>
