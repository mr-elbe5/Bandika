/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.bandika;

import de.bandika.application.*;
import de.bandika.data.Log;
import de.bandika.servlet.BaseServlet;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestHelper;
import de.bandika.servlet.SessionData;
import de.bandika.sql.DbHandler;
import de.bandika.sql.DbInitFormBuilder;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

@MultipartConfig(fileSizeThreshold=1024*1024*20,maxFileSize=1024*1024*20, maxRequestSize=1024*1024*20*5)
public class BandikaServlet extends BaseServlet {

    protected RequestData getNewRequestData() {
        return new RequestData();
    }

    protected SessionData getNewSessionData() {
        return new SessionData();
    }

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        Log.log("initialzing bandika...");
        super.init(servletConfig);
        if (!DbHandler.getInstance().isInitialized()) {
            if (!DbHandler.getInstance().initialize(WebAppPath.getAppPath())){
                Log.log("database needs to be initialized");
                return;
            }
        }
        BandikaInitializer.init();
        Log.log("bandika initialized");
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(AppConfiguration.getInstance().getEncoding());
        if (!DbHandler.getInstance().isInitialized()) {
            sendHtml(response, DbInitFormBuilder.getCONFIGHTML(DbInitFormBuilder.POSTGRES_CLASS,DbInitFormBuilder.POSTGRES_URL));
            return;
        }
        RequestData rdata = createRequestData(request);
        rdata.setPostback(false);
        SessionData sdata = ensureSessionData(request);
        processRequest(rdata, sdata, request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        request.setCharacterEncoding(AppConfiguration.getInstance().getEncoding());
        RequestData rdata = createRequestData(request);
        rdata.setPostback(true);
        SessionData sdata = ensureSessionData(request);
        if (!DbHandler.getInstance().isInitialized()) {
            String dbClass = rdata.getString("dbClass");
            String dbUrl = rdata.getString("dbUrl");
            String dbUser = rdata.getString("dbUser");
            String dbPwd = rdata.getString("dbPwd");
            DbHandler.getInstance().setProperties(WebAppPath.getAppPath(), dbClass, dbUrl, dbUser, dbPwd);
            if (DbHandler.getInstance().initialize(WebAppPath.getAppPath())){
                BandikaInitializer.init();
                sendHtml(response, DbInitFormBuilder.getSAVEDHTML());
            }
            else
                sendHtml(response, DbInitFormBuilder.getCONFIGHTML(dbClass,dbUrl));
        }
        else
            processRequest(rdata, sdata, request, response);
    }

    private void sendHtml(HttpServletResponse response, String html) throws IOException {
        OutputStream out = response.getOutputStream();
        response.setContentType(MessageFormat.format("text/html;charset={0}", AppConfiguration.getInstance().getEncoding()));
        byte[] bytes = html.getBytes(AppConfiguration.getInstance().getEncoding());
        RequestHelper.setNoCache(response);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        out.write(bytes);
        out.flush();
    }


}