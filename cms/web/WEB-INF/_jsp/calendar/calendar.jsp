<%--
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
--%>
<%@ page import="java.util.Locale" %>
<%@ page import="de.elbe5.webbase.servlet.SessionReader" %>
<%@ page import="de.elbe5.webbase.servlet.RequestReader" %>
<%@ page import="java.time.LocalDate" %>
<%@ page import="java.time.format.TextStyle" %>
<%@ page import="java.time.DayOfWeek" %>
<%@ page import="de.elbe5.cms.calendar.CalendarData" %>
<%@ page import="de.elbe5.cms.calendar.CalendarActions" %>
<%@ page import="de.elbe5.base.util.StringUtil" %>
<%
    int partId = RequestReader.getInt(request,"partId");
    Locale locale = SessionReader.getSessionLocale(request);
    String containerId="container"+partId;
    CalendarData data= (CalendarData) SessionReader.getSessionObject(request, CalendarActions.KEY_CALENDAR+partId);
    assert data!=null;
    LocalDate current=data.getFirstVisibleDay();
%>
<% if (RequestReader.isAjaxRequest(request)){%>
<jsp:include page="/WEB-INF/_jsp/_master/error.inc.jsp"/>
<%}%>
<div id="<%=containerId%>">
    <h3 class="calHeader"><%=data.getCurrentMonth().getDisplayName(TextStyle.FULL,locale)%>&nbsp;<%=data.getCurrent().getYear()%></h3>
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
        <% for (int line=0;line<data.getVisibleDays()/7;line++){%>
        <tr>
            <% for (int col=0;col<7;col++){
                String cls=current.equals(data.getToday()) ? "today" : current.getMonth().equals(data.getCurrentMonth()) ? "" : "other";
            %>
            <td class="day" data-day="<%=current.toString()%>">
                <div class="day <%=cls%>" ><%=current.getDayOfMonth()%></div>
            </td>
        <% current=current.plusDays(1);
            }%>
        </tr>
        <%}%>
    </table>
</div>
<script type="text/javascript">
    function sendCalendarAction(action) {
        var params = {act:action,partId: <%=partId%>};
        post2Target('/calendar.ajx', params, $('#<%=containerId%>').closest('.calendar'));
        return false;
    }
    $('.day').each( function(){
        this.addEventListener(contextEvent, function (e) {
            e.preventDefault();
            var $td = $(this);
            var $day=$td.data('day');
            openLayerDialog('<%=StringUtil.getHtml("_calendarEntry",locale)%>', '/calendar.ajx?act=<%=CalendarActions.openCreateEntry%>&partId=<%=partId%>&day='+$day);
        });
    });
</script>

