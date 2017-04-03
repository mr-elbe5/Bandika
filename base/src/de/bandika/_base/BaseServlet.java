/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;

public abstract class BaseServlet extends HttpServlet {

  private static final long serialVersionUID = 1L;
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

  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
  }

  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    processRequest(request, response);
  }

  protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    request.setCharacterEncoding("UTF-8");
    RequestData reqData = ensureRequestData(request);
    SessionData sData = ensureSessionData(request);
    adjustRequestData(request, reqData);
    try {
      Controller controller = getController();
      Response respData = controller.doAction(reqData, sData);
      processResponse(respData, request, response);
    } catch (Exception e) {
      handleException(e, response);
    }
  }

  protected RequestData ensureRequestData(HttpServletRequest request) throws ServletException {
    RequestData reqData = RequestHelper.getRequestData(request);
    if (reqData == null) {
      reqData = new RequestData();
      reqData.setPostback(request.getMethod().toLowerCase().equals("post"));
      String type = request.getContentType();
      if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
        reqData.setHasRequestParams(true);
        try {
          RequestHelper.prepareMultipartRequestData(getServletContext(), request, reqData);
        } catch (IOException e) {
          throw new ServletException(e);
        }
      } else
        RequestHelper.prepareSinglepartRequestData(getServletContext(), request, reqData);
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

  protected void adjustRequestData(HttpServletRequest request, RequestData rdata) {
  }

  protected String getBasePath() {
    return getServletContext().getRealPath("/").replace('\\', '/');
  }

  protected void handleException(Exception e, HttpServletResponse response)
    throws ServletException {
    Throwable t = e;
    if (e instanceof ServletException) {
      t = ((ServletException) e).getRootCause();
    }
    if (t instanceof HttpException) {
      try {
        response.sendError(((HttpException) t).getErrorCode(), t.getMessage());
      } catch (IOException ioe) {
        throw new ServletException(ioe);
      }
      return;
    }
    if (t instanceof ServletException) {
      throw (ServletException) t;
    } else {
      throw new ServletException(t);
    }
  }

  protected void processResponse(Response respdata, HttpServletRequest request, HttpServletResponse response)
    throws Exception {
    if (respdata == null) {
      response.sendError(404);
      return;
    }
    if (respdata instanceof BinaryResponse) {
      BinaryResponse brd = (BinaryResponse) respdata;
      OutputStream out = response.getOutputStream();
      if (StringHelper.isNullOrEmtpy(brd.getContentType()))
        brd.setContentType("*/*");
      response.setContentType(brd.getContentType());
      if (brd.getBytes() == null) {
        response.setHeader("Content-Length", "0");
      } else {
        RequestHelper.setNoCache(response);
        StringBuilder buffer = new StringBuilder();
        if (brd.isForceDownload())
          buffer.append("attachment;");
        buffer.append("filename=\"");
        buffer.append(brd.getFileName());
        buffer.append("\"");
        response.setHeader("Content-Disposition", buffer.toString());
        response.setHeader("Content-Length", Integer.toString(brd.getBytes().length));
        out.write(brd.getBytes());
      }
      out.flush();
    } else if (respdata instanceof HtmlResponse) {
      HtmlResponse hrd = (HtmlResponse) respdata;
      OutputStream out = response.getOutputStream();
      response.setContentType("text/html;charset=UTF-8");
      if (StringHelper.isNullOrEmtpy(hrd.getHtml())) {
        response.setHeader("Content-Length", "0");
      } else {
        byte[] bytes = hrd.getHtml().getBytes("UTF-8");
        RequestHelper.setNoCache(response);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        out.write(bytes);
      }
      out.flush();
    } else if (respdata instanceof XmlResponse) {
      XmlResponse xrd = (XmlResponse) respdata;
      OutputStream out = response.getOutputStream();
      response.setContentType("text/xml;charset=UTF-8");
      if (xrd.getXml() == null || xrd.getXml().length() == 0) {
        response.setHeader("Content-Length", "0");
      } else {
        byte[] bytes = xrd.getXml().getBytes("UTF-8");
        RequestHelper.setNoCache(response);
        response.setHeader("Content-Length", Integer.toString(bytes.length));
        out.write(bytes);
      }
      out.flush();
    } else if (respdata instanceof MasterResponse) {
      MasterResponse jrd = (MasterResponse) respdata;
      RequestData rdata = RequestHelper.getRequestData(request);
      rdata.setCurrentJsp(jrd.getJsp());
      rdata.setTitle(jrd.getTitle());
      jrd.setMessages(rdata);
      RequestDispatcher rd = getServletContext().getRequestDispatcher(jrd.getMaster());
      if (rd == null)
        throw new ServletException("master does not exist: " + jrd.getMaster());
      rd.forward(request, response);
    } else if (respdata instanceof PageResponse) {
      PageResponse jrd = (PageResponse) respdata;
      RequestData rdata = RequestHelper.getRequestData(request);
      rdata.setCurrentJsp(jrd.getLayout());
      rdata.setTitle(jrd.getTitle());
      RequestDispatcher rd = getServletContext().getRequestDispatcher(jrd.getMaster());
      if (rd == null)
        throw new ServletException("master does not exist: " + jrd.getMaster());
      rd.forward(request, response);
    } else if (respdata instanceof ForwardResponse) {
      ForwardResponse frd = (ForwardResponse) respdata;
      RequestDispatcher rd = getServletContext().getRequestDispatcher(frd.getUrl());
      rd.forward(request, response);
    }
  }

}