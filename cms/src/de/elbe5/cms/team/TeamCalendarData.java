/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.team;

import de.elbe5.base.data.CalendarDateTime;
import de.elbe5.webbase.servlet.RequestReader;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.List;

public class TeamCalendarData extends CalendarDateTime {

    protected int partId;

    protected int entryId=0;
    protected int userId=0;
    protected List<TeamCalendarEntryData> entries = new ArrayList<>();

    public TeamCalendarData(int partId){
        super(Scope.MONTH);
        this.partId=partId;
    }

    public boolean readRequestData(HttpServletRequest request) {
        entryId = RequestReader.getInt(request,"entryId");
        userId = SessionReader.getLoginId(request);
        entries = TeamCalendarBean.getInstance().getEntryList(partId);
        return true;
    }

}