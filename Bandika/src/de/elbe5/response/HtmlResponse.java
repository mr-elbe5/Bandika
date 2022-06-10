/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.response;

import de.elbe5.application.Configuration;
import de.elbe5.request.RequestData;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class HtmlResponse implements IResponse {

    protected StringBuilder sb = new StringBuilder();

    public HtmlResponse() {
    }

    public HtmlResponse(String html) {
        sb.append(html);
    }

    @Override
    public void processResponse(ServletContext context, RequestData rdata, HttpServletResponse response)  {
        sendHtml(response);
    }

    public void sendHtml(HttpServletResponse response){
        Document doc = Jsoup.parse(sb.toString());
        String html = doc.toString();
        try {
            ServletOutputStream out = response.getOutputStream();
            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
            response.setHeader("Cache-Control", "no-cache");
            response.setHeader("Pragma", "no-cache");
            response.setHeader("Content-Type", "text/html");
            if (html.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = html.getBytes(Configuration.ENCODING);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
            response.setStatus(HttpServletResponse.SC_OK);
        }
        catch (IOException e){
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }

}
