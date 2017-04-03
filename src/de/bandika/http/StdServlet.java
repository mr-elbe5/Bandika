/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.http;

import de.bandika.base.Controller;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletException;
import java.io.IOException;
import java.io.OutputStream;

/**
 * Class StdServlet is the servlet class for receiving requests and returning responses for non-html output. <br>
 * Usage:
 */
public class StdServlet extends BaseServlet {

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		super.doPost(request,response);
    RequestData reqData = HttpHelper.getRequestData(request);
		SessionData sData = HttpHelper.getSessionData(request);
    try {
      String ctrlKey = reqData.getParamString("ctrl");
      Controller controller = Controller.getController(ctrlKey);
      Response respData = controller.doAction(reqData, sData);
      processResponse(respData, response);
    }
    catch (Exception e) {
      handleException(e, this, request, response);
    }
  }

  protected void processResponse(Response respdata, HttpServletResponse response) throws Exception {
		if (respdata==null){
			response.sendError(404);
		  return;
		}
    switch (respdata.getType()) {
      case Response.TYPE_BINARY: {
        BinaryResponse brd = (BinaryResponse) respdata;
        OutputStream out = response.getOutputStream();
        if (brd.getContentType() == null || brd.getContentType().length() == 0)
          brd.setContentType("*/*");
        response.setContentType(brd.getContentType());
        if (brd.getBytes() == null) {
          response.setHeader("Content-Length", "0");
        } else {
          HttpHelper.setNoCache(response);
          StringBuffer buffer = new StringBuffer("filename=\"");
          buffer.append(brd.getFileName());
          buffer.append("\"");
          response.setHeader("Content-Disposition", buffer.toString());
          response.setHeader("Content-Length", Integer.toString(brd.getBytes().length));
          out.write(brd.getBytes());
        }
        out.flush();
      }
      break;
      case Response.TYPE_HTML: {
        HtmlResponse hrd = (HtmlResponse) respdata;
        OutputStream out = response.getOutputStream();
        response.setContentType("text/html; charset=" + HttpHelper.ISOCODE);
        if (hrd.getHtml() == null || hrd.getHtml().length() == 0) {
          response.setHeader("Content-Length", "0");
        } else {
          byte[] bytes = hrd.getHtml().getBytes(HttpHelper.ISOCODE);
          HttpHelper.setNoCache(response);
          response.setHeader("Content-Length", Integer.toString(bytes.length));
          out.write(bytes);
        }
        out.flush();
      }
      break;
    }
  }

}
