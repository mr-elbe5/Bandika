/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.application.ApplicationPath;
import de.elbe5.application.Configuration;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.content.ContentCache;
import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;
import de.elbe5.response.IResponse;
import de.elbe5.servlet.WebServlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class FileServlet extends WebServlet {

    private final File fileDir = new File(ApplicationPath.getAppFilePath());

    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        assert fileDir.exists() || fileDir.mkdir();
    }

    protected void processRequest(String method, HttpServletRequest request, HttpServletResponse response) throws IOException {
        request.setCharacterEncoding(Configuration.ENCODING);
        SessionRequestData rdata = new SessionRequestData(method, request);
        request.setAttribute(SessionRequestData.KEY_REQUESTDATA, rdata);
        rdata.readRequestParams();
        rdata.initSession();
        try {
            String fileName = request.getPathInfo();
            if (fileName == null) {
                Log.error("no file requested");
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            fileName = URLDecoder.decode(fileName, StandardCharsets.UTF_8).substring(1);
            String name = FileUtil.getFileNameWithoutExtension(fileName);
            int id = Integer.parseInt(name);
            FileData data = ContentCache.getFile(id);
            assert(data!=null);
            ContentData parent=ContentCache.getContent(data.getParentId());
            if (!parent.hasUserReadRight(rdata)) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN);
                return;
            }
            File file = new File(fileDir, fileName);
            // if not exists, create from database
            if (!file.exists() && !FileBean.getInstance().createTempFile(file)) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }
            RangeInfo rangeInfo = null;
            String rangeHeader = request.getHeader("Range");
            if (rangeHeader != null) {
                rangeInfo = new RangeInfo(rangeHeader, file.length());
            }
            IResponse result = new FileResponse(file, data.getDisplayFileName(), rangeInfo);
            result.processView(getServletContext(), rdata, response);
        }
        catch (Exception e){
            handleException(request,response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

}