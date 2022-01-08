/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.request;

import de.elbe5.content.ContentData;
import javax.servlet.http.HttpServletRequest;

public class ContentSessionRequestData extends SessionRequestData {

    public ContentSessionRequestData(String method, HttpServletRequest request) {
        super(method, request);
    }

    public void setCurrentRequestContent(ContentData data) {
        //Log.log("set current request content: " + data.getClass().getSimpleName());
        setRequestObject(KEY_CONTENT, data);
    }

    public ContentData getCurrentContent() {
        return getCurrentContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentContent(Class<T> cls) {
        try {
            Object obj=getRequestObject(KEY_CONTENT);
            if (obj==null)
                obj=getSessionObject(KEY_CONTENT);
            assert(obj!=null);
            //Log.log("current request content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException e){
            return null;
        }
    }

    public void setCurrentSessionContent(ContentData data) {
        //Log.log("set current session content: " + data.getClass().getSimpleName());
        setSessionObject(KEY_CONTENT, data);
    }

    public void removeCurrentSessionContent() {
        removeSessionObject(KEY_CONTENT);
    }

    public ContentData getCurrentSessionContent() {
        return getCurrentSessionContent(ContentData.class);
    }

    public <T extends ContentData> T getCurrentSessionContent(Class<T> cls) {
        try {
            Object obj=getSessionObject(KEY_CONTENT);
            //Log.log("current session content is: " + obj.getClass().getSimpleName());
            return cls.cast(obj);
        }
        catch (ClassCastException e){
            return null;
        }
    }

}
