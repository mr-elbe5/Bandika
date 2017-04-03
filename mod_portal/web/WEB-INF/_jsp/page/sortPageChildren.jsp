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
<%@ page import="de.bandika.page.PageSortData" %>
<%@ page import="java.util.Locale" %>
<%@ taglib uri="/WEB-INF/bandikatags.tld" prefix="bandika" %>
<%
    SessionData sdata = RequestHelper.getSessionData(request);
    Locale locale=sdata.getLocale();
    PageSortData data = (PageSortData) sdata.get("sortData");
    int nChildren = data.getChildren().size();
%>
<script type="text/javascript">
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
<form class="form-horizontal" action="/page.srv" method="post" name="form" accept-charset="UTF-8">
    <input type="hidden" name="act" value="saveSortChildren"/>
    <input type="hidden" name="childIdx" value="0"/>
    <input type="hidden" name="childRanking" value="0"/>
    <input type="hidden" name="pageId" value="<%=data.getId()%>"/>

    <div class="well">
        <legend><%=StringCache.getHtml("portal_sortChildPages",locale)%>
        </legend>
        <table class="table">
            <tr class="formTableHeader">
                <th><%=StringCache.getHtml("portal_ranking",locale)%>
                </th>
                <th><%=StringCache.getHtml("portal_name",locale)%>
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
                <td><%=StringFormat.toHtml(child.getName())%>
                </td>
            </tr>
            <%
                    idx++;
                }
            %>
        </table>
    </div>
    <div class="btn-toolbar">
        <button type="submit" class="btn btn-primary" ><%=StringCache.getHtml("webapp_save",locale)%>
        </button>
        <button class="btn"
                onclick="return linkTo('/page.srv?act=openPageSettings&pageId=<%=data.getId()%>');"><%=StringCache.getHtml("webapp_back",locale)%>
        </button>
    </div>
</form>
