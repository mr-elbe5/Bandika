/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.rights.Right;
import de.bandika.rights.RightsCache;
import de.bandika.rights.SystemZone;
import de.bandika.user.UserRightsData;

import javax.servlet.http.HttpServletRequest;

public class RightsReader {

    public static UserRightsData getSessionRightsData(HttpServletRequest request) {
        UserRightsData rightsData = (UserRightsData) SessionReader.getSessionObject(request, RequestStatics.KEY_RIGHTS);
        UserRightsData newRightsData;
        if (rightsData == null) {
            int userId = SessionReader.getLoginId(request);
            if (userId == 0)
                return null;
            newRightsData = new UserRightsData();
            newRightsData.setUserId(userId);
            newRightsData = RightsCache.getInstance().checkRights(newRightsData);
        } else
            newRightsData = RightsCache.getInstance().checkRights(rightsData);
        if (newRightsData != null) {
            RightsWriter.setSessionRightsData(request, newRightsData);
            return newRightsData;
        }
        return rightsData;
    }

    public static boolean hasAnySystemRight(HttpServletRequest request) {
        UserRightsData rightsData = getSessionRightsData(request);
        return rightsData != null && rightsData.hasAnySystemRight();
    }

    public static boolean hasAnyElevatedSystemRight(HttpServletRequest request) {
        UserRightsData rightsData = getSessionRightsData(request);
        return rightsData != null && rightsData.hasAnyElevatedSystemRight();
    }

    public static boolean hasAnyContentRight(HttpServletRequest request) {
        UserRightsData rightsData = getSessionRightsData(request);
        return rightsData != null && rightsData.hasAnyContentRight();
    }

    public static boolean hasSystemRight(HttpServletRequest request, SystemZone zone, Right right) {
        UserRightsData rightsData = getSessionRightsData(request);
        return rightsData != null && rightsData.hasSystemRight(zone, right);
    }

    public static boolean hasContentRight(HttpServletRequest request, int id, Right right) {
        UserRightsData rightsData = getSessionRightsData(request);
        return rightsData != null && rightsData.hasContentRight(id, right);
    }
}
