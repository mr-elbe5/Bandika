/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.team;

import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class TeamCalendarActions extends CmsActions {

    public static final String showCalendar="showCalendar";
    public static final String openCreateEntry="openCreateEntry";
    public static final String openEditEntry="openEditEntry";
    public static final String saveEntry="saveEntry";
    public static final String deleteEntry="deleteEntry";

    public static final String KEY = "teamcalendar";

    public static final String KEY_CALENDAR = "$TEAMCAL";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new TeamCalendarActions());
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case showCalendar: {
                setCalendarData(request);
                return showCalendar(request, response);
            }
            case openCreateEntry: {
                assertCalendarData(request);
                TeamCalendarEntryData data = new TeamCalendarEntryData();
                data.setPartId(RequestReader.getInt(request, "partId"));
                data.setId(TeamBlogBean.getInstance().getNextId());
                data.setNew(true);
                SessionWriter.setSessionObject(request, "entry", data);
                return showEditEntry(request, response);
            }
            case openEditEntry: {
                assertCalendarData(request);
                int id = RequestReader.getInt(request,"entryId");
                TeamCalendarEntryData data = TeamCalendarBean.getInstance().getEntryData(id);
                SessionWriter.setSessionObject(request, "entry", data);
                return showEditEntry(request, response);
            }
            case saveEntry: {
                assertCalendarData(request);
                TeamCalendarEntryData data = (TeamCalendarEntryData) SessionReader.getSessionObject(request, "entry");
                if (data == null || data.getId() != RequestReader.getInt(request,"entryId"))
                    return false;
                if (!data.readRequestData(request)) {
                    return showEditEntry(request, response);
                }
                data.prepareSave();
                TeamCalendarBean.getInstance().saveEntryData(data);
                return showCalendar(request, response);
            }
            case deleteEntry: {
                assertCalendarData(request);
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                TeamCalendarBean.getInstance().deleteEntry(RequestReader.getInt(request,"entryId"));
                return showCalendar(request, response);
            }
        }
        return false;
    }

    public static void setCalendarData(HttpServletRequest request){
        TeamCalendarData data = new TeamCalendarData();
        SessionWriter.setSessionObject(request, KEY_CALENDAR, data);
    }

    public static void assertCalendarData(HttpServletRequest request){
        TeamCalendarData data= (TeamCalendarData) SessionReader.getSessionObject(request, KEY_CALENDAR);
        if (data==null){
            data=new TeamCalendarData();
            SessionWriter.setSessionObject(request, KEY_CALENDAR, data);
        }
    }

    protected boolean showCalendar(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/team/calendar.jsp");
    }

    protected boolean showEditEntry(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/team/editCalendarEntry.jsp");
    }
}