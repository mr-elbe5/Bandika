/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.base.data.JsonData;
import de.elbe5.base.data.BinaryFileStreamData;
import de.elbe5.base.log.Log;
import de.elbe5.base.mail.Mailer;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.configuration.Configuration;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public abstract class ActionSet {

    public static final String KEY_PAGE = "pageData";

    protected abstract String getKey();

    protected abstract boolean execute(HttpServletRequest request, HttpServletResponse response, String actionName) throws Exception;

    protected void setResponseType(HttpServletRequest request, int responseType) {
        request.setAttribute(Statics.KEY_RESPONSE_TYPE, Integer.toString(responseType));
    }

    protected int getResponseType(HttpServletRequest request) {
        return RequestReader.getInt(request, Statics.KEY_RESPONSE_TYPE);
    }

    protected void setForwardUrl(HttpServletRequest request, String forwardUrl) {
        request.setAttribute(Statics.KEY_FORWARD_URL, forwardUrl);
    }

    protected String getForwardUrl(HttpServletRequest request) {
        return RequestReader.getString(request, Statics.KEY_FORWARD_URL);
    }

    protected boolean sendBinaryResponse(HttpServletRequest request, HttpServletResponse response, String fileName, String contentType, byte[] bytes, boolean forceDownload) throws IOException {
        setResponseType(request, Statics.RESPONSE_TYPE_DIRECT);
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

    protected boolean sendBinaryStreamResponse(HttpServletRequest request, HttpServletResponse response, BinaryFileStreamData data, boolean forceDownload) throws IOException {
        setResponseType(request, Statics.RESPONSE_TYPE_DIRECT);
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
        setResponseType(request, Statics.RESPONSE_TYPE_FORWARD);
        setForwardUrl(request, url);
        return true;
    }

    protected boolean sendXmlResponse(HttpServletRequest request, HttpServletResponse response, String xml) {
        response.setContentType(MessageFormat.format("text/xml;charset={0}", Statics.ENCODING));
        return sendTextResponse(request, response, xml);
    }

    protected boolean sendHtmlResponse(HttpServletRequest request, HttpServletResponse response, String html) {
        response.setContentType(MessageFormat.format("text/html;charset={0}", Statics.ENCODING));
        return sendTextResponse(request, response, html);
    }

    protected boolean sendTextResponse(HttpServletRequest request, HttpServletResponse response, String str) {
        setResponseType(request, Statics.RESPONSE_TYPE_DIRECT);
        RequestWriter.setNoCache(response);
        try {
            OutputStream out = response.getOutputStream();
            if (str == null || str.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = str.getBytes(Statics.ENCODING);
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

    protected boolean showHome(HttpServletRequest request, HttpServletResponse response) {
        return sendRedirect(request, response, "/default.srv");
    }

    protected boolean showLogin(HttpServletRequest request, HttpServletResponse response) {
        return sendForwardResponse(request, response, "/user.srv");
    }

    protected boolean sendRedirect(HttpServletRequest request, HttpServletResponse response, String url) {
        request.removeAttribute(Statics.PARAM_ACTION);
        request.setAttribute("redirectUrl", url);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/redirect.jsp");
    }

    protected boolean forbidden(HttpServletRequest request, HttpServletResponse response) {
        ErrorMessage.setMessageByKey(request, Strings._forbidden);
        return showLogin(request, response);
    }

    protected boolean noData(HttpServletRequest request, HttpServletResponse response) {
        ErrorMessage.setMessageByKey(request, Strings._noData);
        return showLogin(request, response);
    }

    protected boolean badData(HttpServletRequest request, HttpServletResponse response) {
        ErrorMessage.setMessageByKey(request, Strings._badData);
        return showLogin(request, response);
    }

    protected boolean hasSystemRight(HttpServletRequest request, SystemZone zone, Right right) {
        return SessionReader.hasSystemRight(request, zone, right);
    }

    protected boolean hasContentRight(HttpServletRequest request, int id, Right right) {
        return SessionReader.hasContentRight(request, id, right);
    }

    protected boolean hasAnyContentRight(HttpServletRequest request) {
        return SessionReader.hasAnyContentRight(request);
    }

    protected boolean setPageResponse(HttpServletRequest request, HttpServletResponse response, PageData data) {
        request.setAttribute(KEY_PAGE, data);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/page/page.jsp");
    }

    protected boolean closeDialogWithRedirect(HttpServletRequest request, HttpServletResponse response, String actionUrl, Strings msgEnum)  {
        assert (actionUrl.contains("?act="));
        String closeScript="linkTo('"+actionUrl;
        if (msgEnum!=null)
            closeScript+="&"+ Statics.KEY_MESSAGEKEY+"="+msgEnum.toString();
        closeScript+="');";
        request.setAttribute(Statics.KEY_CLOSESCRIPT, closeScript);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/closeDialog.ajax.jsp");
    }

    protected boolean closeDialogWithAjaxRedirect(HttpServletRequest request, HttpServletResponse response, String closeScript)  {
        Log.info(closeScript);
        request.setAttribute(Statics.KEY_CLOSESCRIPT, closeScript);
        return sendForwardResponse(request, response, "/WEB-INF/_jsp/closeDialog.ajax.jsp");
    }

    protected void setSuccessMessageByKey(HttpServletRequest request){
        String msgKey=RequestReader.getString(request, Statics.KEY_MESSAGEKEY);
        if (!msgKey.isEmpty())
            SuccessMessage.setMessageByKey(request, msgKey);
    }

    public String getPostByAjaxCall(String url, JsonData data, String targetId){
        return "postByAjax('"+url+"',"+data.getParams()+",'"+targetId+"');";
    }

    protected boolean sendPlainMail(String to,String subject,String text){
        Mailer mailer = Configuration.getInstance().getMailer();
        mailer.setTo(to);
        mailer.setSubject(subject);
        mailer.setText(text);
        try {
            if (!mailer.sendMail()){
                Log.error("could not end mail");
                return false;
            }
        }
        catch (Exception e){
            Log.error("could not end mail", e);
            return false;
        }
        return true;
    }

    protected boolean sendHtmlMail(String to,String subject,String html){
        Mailer mailer = Configuration.getInstance().getMailer();
        mailer.setTo(to);
        mailer.setSubject(subject);
        mailer.setHtml(html);
        try {
            if (!mailer.sendMail()){
                Log.error("could not end mail");
                return false;
            }
        }
        catch (Exception e){
            Log.error("could not end mail", e);
            return false;
        }
        return true;
    }

}
