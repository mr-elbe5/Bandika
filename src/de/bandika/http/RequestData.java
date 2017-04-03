/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.http;

import de.bandika.base.RequestError;
import de.bandika.base.BaseConfig;

import javax.servlet.http.HttpServletRequest;

/**
 * Class RequestData is the data class for holding request data. <br>
 * Usage:
 */
public class RequestData extends ParamData {

	public static final int PROCESSED_NONE = 0;
	public static final int PROCESSED_CONTROLLER = 1;
	public static final int PROCESSED_MASTER = 2;

	protected HttpServletRequest request = null;
  protected boolean isPostback=false;
	protected String title="";
  protected boolean hasRequestParams=false;
  protected int currentPageId= BaseConfig.ROOT_PAGE_ID;
	protected String currentJsp = "/index.jsp";
	protected int processingState = PROCESSED_NONE;

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

	public RequestError getError() {
		return (RequestError) params.get("error");
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

	public void setCurrentPageId(int id){
    currentPageId=id;
  }

	public int getCurrentPageId(){
    return currentPageId;
  }

	public String getCurrentJsp() {
		return currentJsp;
	}

	public void setCurrentJsp(String currentJsp) {
		this.currentJsp = currentJsp;
	}

	public void setCurrentJspFromRequest(HttpServletRequest req) {
		String uri=req.getRequestURI();
		if (uri.toLowerCase().endsWith(".jsp"))
			currentJsp=uri;
	}

	public int getProcessingState() {
		return processingState;
	}

	public void setProcessingState(int processingState) {
		this.processingState = processingState;
	}

	public void resetProcessingState() {
		processingState = PROCESSED_NONE;
	}
}
