/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.user;

import java.util.Locale;

public interface IUserData {

    public static final int ID_SYSTEM = 1;

    public int getId();

    public String getTitle();

    public void setTitle(String title);

    public String getName();

    public Locale getLocale();

    public void setLocale(Locale locale);

    public void setLocale(String localeName);

    public String getEmail();

    public void setEmail(String email);

    public String getLogin();

    public void setLogin(String login);

    public String getPassword();

    public void setPassword(String password);

    public IUserRightsData getRights();

    public boolean checkRights();

}
