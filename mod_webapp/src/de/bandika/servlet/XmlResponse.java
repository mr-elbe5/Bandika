/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.application.AppConfiguration;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.OutputStream;
import java.text.MessageFormat;

public class XmlResponse extends Response {

    protected String xml;

    public XmlResponse() {
    }

    public XmlResponse(String xml) {
        this.xml = xml;
    }

    public String getXml() {
        return xml;
    }

    public void setXml(String xml) {
        this.xml = xml;
    }

    @Override
    public void processResponse(HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws Exception {
        OutputStream out = response.getOutputStream();
        response.setContentType(MessageFormat.format("text/xml;charset={0}", AppConfiguration.getInstance().getEncoding()));
        if (getXml() == null || getXml().length() == 0) {
            response.setHeader("Content-Length", "0");
        } else {
            byte[] bytes = getXml().getBytes(AppConfiguration.getInstance().getEncoding());
            RequestHelper.setNoCache(response);
            response.setHeader("Content-Length", Integer.toString(bytes.length));
            out.write(bytes);
        }
        out.flush();
    }
}
