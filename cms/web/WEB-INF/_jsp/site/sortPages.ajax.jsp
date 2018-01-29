<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.cms.tree.TreeNodeSortData" %>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.site.SiteActions" %>
<%@ page import="de.bandika.cms.tree.TreeActions" %>
<%Locale locale = SessionReader.getSessionLocale(request);
    TreeNodeSortData data = (TreeNodeSortData) SessionReader.getSessionObject(request, "sortData");
    assert data!=null;
    int nChildren = data.getChildren().size();%>
<form action="/site.srv" method="post" name="rankform" id="rankform" accept-charset="UTF-8">
    <input type="hidden" name="act" value="<%=SiteActions.saveSortPages%>"/>
    <input type="hidden" name="childIdx" value="0"/>
    <input type="hidden" name="childRanking" value="0"/>
    <input type="hidden" name="siteId" value="<%=data.getId()%>"/>
    
    <div class="well">
        <table class="padded listTable">
            <tr class="formTableHeader">
                <th><%=StringUtil.getHtml("_ranking", locale)%>
                </th>
                <th><%=StringUtil.getHtml("_name", locale)%>
                </th>
            </tr>
            <%int idx = 0;
                for (TreeNodeSortData child : data.getChildren()) {%>
            <tr>
                <td><select id="select<%=idx%>" onchange="setRanking(<%=idx%>);">
                    <%for (int i = 0; i < nChildren; i++) {%>
                    <option value="<%=i%>" <%=i == idx ? "selected" : ""%>><%=i + 1%>
                    </option>
                    <%}%>
                </select></td>
                <td><%=StringUtil.toHtml(child.getName())%>
                </td>
            </tr>
            <%idx++;
            }%>
        </table>
    </div>
    <div class="buttonset topspace">
        <button type="submit" class="primary"><%=StringUtil.getHtml("_save", locale)%>
        </button>
        <button onclick="linkToTree('/tree.srv?act=<%=TreeActions.openTree%>&siteId=<%=data.getId()%>');"><%=StringUtil.getHtml("_back", locale)%>
        </button>
    </div>
</form>
<script type="text/javascript">
    $('#rankform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2ModalDialog('/site.ajx', params);
    });
    function setRanking(idx) {
        var sel = document.getElementById('select' + idx);
        if (sel) {
            document.rankform.childIdx.value = idx;
            document.rankform.childRanking.value = sel.selectedIndex;
            document.rankform.act.value = '<%=SiteActions.changePageRanking%>';
            var $form = $('#rankform');
            var params = $form.serialize();
            post2ModalDialog('/site.ajx', params);
        }
    }
</script>
