/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.request.ContentRequestKeys;
import de.elbe5.request.RequestData;
import de.elbe5.response.MasterResponse;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ContentResponse extends MasterResponse {

    public ContentResponse(ContentData data) {
        super(DEFAULT_MASTER, data);
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        rdata.setRequestObject(ContentRequestKeys.KEY_CONTENT, includeObject);
        includeObject.prepareMaster(rdata);
        super.processResponse(context, rdata, response);
    }
}
