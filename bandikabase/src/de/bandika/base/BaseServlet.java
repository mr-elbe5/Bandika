/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.data.RequestData;
import de.bandika.data.SessionData;
import de.bandika.response.*;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseServlet extends HttpServlet {

	public static final String REQUEST_DATA = "reqdata";
  public static final String RESPONSE_DATA = "respdata";
  public static final String SESSION_DATA = "sdata";

  protected RequestData getNewRequestData() {
    return new RequestData();
  }

  protected SessionData getNewSessionData() {
    return new SessionData();
  }

  protected abstract Controller getController();
  protected abstract BaseAppConfig getBaseConfig();

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    ensureBaseConfig(request);
    RequestData reqData = ensureRequestData(request);
		SessionData sData = ensureSessionData(request);
    if (reqData == null) {
      reqData = getNewRequestData();
      String type = request.getContentType();
      if (type != null && type.toLowerCase().startsWith("multipart/form-data"))
        try {
          RequestHelper.prepareMultipartRequestData(request, reqData);
        }
        catch (IOException e) {
          throw new ServletException(e);
        }
      else
        RequestHelper.prepareSinglepartRequestData(request, reqData);
      request.setAttribute(REQUEST_DATA, reqData);
    }
    try {
      Controller controller = getController();
      Response respData = controller.doAction(reqData, sData);
      processResponse(respData, request, response);
    }
    catch (Exception e) {
      handleException(e, this, request, response);
    }
  }

  protected void ensureBaseConfig(HttpServletRequest request) throws ServletException{
		if (!getBaseConfig().initialized()) {
			try {
        getBaseConfig().initialize(request, getBasePath());
			}
			catch (Exception e) {
				throw new ServletException("could not initialize", e);
			}
		}
	}

  protected RequestData ensureRequestData(HttpServletRequest request) throws ServletException {
		RequestData reqData = RequestHelper.getRequestData(request);
		if (reqData == null) {
			reqData = new RequestData();
      reqData.setPostback(request.getMethod().toLowerCase().equals("post"));
      String type = request.getContentType();
			if (type != null && type.toLowerCase().startsWith("multipart/form-data")){
			  reqData.setHasRequestParams(true);
				try {
					RequestHelper.prepareMultipartRequestData(request, reqData);
				}
				catch (IOException e) {
					throw new ServletException(e);
				}
			}
			else
				RequestHelper.prepareSinglepartRequestData(request, reqData);
			request.setAttribute(REQUEST_DATA, reqData);
		}
		return reqData;
	}

	protected SessionData ensureSessionData(HttpServletRequest request) throws ServletException {
		HttpSession session = request.getSession(true);
		SessionData sData = (SessionData) session.getAttribute(SESSION_DATA);
		if (sData == null) {
			sData = new SessionData();
			sData.setSession(session);
			session.setAttribute(SESSION_DATA, sData);
		}
		return sData;
	}

  protected String getBasePath(){
    return getServletContext().getRealPath("/");
  }

  protected void handleException(Exception e, HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws ServletException {
    Throwable t = e;
    e.printStackTrace();
    if (e instanceof ServletException) {
      t = ((ServletException) e).getRootCause();
    }
    if (t instanceof HttpException) {
      try {
        response.sendError(((HttpException) t).getErrorCode(), t.getMessage());
      }
      catch (IOException ioe) {
        throw new ServletException(ioe);
      }
    } else if (t instanceof RightException) {
      try {
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher("/_jsp/noaccess.jsp");
        if (response.getContentType() != null)
          rd.include(request, response);
        else
          rd.forward(request, response);
      }
      catch (IOException ioe) {
        throw new ServletException(ioe);
      }
    } else {
      try {
        request.setAttribute("exception", t);
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher("/_jsp/error.jsp");
        if (response.getContentType() != null)
          rd.include(request, response);
        else
          rd.forward(request, response);
      }
      catch (IOException ioe) {
        throw new ServletException(ioe);
      }
    }
  }

  protected void processResponse(Response respdata, HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (respdata==null){
			response.sendError(404);
		  return;
		}
    Class cls=respdata.getClass();
    if (cls.equals(BinaryResponse.class))
    {
      BinaryResponse brd = (BinaryResponse) respdata;
      OutputStream out = response.getOutputStream();
      if (brd.getContentType() == null || brd.getContentType().length() == 0)
        brd.setContentType("*/*");
      response.setContentType(brd.getContentType());
      if (brd.getBytes() == null) {
        response.setHeader("Content-Length", "0");
      } else {
        RequestHelper.setNoCache(response);
        StringBuffer buffer = new StringBuffer("filename=\"");
        buffer.append(brd.getFileName());
        buffer.append("\"");
        response.setHeader("Content-Disposition", buffer.toString());
        response.setHeader("Content-Length", Integer.toString(brd.getBytes().length));
        out.write(brd.getBytes());
      }
      out.flush();
    }
    else if (cls.equals(HtmlResponse.class))
    {
      HtmlResponse hrd = (HtmlResponse) respdata;
      OutputStream out = response.getOutputStream();
      response.setContentType("text/html; charset=" + RequestHelper.ISOCODE);
      if (hrd.getHtml() == null || hrd.getHtml().length() == 0) {
        response.setHeader("Content-Length", "0");
      } else {
        byte[] bytes = hrd.getHtml().getBytes(RequestHelper.ISOCODE);
        RequestHelper.setNoCache(response);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        out.write(bytes);
      }
      out.flush();
    }
    else if (cls.equals(MsgResponse.class))
    {
      MsgResponse erd = (MsgResponse) respdata;
      RequestData rdata= RequestHelper.getRequestData(request);
      rdata.setCurrentJsp(erd.getJsp());
      RequestDispatcher rd = getServletContext().getRequestDispatcher(erd.getMaster());
      if (rd == null)
        throw new ServletException("jsp does not exist: " + erd.getMaster());
      rd.forward(request, response);
    }
    else if (cls.equals(XmlResponse.class))
    {
      XmlResponse xrd = (XmlResponse) respdata;
      OutputStream out = response.getOutputStream();
      response.setContentType("text/xml; charset=" + RequestHelper.ISOCODE);
      if (xrd.getXml() == null || xrd.getXml().length() == 0) {
        response.setHeader("Content-Length", "0");
      } else {
        byte[] bytes = xrd.getXml().getBytes(RequestHelper.ISOCODE);
        RequestHelper.setNoCache(response);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        out.write(bytes);
      }
      out.flush();
    }
    else if (cls.equals(JspResponse.class))
    {
      JspResponse jrd = (JspResponse) respdata;
      RequestData rdata= RequestHelper.getRequestData(request);
      rdata.setCurrentJsp(jrd.getJsp());
      RequestDispatcher rd = getServletContext().getRequestDispatcher(jrd.getMaster());
      if (rd == null)
        throw new ServletException("jsp does not exist: " + jrd.getMaster());
      rd.forward(request, response);
    }
    else if (cls.equals(ForwardResponse.class))
    {
      ForwardResponse frd = (ForwardResponse) respdata;
      RequestDispatcher rd = getServletContext().getRequestDispatcher(frd.getUrl());
      rd.forward(request, response);
    }
  }

}