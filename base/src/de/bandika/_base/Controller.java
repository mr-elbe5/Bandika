/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import de.bandika.application.StringCache;

import java.util.ArrayList;

/**
 * Class Controller is the base class for all Controllers <br>
 * It holds some basic request and response handling. Usage:
 */
public abstract class Controller {

  protected ArrayList<IChangeListener> changeListeners = new ArrayList<IChangeListener>();


  public Response doAction(RequestData rdata, SessionData sdata)
    throws Exception {
    String method = rdata.getParamString("method");
    if (method == null)
      method = "";
    return doMethod(method, rdata, sdata);
  }

  public Response doMethod(String method, RequestData rdata, SessionData sdata)
    throws Exception {
    return null;
  }

  public ArrayList<IChangeListener> getChangeListeners() {
    return changeListeners;
  }

  public void itemChanged(String type, String action, String itemName, int itemId) {
    for (IChangeListener listener : changeListeners)
      listener.itemChanged(type, action, itemName, itemId, true);
  }

  public static Response showBlankPage(RequestData rdata, int masterType) {
    rdata.removeParam("id");
    rdata.removeParam("method");
    return new JspResponse("/_jsp/blank.jsp", masterType);
  }

  public static Response showBlankPage(RequestData rdata, String title, int masterType) {
    rdata.removeParam("id");
    rdata.removeParam("method");
    return new JspResponse("/_jsp/blank.jsp", title, masterType);
  }

  public static Response showCloseLayer(RequestData rdata, int masterType) {
    rdata.removeParam("id");
    rdata.removeParam("method");
    return new JspResponse("/_jsp/closeLayer.inc.jsp", masterType);
  }

  public Response noRight(RequestData rdata, int masterType) {
    String method = rdata.getParamString("method");
    if (method == null)
      method = "[EMPTY]";
    rdata.setError(new RequestError(StringCache.getHtml("noRight") + ": " + this.getClass().getName() + "." + method));
    return new JspResponse("/_jsp/blank.jsp", masterType);
  }

  public Response noData(RequestData rdata, int masterType) {
    String method = rdata.getParamString("method");
    if (method == null)
      method = "[EMPTY]";
    rdata.setError(new RequestError(StringCache.getHtml("noData") + ": " + this.getClass().getName() + "." + method));
    return new JspResponse("/_jsp/blank.jsp", masterType);
  }

  public static void addError(RequestData rdata, String s) {
    RequestError err = rdata.getError();
    if (err == null) {
      err = new RequestError();
      rdata.setError(err);
    }
    err.addErrorString(s);
  }

  public static void addError(RequestData rdata, Exception e) {
    RequestError err = rdata.getError();
    if (err == null) {
      err = new RequestError();
      rdata.setError(err);
    }
    err.addError(e);
  }

}
