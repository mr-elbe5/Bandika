/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.webbase.servlet;

import de.bandika.base.data.BaseData;
import de.bandika.base.data.BaseIdData;
import de.bandika.base.data.BinaryFileStreamData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public abstract class ActionSet {

    protected abstract String getKey();

    protected abstract boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception;

    protected String getServletCall() {
        return "/" + getKey() + ".srv";
    }

    protected String getAjaxCall() {
        return "/" + getKey() + ".ajx";
    }

    protected boolean setResponseType(HttpServletRequest request, int responseType) {
        request.setAttribute(RequestStatics.KEY_RESPONSE_TYPE, Integer.toString(responseType));
        return true;
    }

    protected int getResponseType(HttpServletRequest request) {
        return RequestReader.getInt(request, RequestStatics.KEY_RESPONSE_TYPE);
    }

    protected boolean setForwardUrl(HttpServletRequest request, String forwardUrl) {
        request.setAttribute(RequestStatics.KEY_FORWARD_URL, forwardUrl);
        return true;
    }

    protected String getForwardUrl(HttpServletRequest request) {
        return RequestReader.getString(request, RequestStatics.KEY_FORWARD_URL);
    }

    protected Object getSessionObject(HttpServletRequest request, String key) {
        Object obj = RequestReader.getSessionObject(request, key);
        checkObject(obj);
        return obj;
    }

    protected void checkObject(Object obj) {
        if (obj == null) {
            throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    protected void checkObject(BaseIdData obj, int requestId) {
        if (obj == null || obj.getId() != requestId) {
            throw new HttpException(HttpServletResponse.SC_NO_CONTENT);
        }
    }

    protected boolean isDataComplete(BaseData data, HttpServletRequest request) {
        if (!data.isComplete()) {
            RequestError err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", RequestReader.getSessionLocale(request)));
            RequestError.setError(request, err);
            return false;
        }
        return true;
    }

    protected boolean sendBinaryResponse(HttpServletRequest request, HttpServletResponse response, String fileName, String contentType, byte[] bytes) throws IOException {
        return sendBinaryResponse(request, response, fileName, contentType, bytes, false);
    }

    protected boolean sendBinaryResponse(HttpServletRequest request, HttpServletResponse response, String fileName, String contentType, byte[] bytes, boolean forceDownload) throws IOException {
        setResponseType(request, RequestStatics.RESPONSE_TYPE_STREAM);
        if (contentType != null && !contentType.isEmpty()) {
            contentType = "*/*";
        }
        response.setContentType(contentType);
        OutputStream out = response.getOutputStream();
        if (bytes == null) {
            response.setHeader("Content-Length", "0");
        } else {
            RequestWriter.setNoCache(response);
            StringBuilder sb = new StringBuilder();
            if (forceDownload) {
                sb.append("attachment;");
            }
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

    protected boolean sendBinaryFileResponse(HttpServletRequest request, HttpServletResponse response, BinaryFileStreamData data) throws IOException {
        return sendBinaryFileResponse(request, response, data, false);
    }

    protected boolean sendBinaryFileResponse(HttpServletRequest request, HttpServletResponse response, BinaryFileStreamData data, boolean forceDownload) throws IOException {
        setResponseType(request, RequestStatics.RESPONSE_TYPE_STREAM);
        String contentType = data.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            contentType = "*/*";
        }
        StringBuilder contentDisposition = new StringBuilder();
        if (forceDownload) {
            contentDisposition.append("attachment;");
        }
        contentDisposition.append("filename=\"");
        contentDisposition.append(data.getFileName());
        contentDisposition.append('"');
        RequestWriter.setNoCache(response);
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", contentDisposition.toString());
        OutputStream out = response.getOutputStream();
        data.writeToStream(out);
        out.flush();
        return true;
    }

    protected boolean sendForwardResponse(HttpServletRequest request, HttpServletResponse response, String url) {
        setResponseType(request, RequestStatics.RESPONSE_TYPE_FORWARD);
        setForwardUrl(request, url);
        return true;
    }

    protected boolean sendJspResponse(HttpServletRequest request, HttpServletResponse response, String jsp, String master) {
        setResponseType(request, RequestStatics.RESPONSE_TYPE_FORWARD);
        request.setAttribute(RequestStatics.KEY_JSP, jsp);
        setForwardUrl(request, "/WEB-INF/_jsp/_master/" + master);
        return true;
    }

    protected boolean sendXmlResponse(HttpServletRequest request, HttpServletResponse response, String xml) {
        response.setContentType(MessageFormat.format("text/xml;charset={0}", RequestStatics.ENCODING));
        return sendStdResponse(request, response, xml);
    }

    protected boolean sendHtmlResponse(HttpServletRequest request, HttpServletResponse response, String html) {
        response.setContentType(MessageFormat.format("text/html;charset={0}", RequestStatics.ENCODING));
        return sendStdResponse(request, response, html);
    }

    protected boolean sendStdResponse(HttpServletRequest request, HttpServletResponse response, String str) {
        setResponseType(request, RequestStatics.RESPONSE_TYPE_STD);
        RequestWriter.setNoCache(response);
        try {
            OutputStream out = response.getOutputStream();
            if (str == null || str.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = str.getBytes(RequestStatics.ENCODING);
                RequestWriter.setNoCache(response);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
        } catch (IOException ioe) {
            Log.error("response error", ioe);
            return false;
        }
        return true;
    }

    protected void addError(HttpServletRequest request, String s) {
        RequestError err = RequestError.getError(request);
        if (err == null) {
            err = new RequestError();
            RequestError.setError(request, err);
        }
        err.addErrorString(s);
    }

    protected void addError(HttpServletRequest request, Exception e) {
        RequestError err = RequestError.getError(request);
        if (err == null) {
            err = new RequestError();
            RequestError.setError(request, err);
        }
        err.addError(e);
    }

    protected boolean closeLayer(HttpServletRequest request, HttpServletResponse response) {
        request.removeAttribute(RequestStatics.PARAM_ACTION);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/closeLayer.ajax.jsp");
    }

    protected boolean closeLayer(HttpServletRequest request, HttpServletResponse response, String jsCommand) {
        request.setAttribute("closeLayerFunction", jsCommand);
        return closeLayer(request, response);
    }

    protected boolean closeLayerToUrl(HttpServletRequest request, HttpServletResponse response, String url) {

        return closeLayer(request, response, "linkTo('" + url + "');");
    }

    protected boolean closeLayerToUrl(HttpServletRequest request, HttpServletResponse response, String url, String messageKey) {

        return closeLayer(request, response, "linkTo('" + url + "&" + RequestStatics.KEY_MESSAGEKEY + "=" + messageKey + "');");
    }

    protected boolean showHome(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return sendRedirect(request, response, "/default.srv");
    }

    protected boolean sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
        request.removeAttribute(RequestStatics.PARAM_ACTION);
        request.setAttribute("redirectUrl", url);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/redirect.jsp");
    }

    protected boolean forbidden() {
        throw new HttpException(HttpServletResponse.SC_FORBIDDEN);
    }

    protected boolean badRequest() {
        throw new HttpException(HttpServletResponse.SC_BAD_REQUEST);
    }

    protected boolean isAjaxRequest(HttpServletRequest request) {
        return RequestReader.getString(request, RequestStatics.PARAM_SUFFIX).equals(RequestStatics.AJAX_SUFFIX);
    }

}
