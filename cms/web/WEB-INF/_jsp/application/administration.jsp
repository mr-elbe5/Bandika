<%--
  Bandika  - A Java based modular Content Management System
  Copyright (C) 2009-2017 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="de.bandika.base.util.StringUtil" %>
<%@ page import="de.bandika.servlet.SessionReader" %>

<section class="mainSection flexRow">
    <section class="contentSection flexItemTwo">
        <div class="adminTree">
            <h3 class="treeHeader">
                <%=StringUtil.getString("_administration", SessionReader.getSessionLocale(request))%>
            </h3>
            <ul id="settings" class="treeRoot">
                <jsp:include page="/WEB-INF/_jsp/application/adminActions.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/application/adminSettings.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/search/adminSearch.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/template/adminTemplates.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/page/adminSharedPageParts.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/group/admingroups.inc.jsp"/>
                <jsp:include page="/WEB-INF/_jsp/user/adminusers.inc.jsp"/>
            </ul>
        </div>
    </section>
    <aside class="asideSection flexItemOne">
        <div id="details">
        
        </div>
    </aside>
</section>
<script type="text/javascript">
    $("#settings").treeview({
        persist: "location", collapsed: true, unique: false
    });
    $(".contentSection").initContextMenus();
</script>
    
