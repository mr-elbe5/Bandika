/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.doccenter;

import de.bandika.cms.pagepart.HtmlPartData;
import de.bandika.cms.template.PartTemplateDataType;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class DocCenterPartData extends HtmlPartData {

    public final static int MODE_LIST = 0;
    public final static int MODE_EDIT = 1;
    public final static int MODE_DELETE = 2;
    public final static int MODE_HISTORY = 3;
    public final static int MODE_HISTORY_DELETE = 4;

    public DocCenterPartData(){
    }

    @Override
    public PartTemplateDataType getDataType(){
        return PartTemplateDataType.DOCCENTER;
    }

    @Override
    public boolean executePagePartMethod(String method, HttpServletRequest request, HttpServletResponse response) throws Exception{
        return DocCenterAction.defaultAction.execute(request, response);
    }

}