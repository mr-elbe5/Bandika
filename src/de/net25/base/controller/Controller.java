/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.controller;

import de.net25.resources.statics.Statics;
import de.net25.resources.statics.Strings;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.base.RequestError;


/**
 * Class Controller is the base class for all Controllers <br>
 * It holds some basic request and response handling.
 * Usage:
 */
public class Controller {

  protected static final String msgJsp = "/jsps/msg.jsp";
  protected static final String msgPopupJsp = "/jsps/msgPopup.jsp";

  /**
   * Method doAction
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response doAction(RequestData rdata, SessionData sdata) throws Exception {
    String method = rdata.getParamString("method");
    if (method == null || method.length() == 0)
      method = "show";
    return doMethod(method, rdata, sdata);
  }

  /**
   * Method doMethod
   *
   * @param method of type String
   * @param rdata  of type RequestData
   * @param sdata  of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
    return showHome(rdata, sdata);
  }

  /**
   * Method noMethod
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response noMethod(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setParam("msg", Strings.getString("err_no_method", sdata.getLocale()));
    return new PageResponse(msgJsp);
  }

  /**
   * Method noData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response noData(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setParam("msg", Strings.getString("err_no_data", sdata.getLocale()));
    return new PageResponse(msgJsp);
  }

  /**
   * Method noPopupData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response noPopupData(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setParam("msg", Strings.getString("err_no_data", sdata.getLocale()));
    return new PopupResponse(msgPopupJsp);
  }

  /**
   * Method showHome
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return Response
   * @throws Exception when data processing is not successful
   */
  public Response showHome(RequestData rdata, SessionData sdata) throws Exception {
    rdata.setParam("id", Integer.toString(Statics.getContentHomeId(sdata.getLocale())));
    return Statics.getController(Statics.KEY_CONTENT).doMethod("show", rdata, sdata);
  }

  /**
   * Method addError
   *
   * @param rdata of type RequestData
   * @param s     of type String
   */
  public static void addError(RequestData rdata, String s) {
    RequestError err = rdata.getError();
    if (err == null) {
      err = new RequestError();
      rdata.setError(err);
    }
    err.addErrorString(s);
  }

  /**
   * Method getMsgJsp returns the msgJsp of this Controller object.
   *
   * @return the msgJsp (type String) of this Controller object.
   */
  protected String getMsgJsp() {
    return "msg.jsp";
  }

  /**
   * Method getMsgPopupJsp returns the msgPopupJsp of this Controller object.
   *
   * @return the msgPopupJsp (type String) of this Controller object.
   */
  protected String getMsgPopupJsp() {
    return "msgPopup.jsp";
  }

  /**
   * Method getErrorJsp returns the errorJsp of this Controller object.
   *
   * @return the errorJsp (type String) of this Controller object.
   */
  protected String getErrorJsp() {
    return "error.jsp";
  }

  /**
   * Method showMessage
   *
   * @param rdata  of type RequestData
   * @param header of type String
   * @param msg    of type String
   * @return Response
   */
  protected Response showMessage(RequestData rdata, String header, String msg) {
    rdata.setParam("msg", msg);
    return new PageResponse(header, "", getMsgJsp());
  }

  /**
   * Method showError
   *
   * @param rdata of type RequestData
   * @param error of type String
   * @return Response
   */
  protected Response showError(RequestData rdata, String error) {
    rdata.setError(new RequestError(error));
    return new PageResponse(getErrorJsp());
  }

}
