/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.webserver.servlet;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class RequestHelper {
    public static final String KEY_ERROR = "$ERROR";
    public static final String KEY_EXCEPTION = "$EXCEPTION";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_MESSAGEKEY = "$MESSAGEKEY";
    public static final String KEY_TITLE = "$TITLE";
    public static final String KEY_USER = "$USER";
    public static final String KEY_LOCALE = "$LOCALE";

    public static boolean isPostback(HttpServletRequest request) {
        return request.getMethod().equalsIgnoreCase("POST");
    }

    public static void setError(HttpServletRequest request, RequestError error) {
        request.setAttribute(KEY_ERROR, error);
    }

    public static RequestError getError(HttpServletRequest request) {
        return (RequestError) request.getAttribute(KEY_ERROR);
    }

    public static void setMessageKey(HttpServletRequest request, String messageKey) {
        request.setAttribute(KEY_MESSAGEKEY, messageKey);
    }

    public static String getMessage(HttpServletRequest request) {
        String msg = getString(request, KEY_MESSAGE);
        if (msg.isEmpty()){
            String key=getString(request, KEY_MESSAGEKEY);
            if (!key.isEmpty())
                msg= StringUtil.getString(key, SessionHelper.getSessionLocale(request));
        }
        return msg;
    }

    public static void setException(HttpServletRequest request, Exception ex) {
        request.setAttribute(KEY_EXCEPTION, ex);
    }

    public static String getTitle(HttpServletRequest request) {
        return getString(request, KEY_TITLE);
    }

    public static void setTitle(HttpServletRequest request, String title) {
        request.setAttribute(KEY_TITLE, title);
    }

    public static String getString(HttpServletRequest request, String key) {
        Object obj = request.getAttribute(key);
        if (obj == null) return "";
        if (obj instanceof String) return (String) obj;
        if (obj instanceof String[]) return ((String[]) obj)[0];
        return null;
    }

    public static String getString(HttpServletRequest request, String key, String def) {
        Object obj = request.getAttribute(key);
        if (obj == null) return def;
        if (obj instanceof String) return (String) obj;
        if (obj instanceof String[]) return ((String[]) obj)[0];
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
                while (stk.hasMoreTokens()) list.add(stk.nextToken());
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
        Object obj = request.getAttribute(key);
        if (obj != null && obj instanceof String) {
            StringTokenizer stk = new StringTokenizer((String) obj, ",");
            String token = null;
            while (stk.hasMoreTokens()) {
                try {
                    token = stk.nextToken();
                    set.add(Integer.parseInt(token));
                } catch (NumberFormatException e) {
                    Log.error("wrong number format: " + token);
                }
            }
        } else {
            String[] values = (String[]) request.getAttribute(key);
            if (values != null) {
                for (String value : values) {
                    try {
                        set.add(Integer.parseInt(value));
                    } catch (NumberFormatException e) {
                        Log.error("wrong number format: " + value);
                    }
                }
            }
        }
        return set;
    }

    public static BinaryFileData getFile(HttpServletRequest request, String key) {
        BinaryFileData file = null;
        try {
            Object obj = request.getAttribute(key);
            if (obj != null && obj instanceof BinaryFileData) file = (BinaryFileData) obj;
        } catch (Exception ignore) {/* do nothing */
        }
        return file;
    }

}