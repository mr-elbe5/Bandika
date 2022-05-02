/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.request.RequestData;
import de.elbe5.request.RequestKeys;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CloseDialogResponse extends ServerPageResponse {

    private String targetId = "";

    public CloseDialogResponse(String path) {
        super(path);
    }

    public CloseDialogResponse(String path, String targetId) {
        super(path);
        this.targetId = targetId;
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        rdata.getAttributes().put(RequestKeys.KEY_URL, path);
        if (!targetId.isEmpty())
            rdata.getAttributes().put(RequestKeys.KEY_TARGETID, targetId);
        path = "closeDialog";
        super.processResponse(context, rdata, response);
    }
}
