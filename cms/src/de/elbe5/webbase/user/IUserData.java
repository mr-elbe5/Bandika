/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webbase.user;

import java.util.Locale;

public interface IUserData {

    int getId();

    String getTitle();

    void setTitle(String title);

    String getName();

    Locale getLocale();

    void setLocale(Locale locale);

    void setLocale(String localeName);

    String getEmail();

    void setEmail(String email);

    String getLogin();

    void setLogin(String login);

    String getPassword();

    void setPassword(String password);

    IUserRightsData getRights();

    boolean checkRights();

}
