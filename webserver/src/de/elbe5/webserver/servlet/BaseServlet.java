/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.servlet;

import de.elbe5.base.catalina.FilePath;
import de.elbe5.webserver.configuration.Configuration;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.log.Log;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.Enumeration;

public abstract class BaseServlet extends HttpServlet {
    public static final String PARAM_ACTION = "act";
    public static final String PARAM_AJAX = "ajx";
    public static final String SERVLET_SUFFIX = ".srv";
    public static final String AJAX_SUFFIX = ".ajx";
    public static final int SUFFIX_LENGTH = 4;

    public static void setNoCache(HttpServletResponse response) {
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }

    protected abstract IActionController getController(HttpServletRequest request);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        super.init(servletConfig);
        FilePath.initializePath(FilePath.getCatalinaAppDir(getServletContext()), FilePath.getCatalinaAppROOTDir(getServletContext()));
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        try {
            IActionController controller = getController(request);
            if (controller != null) {
                String action = RequestHelper.getString(request, PARAM_ACTION);
                if (controller.doAction(action, request, response)) {
                    int responseType=ResponseHelper.getResponseType(request);
                    if (responseType==ResponseHelper.RESPONSE_TYPE_FORWARD){
                        String forwardUrl=ResponseHelper.getForwardUrl(request);
                        RequestDispatcher rd = getServletContext().getRequestDispatcher(forwardUrl);
                        rd.forward(request, response);
                    }
                }
                else{
                    response.sendError(404);
                }
            }
        } catch (HttpException e) {
            try{
                RequestError re=new RequestError();
                switch (e.errorCode){
                    case HttpServletResponse.SC_BAD_REQUEST :
                        re.addErrorString("Bad Request");
                        break;
                    case HttpServletResponse.SC_FORBIDDEN :
                        re.addErrorString("Forbidden");
                        break;
                    case HttpServletResponse.SC_NO_CONTENT :
                        re.addErrorString("Session data missing");
                        break;
                    default:
                        throw e;
                }
                RequestHelper.setError(request, re);
                RequestDispatcher rd = getServletContext().getRequestDispatcher("/WEB-INF/_jsp/exception.jsp");
                rd.forward(request, response);
            }
            catch (IOException ioe){
                throw new ServletException(ioe);
            }
        } catch (Exception e) {
            Log.error("servlet error", e);
        }
    }

    protected void processRequestData(HttpServletRequest request, HttpServletResponse response) throws ServletException {
        String type = request.getContentType();
        if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
            getMultiPartParams(request);
        } else {
            getSinglePartParams(request);
        }
    }

    public void getSinglePartParams(HttpServletRequest request) {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            if (strings.length == 1) request.setAttribute(key, strings[0]);
            else {
                StringBuilder sb = new StringBuilder(strings[0]);
                for (int i = 1; i < strings.length; i++) {
                    sb.append(',');
                    sb.append(strings[i]);
                }
                request.setAttribute(key, sb.toString());
            }
        }
    }

    public void getMultiPartParams(HttpServletRequest request) throws ServletException {
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName = getFileName(part);
                if (fileName != null) {
                    BinaryFileData file = getMultiPartFile(part, fileName);
                    if (file != null) request.setAttribute(name, file);
                } else {
                    String param = getMultiPartParameter(part);
                    if (param != null) request.setAttribute(name, param);
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private String getMultiPartParameter(Part part) {
        try {
            byte[] bytes = new byte[(int) part.getSize()];
            int read = part.getInputStream().read(bytes);
            if (read > 0) return new String(bytes, Configuration.getInstance().getEncoding());
        } catch (Exception e) {
            Log.error("could not extract parameter from multipart", e);
        }
        return null;
    }

    private BinaryFileData getMultiPartFile(Part part, String fileName) {
        try {
            BinaryFileData file = new BinaryFileData();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setFileSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null) return null;
            ByteArrayOutputStream out = new ByteArrayOutputStream(file.getFileSize());
            byte[] buffer = new byte[8096];
            int len;
            while ((len = in.read(buffer, 0, 8096)) != -1) {
                out.write(buffer, 0, len);
            }
            file.setBytes(out.toByteArray());
            return file;
        } catch (Exception e) {
            Log.error("could not extract file from multipart", e);
            return null;
        }
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}