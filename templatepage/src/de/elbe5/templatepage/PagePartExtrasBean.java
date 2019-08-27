/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage;

import de.elbe5.database.DbBean;

import java.sql.Connection;
import java.sql.SQLException;

public abstract class PagePartExtrasBean extends DbBean {

    public abstract void readPagePartExtras(Connection con, PagePartData partData) throws SQLException;

    public abstract void writePagePartExtras(Connection con, PagePartData partData) throws Exception;

}
