/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.application.StringCache;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.ServletContext;

/**
 * Class RequestData is the data class for holding request data. <br>
 * Usage:
 */
public class RequestData extends ParamData {

  public static final int ROOT_PAGE_ID = 100;

  protected ServletContext context = null;
  protected HttpServletRequest request = null;
  protected boolean isPostback = false;
  protected String title = "";
  protected boolean hasRequestParams = false;
  protected String currentJsp = "/index.jsp";

  public void reset() {
    params.clear();
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
    params.put("error", error);
  }

  public void setErrorKey(String errorKey) {
    params.put("error", StringCache.getString(errorKey));
  }

  public RequestError getError() {
    return (RequestError) params.get("error");
  }

  public void setMessage(String message) {
    params.put("message", message);
  }

  public void setMessageKey(String messageKey) {
    params.put("message", StringCache.getString(messageKey));
  }

  public String getMessage() {
    return getParamString("message");
  }

  public void setException(Exception ex) {
    params.put("exception", ex);
  }

  public Exception getException() {
    return (Exception) params.get("exception");
  }

  public String getTitle() {
    return title;
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public boolean hasRequestParams() {
    return hasRequestParams;
  }

  public void setHasRequestParams(boolean hasRequestParams) {
    this.hasRequestParams = hasRequestParams;
  }

  public String getCurrentJsp() {
    return currentJsp;
  }

  public void setCurrentJsp(String currentJsp) {
    this.currentJsp = currentJsp;
  }

  public int getCurrentPageId() {
    int id = getParamInt("id");
    if (id != 0)
      return id;
    return RequestData.ROOT_PAGE_ID;
  }

}
