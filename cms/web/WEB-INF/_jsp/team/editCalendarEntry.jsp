<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="de.bandika.cms.team.TeamCalendarEntryData" %>
<%@ page import="de.bandika.cms.team.TeamCalendarBean" %>
<%
    Locale locale= SessionReader.getSessionLocale(request);
    int partId = RequestReader.getInt(request,"partId");
    int entryId = RequestReader.getInt(request,"entryId");
    int userId = SessionReader.getLoginId(request);
    TeamCalendarEntryData editEntry = (TeamCalendarEntryData) SessionReader.getSessionObject(request, "entry");
    assert editEntry!=null;
    List<TeamCalendarEntryData> entries = TeamCalendarBean.getInstance().getEntryList(partId);
    String containerId ="container"+partId;
%>
<div id="<%=containerId%>">
    <form action="/teamcalendar.ajx" method="post" id="teamcalendarform" name="teamcalendarform" accept-charset="UTF-8">
        <input type="hidden" name="act" value="saveEntry"/>
        <input type="hidden" name="partId" value="<%=partId%>"/>
        <input type="hidden" name="entryId" value="<%=editEntry.getId()%>"/>

    </form>
</div>
<script type="text/javascript">
    $('#teamcalendarform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2Target('/teamcalendar.ajx', params, $('#<%=containerId%>').closest('.teamcalendar'));
    });
    function sendCalendarAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/teamcalendar.ajx', params, $('#<%=containerId%>').closest('.teamcalendar'));
        return false;
    }
</script>
