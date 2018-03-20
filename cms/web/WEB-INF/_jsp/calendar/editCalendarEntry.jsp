<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="java.util.List" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="de.elbe5.cms.calendar.CalendarEntryData" %>
<%@ page import="de.elbe5.cms.calendar.CalendarBean" %>
<%
    Locale locale= SessionReader.getSessionLocale(request);
    int partId = RequestReader.getInt(request,"partId");
    int entryId = 0;//RequestReader.getInt(request,"entryId");
    int userId = SessionReader.getLoginId(request);
    /*CalendarEntryData editEntry = (CalendarEntryData) SessionReader.getSessionObject(request, "entry");
    assert editEntry!=null;*/
    List<CalendarEntryData> entries = CalendarBean.getInstance().getEntryList(partId);
    String containerId ="container"+partId;
%>
<div id="<%=containerId%>">
    <form action="/calendar.ajx" method="post" id="calendarform" name="calendarform" accept-charset="UTF-8">
        <input type="hidden" name="act" value="saveEntry"/>
        <input type="hidden" name="partId" value="<%=partId%>"/>
        <input type="hidden" name="entryId" value="<%=entryId%>"/>
    </form>
    Entry for <%=RequestReader.getString(request,"day")%>
</div>
<script type="text/javascript">
    $('#calendarform').submit(function (event) {
        var $this = $(this);
        event.preventDefault();
        var params = $this.serialize();
        post2Target('/calendar.ajx', params, $('#<%=containerId%>').closest('.calendar'));
    });
    function sendCalendarAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/calendar.ajx', params, $('#<%=containerId%>').closest('.calendar'));
        return false;
    }
</script>

