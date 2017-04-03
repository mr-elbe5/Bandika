/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.application.AppConfiguration;
import de.bandika.data.ILoginData;
import de.bandika.data.KeyValueMap;

import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;
import java.util.Locale;

public class SessionData extends KeyValueMap implements HttpSessionBindingListener {

    private ILoginData loginData = null;
    private Locale locale = AppConfiguration.getInstance().getStdLocale();

    public void reset() {
        clear();
        loginData = null;
        locale = AppConfiguration.getInstance().getStdLocale();
    }

    public ILoginData getLoginData() {
        return loginData;
    }

    public String getUserName() {
        return loginData==null ? "" : loginData.getName();
    }

    public void setLoginData(ILoginData loginData) {
        this.loginData = loginData;
    }

    public int getUserId() {
        return loginData == null ? 0 : loginData.getId();
    }

    public boolean isLoggedIn() {
        return loginData != null;
    }

    public boolean hasRight(String type){
        if (loginData == null)
        return false;
        loginData.checkRights();
        return loginData.hasRight(type);
    }

    public boolean hasRight(String type, int right){
        if (loginData == null)
            return false;
        loginData.checkRights();
        return loginData.hasRight(type, right);
    }

    public boolean hasRight(String type, int id, int right) {
        if (loginData == null)
            return false;
        loginData.checkRights();
        return loginData.hasRight(type, id, right);
    }

    public Locale getLocale() {
        return locale==null ? AppConfiguration.getInstance().getStdLocale() : locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public void valueBound(HttpSessionBindingEvent httpSessionBindingEvent) {
        for (Object obj : values()) {
            if (obj instanceof HttpSessionBindingListener)
                ((HttpSessionBindingListener) obj).valueBound(httpSessionBindingEvent);
        }
    }

    public void valueUnbound(HttpSessionBindingEvent httpSessionBindingEvent) {
        for (Object obj : values()) {
            if (obj instanceof HttpSessionBindingListener)
                ((HttpSessionBindingListener) obj).valueUnbound(httpSessionBindingEvent);
        }
    }

}
