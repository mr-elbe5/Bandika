/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.servlet;

import de.bandika.data.KeyValueMap;
import de.bandika.data.StringCache;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import java.util.Locale;

public class RequestData extends KeyValueMap {

    public static final String KEY_ERROR = "$ERROR";
    public static final String KEY_EXCEPTION = "$EXCEPTION";
    public static final String KEY_MESSAGE = "$MESSAGE";
    public static final String KEY_TITLE = "$TITLE";

    protected ServletContext context = null;
    protected HttpServletRequest request = null;
    protected boolean isPostback = false;
    protected boolean hasRequestParams = false;

    public void reset() {
        clear();
        hasRequestParams = false;
    }

    public ServletContext getContext() {
        return context;
    }

    public void setContext(ServletContext context) {
        this.context = context;
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public void setRequest(HttpServletRequest request) {
        this.request = request;
    }

    public boolean isPostback() {
        return isPostback;
    }

    public void setPostback(boolean postback) {
        isPostback = postback;
    }

    public void setError(RequestError error) {
        put(KEY_ERROR, error);
    }

    public RequestError getError() {
        return (RequestError) get(KEY_ERROR);
    }

    public void setMessage(String message) {
        put(KEY_MESSAGE, message);
    }

    public void setMessageKey(String messageKey, Locale locale) {
        put(KEY_MESSAGE, StringCache.getHtml(messageKey, locale));
    }

    public String getMessage() {
        return getString(KEY_MESSAGE);
    }

    public void setException(Exception ex) {
        put(KEY_EXCEPTION, ex);
    }

    public Exception getException() {
        return (Exception) get(KEY_EXCEPTION);
    }

    public String getTitle() {
        return getString(KEY_TITLE);
    }

    public void setTitle(String title) {
        put(KEY_TITLE, title);
    }

    public boolean hasRequestParams() {
        return hasRequestParams;
    }

    public void setHasRequestParams(boolean hasRequestParams) {
        this.hasRequestParams = hasRequestParams;
    }

}
