/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2019 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.templatepage.templatepagepart;

import de.elbe5.request.IRequestData;

public abstract class Field implements IRequestData, Cloneable {

    protected int pagePartId = 0;
    protected String name = "";
    protected String content = "";

    @Override
    public Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public void setPagePartId(int pagePartId) {
        this.pagePartId = pagePartId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIdentifier() {
        return Integer.toString(pagePartId) + '_' + name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public abstract String getFieldType();

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
    }

}
