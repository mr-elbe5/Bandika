/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.data.RequestData;
import de.bandika.data.SessionData;
import de.bandika.response.Response;
import de.bandika.response.ForwardResponse;

/**
 * Class Controller is the base class for all Controllers <br>
 * It holds some basic request and response handling.
 * Usage:
 */
public class Controller {

	public void initialize(){
	}

	public Response doAction(RequestData rdata, SessionData sdata) throws Exception {
		String method = rdata.getParamString("method");
		if (method == null)
			method = "";
		return doMethod(method, rdata, sdata);
	}

	public Response doMethod(String method, RequestData rdata, SessionData sdata) throws Exception {
		return null;
	}

  public static Response goHome(RequestData rdata){
    rdata.setCurrentJsp("/index.jsp");
    rdata.setCurrentPageId(RequestData.ROOT_PAGE_ID);
    rdata.removeParam("method");
    rdata.removeParam("id");
    return new ForwardResponse("/index.jsp");
  }

	public static void addError(RequestData rdata, String s) {
		RequestError err = rdata.getError();
		if (err == null) {
			err = new RequestError();
			rdata.setError(err);
		}
		err.addErrorString(s);
	}

}
