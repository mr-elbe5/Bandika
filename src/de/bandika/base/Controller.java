/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.http.*;

import java.util.HashMap;


/**
 * Class Controller is the base class for all Controllers <br>
 * It holds some basic request and response handling.
 * Usage:
 */
public class Controller {

	protected static HashMap<String, Controller> controllers = new HashMap<String, Controller>();

	public static String DEFAULT_KEY="";
  public static String NO_KEY="none";

	public static HashMap<String, Controller> getControllers() {
		return controllers;
	}

	public static void addController(String key, Controller data) {
		controllers.put(key, data);
	}

	public static Controller getController(String key) {
		if (key == null || key.length() == 0)
			return null;
		return controllers.get(key);
	}

	public static Response getResponse(RequestData rdata, SessionData sdata) throws Exception {
    String ctrlName=rdata.getParamString("ctrl");
    if (ctrlName.equalsIgnoreCase(NO_KEY))
      return null;
    if (ctrlName==null || ctrlName.length()==0)
      ctrlName = Controller.DEFAULT_KEY;
		Controller controller = getController(ctrlName);
		return controller.doAction(rdata, sdata);
	}

	public void initialize(){
	}

	public Response doAction(RequestData rdata, SessionData sdata) throws Exception {
		String method = rdata.getParamString("method");
		if (method == null || method.length() == 0)
			method = "show";
		return doMethod(method, rdata, sdata);
	}

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
		return null;
	}

	public Response noMethod(RequestData rdata, SessionData sdata) throws Exception {
		rdata.setParam("msg", UserStrings.nomethod);
		return new JspResponse("/_jsp/msg.jsp");
	}

	public Response noData(RequestData rdata, SessionData sdata) throws Exception {
		rdata.setParam("msg", UserStrings.nodata);
		return new JspResponse("/_jsp/msg.jsp");
	}

	public static void addError(RequestData rdata, String s) {
		RequestError err = rdata.getError();
		if (err == null) {
			err = new RequestError();
			rdata.setError(err);
		}
		err.addErrorString(s);
	}

	protected Response showMessage(RequestData rdata, String msg) {
		rdata.setParam("msg", msg);
		return new JspResponse("/_jsp/msg.jsp");
	}

	protected Response showError(RequestData rdata, String error) {
		rdata.setError(new RequestError(error));
		return new JspResponse("/_jsp/error.jsp");
	}

}
