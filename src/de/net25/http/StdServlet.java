/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.http;

import de.net25.base.resources.FileData;
import de.net25.base.exception.HttpException;
import de.net25.base.exception.RightException;
import de.net25.base.controller.*;
import de.net25.resources.statics.Statics;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.ServletException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletInputStream;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Enumeration;

/**
 * Class StdServlet is the only servlet class for receiving requests and returning responses. <br>
 * Usage:
 */
public class StdServlet extends HttpServlet {

  public static final String REQUEST_DATA = "reqdata";
  public static final String RESPONSE_DATA = "respdata";
  public static final String SESSION_DATA = "sdata";  

  /**
   * Method getNewRequestData returns the newRequestData of this StdServlet object.
   *
   * @return the newRequestData (type RequestData) of this StdServlet object.
   */
  protected RequestData getNewRequestData() {
    return new RequestData();
  }

  /**
   * Method getNewSessionData returns the newSessionData of this StdServlet object.
   *
   * @return the newSessionData (type SessionData) of this StdServlet object.
   */
  protected SessionData getNewSessionData() {
    return new SessionData();
  }

  /**
   * Method init
   *
   * @param servletConfig of type ServletConfig
   * @throws ServletException when data processing is not successful
   */
  @Override
  public void init(ServletConfig servletConfig) throws ServletException {
    super.init(servletConfig);
  }

  /**
   * Method doGet
   *
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws ServletException when data processing is not successful
   * @throws IOException      when data processing is not successful
   */
  @Override
  protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    doPost(request, response);
  }

  /**
   * Method doPost
   *
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws ServletException when data processing is not successful
   * @throws IOException      when data processing is not successful
   */
  @Override
  protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    RequestData reqData = (RequestData) request.getAttribute(REQUEST_DATA);
    if (!Statics.isInitialized()){
      String appName="";
      try{
        String uri=request.getRequestURI();
        appName=uri.substring(1,uri.indexOf('/',2));
        Statics.init(appName);
      }
      catch (Exception e){
        throw new ServletException("could not initialize with app name "+appName,e);
      }
    }
    if (reqData == null) {
      reqData = getNewRequestData();
      String type = request.getContentType();
      if (type != null && type.toLowerCase().startsWith("multipart/form-data"))
        try {
          prepareMultipartRequestData(request, reqData);
        }
        catch (IOException e) {
          throw new ServletException(e);
        }
      else
        prepareSinglepartRequestData(request, reqData);
      request.setAttribute(REQUEST_DATA, reqData);
    }
    HttpSession session = request.getSession(true);
    SessionData sData = (SessionData) session.getAttribute(SESSION_DATA);
    if (sData == null) {
      sData = getNewSessionData();
      sData.setLocale(Statics.getBestLocale(request.getLocale()));
      sData.setSession(session);
      session.setAttribute(SESSION_DATA, sData);
    }
    try {
      String ctrlKey = reqData.getParamString("ctrl");
      Controller controller = Statics.getController(ctrlKey);
      Response respData = controller.doAction(reqData, sData);
      if (respData.isDirectResponse())
        processResponse(respData, request, response);
      else {
        if (respData.isSticky())
          request.setAttribute(RESPONSE_DATA, respData);
        processResponse(respData, request, response);
      }
    }
    catch (Exception e) {
      handleException(e, this, request, response);
    }
  }

  /**
   * Method processResponse
   *
   * @param respdata of type Response
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws Exception when data processing is not successful
   */
  protected void processResponse(Response respdata, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
          Statics.setNoCache(response);
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
        response.setContentType("text/html; charset=" + Statics.ISOCODE);
        if (hrd.getHtml() == null || hrd.getHtml().length() == 0) {
          response.setHeader("Content-Length", "0");
        } else {
          byte[] bytes = hrd.getHtml().getBytes(Statics.ISOCODE);
          Statics.setNoCache(response);
          response.setHeader("Content-Length", Integer.toString(bytes.length));
          out.write(bytes);
        }
        out.flush();
      }
      break;
      case Response.TYPE_FORWARD:
      case Response.TYPE_PAGE: {
        ForwardResponse frd = (ForwardResponse) respdata;
        RequestDispatcher rd = getServletContext().getRequestDispatcher(frd.getUrl());
        if (rd == null)
          throw new ServletException("url does not exist: " + frd.getUrl());
        rd.forward(request, response);
      }
      break;
      case Response.TYPE_INCLUDE: {
        IncludeResponse ird = (IncludeResponse) respdata;
        RequestDispatcher rd = getServletContext().getRequestDispatcher(ird.getUrl());
        if (rd == null)
          throw new ServletException("url does not exist: " + ird.getUrl());
        rd.include(request, response);
      }
      break;
      case Response.TYPE_XML: {
        XmlResponse xrd = (XmlResponse) respdata;
        OutputStream out = response.getOutputStream();
        response.setContentType("text/xml; charset=" + Statics.ISOCODE);
        if (xrd.getXml() == null || xrd.getXml().length() == 0) {
          response.setHeader("Content-Length", "0");
        } else {
          byte[] bytes = xrd.getXml().getBytes(Statics.ISOCODE);
          Statics.setNoCache(response);
          response.setHeader("Content-Length", Integer.toString(bytes.length));
          out.write(bytes);
        }
        out.flush();
      }
      break;
    }
  }

  /**
   * Method handleException
   *
   * @param e        of type Exception
   * @param servlet  of type HttpServlet
   * @param request  of type HttpServletRequest
   * @param response of type HttpServletResponse
   * @throws ServletException when data processing is not successful
   */
  public void handleException(Exception e, HttpServlet servlet, HttpServletRequest request, HttpServletResponse response) throws ServletException {
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
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher("/jsps/noaccess.jsp");
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
        RequestDispatcher rd = servlet.getServletContext().getRequestDispatcher("/jsps/error.jsp");
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

  //************************* singlepart request *****************************/

  /**
   * Method prepareSinglepartRequestData
   *
   * @param request of type HttpServletRequest
   * @param rdata   of type RequestData
   * @throws ServletException when data processing is not successful
   */
  protected void prepareSinglepartRequestData(HttpServletRequest request, RequestData rdata) throws ServletException {
    rdata.setRequest(request);
    Enumeration enm = request.getParameterNames();
    while (enm.hasMoreElements()) {
      String key = (String) enm.nextElement();
      String[] strings = request.getParameterValues(key);
      if (strings.length == 1)
        rdata.setParam(key, strings[0]);
      else {
        StringBuffer buffer = new StringBuffer(strings[0]);
        for (int i = 1; i < strings.length; i++) {
          buffer.append(",");
          buffer.append(strings[i]);
        }
        rdata.setParam(key, buffer.toString());
      }
    }
  }

  //************************* multipart request *****************************/

  /**
   * Method prepareMultipartRequestData
   *
   * @param request of type HttpServletRequest
   * @param rdata   of type RequestData
   * @throws IOException when data processing is not successful
   */
  private void prepareMultipartRequestData(HttpServletRequest request, RequestData rdata) throws IOException {
    ServletInputStream in = request.getInputStream();
    String type = request.getContentType();
    int contentLength = request.getContentLength();
    int doneLength = 0;
    int idx = type.indexOf("boundary=");
    if (idx == -1)
      throw new IOException("Separation boundary was not specified");
    String boundary = "--" + type.substring(idx + 9);
    String line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return;
    }
    if (!line.startsWith(boundary)) {
      throw new IOException("Corrupt forms data: no leading boundary");
    }
    boolean done = false;
    while (!done) {
      done = extractNextPart(request, in, boundary, contentLength, doneLength, rdata);
    }
  }

  /**
   * Method readLine
   *
   * @param in            of type ServletInputStream
   * @param contentLength of type int
   * @param doneLength    of type int
   * @return String
   * @throws IOException when data processing is not successful
   */
  private String readLine(ServletInputStream in, int contentLength, int doneLength) throws IOException {
    byte[] buf = new byte[8 * 1024];
    StringBuffer sbuf = new StringBuffer();
    int result;
    do {
      if (doneLength >= contentLength) {
        result = -1;
      } else {
        result = in.readLine(buf, 0, buf.length);
        if (result > 0) {
          doneLength += result;
        }
      }
      if (result != -1)
        sbuf.append(new String(buf, 0, result, Statics.ISOCODE));
    } while (result == buf.length);
    if (sbuf.length() == 0) {
      return null;
    } else {
      if (sbuf.length() > 0) {
        if (sbuf.charAt(sbuf.length() - 1) == '\n') {
          sbuf.setLength(sbuf.length() - 1);
          if (sbuf.length() > 0) {
            if (sbuf.charAt(sbuf.length() - 1) == '\r') {
              sbuf.setLength(sbuf.length() - 1);
            }
          }
        }
      }
    }
    return sbuf.toString();
  }

  /**
   * Method extractNextPart
   *
   * @param request       of type HttpServletRequest
   * @param in            of type ServletInputStream
   * @param boundary      of type String
   * @param contentLength of type int
   * @param doneLength    of type int
   * @param rdata         of type RequestData
   * @return boolean
   * @throws IOException when data processing is not successful
   */
  private boolean extractNextPart(HttpServletRequest request, ServletInputStream in, String boundary, int contentLength, int doneLength, RequestData rdata) throws IOException {
    String line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return true;
    }
    String[] dispInfo = extractDispositionInfo(line);
    String name = dispInfo[1];
    String dataname = dispInfo[2];
    line = readLine(in, contentLength, doneLength);
    if (line == null) {
      return true;
    }
    String contentType = extractContentType(line);
    if (contentType != null) {
      line = readLine(in, contentLength, doneLength);
      if (line == null || line.length() > 0) {
        throw new IOException("Malformed line after content type: " + line);
      }
    } else {
      contentType = "application/octet-stream";
    }
    if (dataname == null) {
      String value = extractSingleParameter(in, boundary, contentLength, doneLength);
      String oldValue = (String) request.getAttribute(name);
      if (oldValue != null && !oldValue.equals(value)) {
        StringBuffer buffer = new StringBuffer();
        buffer.append(oldValue);
        buffer.append(",");
        buffer.append(value);
        rdata.setParam(name, buffer.toString());
      } else
        rdata.setParam(name, value);
    } else {
      byte[] bytes = extractSingleData(in, boundary);
      if (bytes.length > 0) {
        FileData data = new FileData();
        data.setName(dataname);
        data.setBytes(bytes);
        data.setContentType(contentType);
        rdata.setParam(name, data);
      }
    }
    return false;
  }

  /**
   * Method extractDispositionInfo
   *
   * @param line of type String
   * @return String[]
   * @throws IOException when data processing is not successful
   */
  private String[] extractDispositionInfo(String line) throws IOException {
    String[] retval = new String[4];

    String origline = line;
    line = origline.toLowerCase();
    int start = line.indexOf("content-disposition: ");
    int end = line.indexOf(";");
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String disposition = line.substring(start + 21, end);
    if (!disposition.equals("form-data")) {
      throw new IOException("Invalid content disposition: " + disposition);
    }
    start = line.indexOf("name=\"", end);
    end = line.indexOf("\"", start + 7);
    if (start == -1 || end == -1) {
      throw new IOException("Content disposition corrupt: " + origline);
    }
    String name = origline.substring(start + 6, end);

    String filename = null;
    start = line.indexOf("filename=\"", end + 2);
    end = line.indexOf("\"", start + 10);
    if (start != -1 && end != -1) {
      filename = origline.substring(start + 10, end);
      retval[3] = filename;
      int slash =
          Math.max(filename.lastIndexOf('/'), filename.lastIndexOf('\\'));
      if (slash > -1) {
        filename = filename.substring(slash + 1);
      }
      if (filename.equals("")) filename = "unknown";
    }

    retval[0] = disposition;
    retval[1] = name;
    retval[2] = filename;
    return retval;
  }

  /**
   * Method extractContentType
   *
   * @param line of type String
   * @return String
   * @throws IOException when data processing is not successful
   */
  private String extractContentType(String line) throws IOException {
    String contentType = null;
    String origline = line;
    line = origline.toLowerCase();

    if (line.startsWith("content-type")) {
      int start = line.indexOf(" ");
      if (start == -1) {
        throw new IOException("Content type corrupt: " + origline);
      }
      contentType = line.substring(start + 1);
    } else if (line.length() != 0) {
      throw new IOException("Malformed line after disposition: " + origline);
    }
    return contentType;
  }

  /**
   * Method extractSingleParameter
   *
   * @param in            of type ServletInputStream
   * @param boundary      of type String
   * @param contentLength of type int
   * @param doneLength    of type int
   * @return String
   * @throws IOException when data processing is not successful
   */
  private String extractSingleParameter(ServletInputStream in, String boundary, int contentLength, int doneLength) throws IOException {
    StringBuffer sbuf = new StringBuffer();
    String line;
    while ((line = readLine(in, contentLength, doneLength)) != null) {
      if (line.startsWith(boundary)) break;
      sbuf.append(line).append("\r\n");
    }

    if (sbuf.length() == 0) {
      return null;
    }

    sbuf.setLength(sbuf.length() - 2);
    return sbuf.toString();
  }

  /**
   * Method extractSingleData
   *
   * @param in       of type ServletInputStream
   * @param boundary of type String
   * @return byte[]
   * @throws IOException when data processing is not successful
   */
  private byte[] extractSingleData(ServletInputStream in, String boundary) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(8 * 1024);
    byte[] bbuf = new byte[8 * 1024];
    int result;
    String line;
    boolean rnflag = false;
    while ((result = in.readLine(bbuf, 0, bbuf.length)) != -1) {
      if (result > 2 && bbuf[0] == '-' && bbuf[1] == '-') {
        line = new String(bbuf, 0, result, Statics.ISOCODE);
        if (line.startsWith(boundary)) break;
      }
      if (rnflag) {
        out.write('\r');
        out.write('\n');
        rnflag = false;
      }
      if (result >= 2 &&
          bbuf[result - 2] == '\r' &&
          bbuf[result - 1] == '\n') {
        out.write(bbuf, 0, result - 2);
        rnflag = true;
      } else {
        out.write(bbuf, 0, result);
      }
    }
    return out.toByteArray();
  }

}
