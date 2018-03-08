<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.bandika.cms.team.TeamCalendarEntryData" %>
<%@ page import="de.bandika.cms.team.TeamCalendarBean" %>
<%@ page import="java.util.List" %>
<%@ page import="de.bandika.webbase.servlet.SessionReader" %>
<%@ page import="de.bandika.webbase.servlet.RequestReader" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="java.time.DayOfWeek" %>
<%@ page import="java.time.Month" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    int entryId = RequestReader.getInt(request,"entryId");
    int userId = SessionReader.getLoginId(request);
    List<TeamCalendarEntryData> entries = TeamCalendarBean.getInstance().getEntryList(partId);
    Locale locale = SessionReader.getSessionLocale(request);
    String containerId="container"+partId;
    LocalDate today = LocalDate.now();
    Month currentMonth=today.getMonth();
    LocalDate current = today.minusDays(today.getDayOfMonth()-1);
    current=current.minusDays(current.getDayOfWeek().getValue()-1);
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<div id="<%=containerId%>">
    <h3 class="calHeader"><%=today.getMonth().getDisplayName(TextStyle.FULL,locale)%>&nbsp;<%=today.getYear()%></h3>
    <table class="calendar">
        <tr>
            <th><%=DayOfWeek.MONDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.TUESDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.WEDNESDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.THURSDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.FRIDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.SATURDAY.getDisplayName(TextStyle.FULL,locale)%></th>
            <th><%=DayOfWeek.SUNDAY.getDisplayName(TextStyle.FULL,locale)%></th>
        </tr>
        <% for (int line=0;line<6;line++){%>
        <tr>
            <% for (int col=0;col<7;col++){
                String cls=current.equals(today) ? "today" : current.getMonth().equals(currentMonth) ? "" : "other";
            %>
            <td><div class="day <%=cls%>" ><%=current.getDayOfMonth()%></div></td>
        <% current=current.plusDays(1);
            }%>
        </tr>
        <%}%>
    </table>
</div>
<script type="text/javascript">
    function sendCalendarAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/teamcalendar.ajx', params, $('#<%=containerId%>').closest('.teamcalendar'));
        return false;
    }
</script>


