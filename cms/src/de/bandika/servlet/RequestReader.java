/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.base.util.StringUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.*;

public class RequestReader {

    public static boolean isPostback(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST");
    }

    public static String getMessage(HttpServletRequest request) {
        String msg = getString(request, RequestStatics.KEY_MESSAGE);
        if (msg.isEmpty()) {
            String key = getString(request, RequestStatics.KEY_MESSAGEKEY);
            if (!key.isEmpty()) {
                msg = StringUtil.getString(key, SessionReader.getSessionLocale(request));
            }
        }
        return msg;
    }

    public static String getString(HttpServletRequest request, String key) {
        return getString(request, key, "");
    }

    public static String getString(HttpServletRequest request, String key, String def) {
        Object obj = request.getAttribute(key);
        if (obj == null) {
            return def;
        }
        if (obj instanceof String) {
            return (String) obj;
        }
        if (obj instanceof String[]) {
            return ((String[]) obj)[0];
        }
        return def;
    }

    public static int getInt(HttpServletRequest request, String key, int defaultValue) {
        int value = defaultValue;
        try {
            String str = getString(request, key);
            value = Integer.parseInt(str);
        } catch (Exception ignore) {/* do nothing */

        }
        return value;
    }

    public static int getInt(HttpServletRequest request, String key) {
        return getInt(request, key, 0);
    }

    public static boolean getBoolean(HttpServletRequest request, String key, boolean defaultValue) {
        boolean value = defaultValue;
        try {
            String str = getString(request, key);
            value = Boolean.parseBoolean(str);
        } catch (Exception ignore) {/* do nothing */

        }
        return value;
    }

    public static boolean getBoolean(HttpServletRequest request, String key) {
        return getBoolean(request, key, false);
    }

    public static List<String> getStringList(HttpServletRequest request, String key) {
        List<String> list = new ArrayList<>();
        Object obj = request.getAttribute(key);
        if (obj != null) {
            if (obj instanceof String) {
                StringTokenizer stk = new StringTokenizer((String) obj, ",");
                while (stk.hasMoreTokens()) {
                    list.add(stk.nextToken());
                }
            } else if (obj instanceof String[]) {
                String[] values = (String[]) obj;
                list.addAll(Arrays.asList(values));
            }
        }
        return list;
    }

    public static List<Integer> getIntegerList(HttpServletRequest request, String key) {
        List<Integer> list = new ArrayList<>();
        Object obj = request.getAttribute(key);
        if (obj != null && obj instanceof String) {
            StringTokenizer stk = new StringTokenizer((String) obj, ",");
            String token = null;
            while (stk.hasMoreTokens()) {
                try {
                    token = stk.nextToken();
                    list.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    Log.error("wrong number format: " + token);
                }
            }
        } else {
            String[] values = (String[]) request.getAttribute(key);
            if (values != null) {
                for (String value : values) {
                    try {
                        list.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        Log.error("wrong number format: " + value);
                    }
                }
            }
        }
        return list;
    }

    public static Set<Integer> getIntegerSet(HttpServletRequest request, String key) {
        Set<Integer> set = new HashSet<>();
        set.addAll(getIntegerList(request, key));
        return set;
    }

    public static BinaryFileData getFile(HttpServletRequest request, String key) {
        BinaryFileData file = null;
        try {
            Object obj = request.getAttribute(key);
            if (obj != null && obj instanceof BinaryFileData) {
                file = (BinaryFileData) obj;
            }
        } catch (Exception ignore) {/* do nothing */

        }
        return file;
    }

    /* basic request analyzer */

    public static void getSinglePartParams(HttpServletRequest request) {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            if (strings.length == 1) {
                request.setAttribute(key, strings[0]);
            } else {
                StringBuilder sb = new StringBuilder(strings[0]);
                for (int i = 1; i < strings.length; i++) {
                    sb.append(',');
                    sb.append(strings[i]);
                }
                request.setAttribute(key, sb.toString());
            }
        }
    }

    public static void getMultiPartParams(HttpServletRequest request) throws ServletException {
        Map<String,List<String>> params=new HashMap<>();
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName = getFileName(part);
                if (fileName != null) {
                    BinaryFileData file = getMultiPartFile(part, fileName);
                    if (file != null) {
                        request.setAttribute(name, file);
                    }
                } else {
                    String param = getMultiPartParameter(part);
                    if (param != null) {
                        List<String> values;
                        if (params.containsKey(name))
                            values=params.get(name);
                        else {
                            values=new ArrayList<>();
                            params.put(name,values);
                        }
                        values.add(param);
                    }
                }
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
        for (String key : params.keySet()){
            List<String> strings = params.get(key);
            if (strings.size() == 1) {
                request.setAttribute(key, strings.get(0));
            } else {
                StringBuilder sb = new StringBuilder(strings.get(0));
                for (int i = 1; i < strings.size(); i++) {
                    sb.append(',');
                    sb.append(strings.get(i));
                }
                request.setAttribute(key, sb.toString());
            }
        }
    }

    private static String getMultiPartParameter(Part part) {
        try {
            byte[] bytes = new byte[(int) part.getSize()];
            int read = part.getInputStream().read(bytes);
            if (read > 0) {
                return new String(bytes, RequestStatics.ENCODING);
            }
        } catch (Exception e) {
            Log.error("could not extract parameter from multipart", e);
        }
        return null;
    }

    private static BinaryFileData getMultiPartFile(Part part, String fileName) {
        try {
            BinaryFileData file = new BinaryFileData();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setFileSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null) {
                return null;
            }
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

    private static String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }
}
