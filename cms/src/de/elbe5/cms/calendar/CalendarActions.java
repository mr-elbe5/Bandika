/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.calendar;

import de.elbe5.cms.blog.BlogBean;
import de.elbe5.cms.servlet.CmsActions;
import de.elbe5.webbase.rights.Right;
import de.elbe5.webbase.rights.SystemZone;
import de.elbe5.webbase.servlet.ActionSetCache;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;
import de.elbe5.webbase.servlet.SessionWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class CalendarActions extends CmsActions {

    public static final String showCalendar="showCalendar";
    public static final String openCreateEntry="openCreateEntry";
    public static final String openEditEntry="openEditEntry";
    public static final String saveEntry="saveEntry";
    public static final String deleteEntry="deleteEntry";

    public static final String KEY = "calendar";

    public static final String KEY_CALENDAR = "$CALENDAR";

    public static void initialize() {
        ActionSetCache.addActionSet(KEY, new CalendarActions());
    }

    public String getKey(){
        return KEY;
    }

    public boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception {
        switch (actionName) {
            case showCalendar: {
                int partId=RequestReader.getInt(request,"partId");
                setCalendarData(request, partId);
                return showCalendar(request, response);
            }
            case openCreateEntry: {
                int partId=RequestReader.getInt(request,"partId");
                assertCalendarData(request, partId);
                CalendarEntryData data = new CalendarEntryData();
                data.setPartId(partId);
                data.setId(BlogBean.getInstance().getNextId());
                data.setNew(true);
                SessionWriter.setSessionObject(request, "entry", data);
                return showEditEntry(request, response);
            }
            case openEditEntry: {
                int partId=RequestReader.getInt(request,"partId");
                assertCalendarData(request, partId);
                int id = RequestReader.getInt(request,"entryId");
                CalendarEntryData data = CalendarBean.getInstance().getEntryData(id);
                SessionWriter.setSessionObject(request, "entry", data);
                return showEditEntry(request, response);
            }
            case saveEntry: {
                int partId=RequestReader.getInt(request,"partId");
                assertCalendarData(request, partId);
                CalendarEntryData data = (CalendarEntryData) SessionReader.getSessionObject(request, "entry");
                if (data == null || data.getId() != RequestReader.getInt(request,"entryId"))
                    return false;
                if (!data.readRequestData(request)) {
                    return showEditEntry(request, response);
                }
                data.prepareSave();
                CalendarBean.getInstance().saveEntryData(data);
                return showCalendar(request, response);
            }
            case deleteEntry: {
                int partId=RequestReader.getInt(request,"partId");
                assertCalendarData(request, partId);
                if (!hasSystemRight(request, SystemZone.CONTENT, Right.EDIT))
                    return false;
                CalendarBean.getInstance().deleteEntry(RequestReader.getInt(request,"entryId"));
                return showCalendar(request, response);
            }
        }
        return false;
    }

    public static void setCalendarData(HttpServletRequest request, int partId){
        CalendarData data = new CalendarData(partId);
        SessionWriter.setSessionObject(request, KEY_CALENDAR+partId, data);
    }

    public static void assertCalendarData(HttpServletRequest request, int partId){
        CalendarData data= (CalendarData) SessionReader.getSessionObject(request, KEY_CALENDAR+partId);
        if (data==null){
            data=new CalendarData(partId);
            SessionWriter.setSessionObject(request, KEY_CALENDAR+partId, data);
        }
    }

    protected boolean showCalendar(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/calendar/calendar.jsp");
    }

    protected boolean showEditEntry(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/calendar/editCalendarEntry.jsp");
    }
}