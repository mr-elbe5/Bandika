/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.exception;

import javax.servlet.http.HttpServletResponse;

/**
 * Class HttpException is the Exception class resulting in HTTP codes. <br>
 * Usage: Set HTTP error code and throw it.
 */
public class HttpException extends Exception {
  private static final long serialVersionUID = 1L;

  protected int errorCode = HttpServletResponse.SC_ACCEPTED;

  /**
   * Constructor HttpException creates a new HttpException instance.
   *
   * @param code of type int
   */
  public HttpException(int code) {
    errorCode = code;
  }

  /**
   * Method getErrorCode returns the errorCode of this HttpException object.
   *
   * @return the errorCode (type int) of this HttpException object.
   */
  public int getErrorCode() {
    return errorCode;
  }

  /**
   * Method getMessage returns the message of this HttpException object.
   *
   * @return the message (type String) of this HttpException object.
   */
  public String getMessage() {
    return Integer.toString(errorCode);
  }

}

