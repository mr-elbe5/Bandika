/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.servlet;

import de.elbe5.base.data.BinaryFileStreamData;
import de.elbe5.webserver.configuration.Configuration;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.MessageFormat;

public class ResponseHelper {
    public static final String KEY_RESPONSE_TYPE = "$RESPONSE";
    public static final String KEY_FORWARD_URL = "$FORWARDURL";
    public static final String KEY_JSP = "$JSP";

    public static final int RESPONSE_TYPE_STD = 0;
    public static final int RESPONSE_TYPE_FORWARD = 1;
    public static final int RESPONSE_TYPE_STREAM = 2;

    public static boolean setResponseType(HttpServletRequest request, int responseType) {
        request.setAttribute(KEY_RESPONSE_TYPE, Integer.toString(responseType));
        return true;
    }

    public static boolean setForwardUrl(HttpServletRequest request, String forwardUrl) {
        request.setAttribute(KEY_FORWARD_URL, forwardUrl);
        return true;
    }

    public static boolean sendBinaryResponse(HttpServletRequest request, HttpServletResponse response, String fileName, String contentType, byte[] bytes) throws IOException{
        return sendBinaryResponse(request, response, fileName, contentType, bytes, false);
    }

    public static boolean sendBinaryResponse(HttpServletRequest request, HttpServletResponse response, String fileName, String contentType, byte[] bytes, boolean forceDownload) throws IOException {
        setResponseType(request, RESPONSE_TYPE_STREAM);
        if (contentType != null && !contentType.isEmpty())
            contentType="*/*";
        response.setContentType(contentType);
        OutputStream out=response.getOutputStream();
        if (bytes == null) {
            response.setHeader("Content-Length", "0");
        } else {
            BaseServlet.setNoCache(response);
            StringBuilder sb = new StringBuilder();
            if (forceDownload) sb.append("attachment;");
            sb.append("filename=\"");
            sb.append(fileName);
            sb.append('"');
            response.setHeader("Content-Disposition", sb.toString());
            response.setHeader("Content-Length", Integer.toString(bytes.length));
            out.write(bytes);
        }
        out.flush();
        return true;
    }

    public static boolean sendBinaryFileResponse(HttpServletRequest request, HttpServletResponse response, BinaryFileStreamData data) throws IOException{
        return sendBinaryFileResponse(request, response, data, false);
    }

    public static boolean sendBinaryFileResponse(HttpServletRequest request, HttpServletResponse response, BinaryFileStreamData data, boolean forceDownload) throws IOException {
        setResponseType(request, RESPONSE_TYPE_STREAM);
        String contentType=data.getContentType();
        if (contentType != null && !contentType.isEmpty())
            contentType="*/*";
        StringBuilder contentDisposition = new StringBuilder();
        if (forceDownload)
            contentDisposition.append("attachment;");
        contentDisposition.append("filename=\"");
        contentDisposition.append(data.getFileName());
        contentDisposition.append('"');
        BaseServlet.setNoCache(response);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", contentDisposition.toString());
        OutputStream out=response.getOutputStream();
        data.writeToStream(out);
        out.flush();
        return true;
    }

    public static boolean sendForwardResponse(HttpServletRequest request, HttpServletResponse response, String url) {
        setResponseType(request, RESPONSE_TYPE_FORWARD);
        setForwardUrl(request, url);
        return true;
    }

    public static boolean sendJspResponse(HttpServletRequest request, HttpServletResponse response, String jsp, String master) {
        setResponseType(request, RESPONSE_TYPE_FORWARD);
        request.setAttribute(KEY_JSP, jsp);
        setForwardUrl(request, "/WEB-INF/_jsp/_master/" + master);
        return true;
    }

    public static boolean sendJspResponse(HttpServletRequest request, HttpServletResponse response, String jsp, String title, String master) {
        setResponseType(request, RESPONSE_TYPE_FORWARD);
        request.setAttribute(KEY_JSP, jsp);
        RequestHelper.setTitle(request, title);
        setForwardUrl(request, "/WEB-INF/_jsp/_master/" + master);
        return true;
    }

    public static boolean sendXmlResponse(HttpServletRequest request, HttpServletResponse response, String xml) throws IOException{
        setResponseType(request, RESPONSE_TYPE_STD);
        BaseServlet.setNoCache(response);
        response.setContentType(MessageFormat.format("text/xml;charset={0}", Configuration.getInstance().getEncoding()));
        OutputStream out = response.getOutputStream();
        if (xml == null || xml.length() == 0) {
            response.setHeader("Content-Length", "0");
        } else {
            byte[] bytes = xml.getBytes(Configuration.getInstance().getEncoding());
            BaseServlet.setNoCache(response);
            response.setHeader("Content-Length", Integer.toString(bytes.length));
            out.write(bytes);
        }
        out.flush();
        return true;
    }

    public static int getResponseType(HttpServletRequest request) {
        return RequestHelper.getInt(request, KEY_RESPONSE_TYPE);
    }

    public static String getForwardUrl(HttpServletRequest request) {
        return RequestHelper.getString(request, KEY_FORWARD_URL);
    }

    public static void addError(HttpServletRequest request, String s) {
        RequestError err = RequestHelper.getError(request);
        if (err == null) {
            err = new RequestError();
            RequestHelper.setError(request, err);
        }
        err.addErrorString(s);
    }

    public static void addError(HttpServletRequest request, Exception e) {
        RequestError err = RequestHelper.getError(request);
        if (err == null) {
            err = new RequestError();
            RequestHelper.setError(request, err);
        }
        err.addError(e);
    }

    public static boolean closeLayer(HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(BaseServlet.PARAM_ACTION);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/closeLayer.ajax.jsp");
    }

    public static boolean closeLayer(HttpServletRequest request, HttpServletResponse response, String jsCommand) {
        request.setAttribute("closeLayerFunction", jsCommand);
        return ResponseHelper.closeLayer(request, response);
    }

    public static boolean closeLayerToUrl(HttpServletRequest request, HttpServletResponse response, String url) {
        return closeLayer(request, response, "linkTo('"+url+"');");
    }

    public static boolean closeLayerToUrl(HttpServletRequest request, HttpServletResponse response, String url, String messageKey) {
        return closeLayer(request, response,  "linkTo('"+url+"&"+RequestHelper.KEY_MESSAGEKEY+"="+messageKey+"');");
    }

    public static boolean showHome(HttpServletRequest request, HttpServletResponse response) {
        return sendRedirect(request, response, "/default.srv");
    }

    public static boolean sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
        request.removeAttribute(BaseServlet.PARAM_ACTION);
        request.setAttribute("redirectUrl", url);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/redirect.jsp");
    }

}