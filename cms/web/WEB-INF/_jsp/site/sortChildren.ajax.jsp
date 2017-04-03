<%--
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import = "de.elbe5.base.util.StringUtil" %>
<%@ page import = "de.elbe5.webserver.servlet.SessionHelper" %>
<%@ page import = "de.elbe5.webserver.tree.TreeNodeSortData" %>
<%@ page import = "java.util.Locale" %>
<%
    Locale locale = SessionHelper.getSessionLocale(request);
    TreeNodeSortData data = (TreeNodeSortData) SessionHelper.getSessionObject(request, "sortData");
    int nChildren = data.getChildren().size();
%>
<script type = "text/javascript">
    function setRanking(idx) {
        var sel = document.getElementById('select' + idx);
        if (sel) {
            document.form.childIdx.value = idx;
            document.form.childRanking.value = sel.selectedIndex;
            document.form.act.value = 'changeRanking';
            document.form.submit();
        }
    }
</script>
<form action = "/site.srv" method = "post" name = "form" accept-charset = "UTF-8">
    <input type = "hidden" name = "act" value = "saveSortChildren"/> <input type = "hidden" name = "childIdx" value = "0"/> <input type = "hidden" name = "childRanking"
        value = "0"/> <input type = "hidden" name = "pageId" value = "<%=data.getId()%>"/>

    <div class = "well">
        <legend><%=StringUtil.getHtml("_sortChildPages", locale)%>
        </legend>
        <table class = "listTable">
            <tr class = "formTableHeader">
                <th><%=StringUtil.getHtml("_ranking", locale)%>
                </th>
                <th><%=StringUtil.getHtml("_name", locale)%>
                </th>
            </tr>
            <%
                int idx = 0;
                for (TreeNodeSortData child : data.getChildren()) {
            %>
            <tr>
                <td><select id = "select<%=idx%>" onchange = "setRanking(<%=idx%>);">
                    <%for (int i = 0; i < nChildren; i++) {%>
                    <option value = "<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
                    </option>
                    <%}%>
                </select></td>
                <td><%=StringUtil.toHtml(child.getName())%>
                </td>
            </tr>
            <%
                    idx++;
                }
            %>
        </table>
    </div>
    <div class = "buttonset topspace">
        <button type = "submit" class = "primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
        <button onclick = "linkTo('/default.srv?act=openAdministration&siteId=<%=data.getId()%>');"><%=StringUtil.getHtml("_back", locale)%>
        </button>
    </div>
</form>
