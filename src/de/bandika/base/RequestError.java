/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.http.SessionData;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class RequestError is a data class holding error messages from request evaluation. <br>
 * Usage:
 */
public class RequestError {

  ArrayList<String> errorStrings = new ArrayList<String>();
  HashSet<String> errorFields = new HashSet<String>();

  public RequestError() {
  }

  public RequestError(String errorKey) {
    addErrorString(errorKey);
  }

  public void addErrorString(String s) {
    if (!errorStrings.contains(s))
      errorStrings.add(s);
  }

  public void addErrorField(String field) {
    if (!errorFields.contains(field))
      errorFields.add(field);
  }

   public void addError(String s, String field) {
    addErrorString(s);
    addErrorField(field);
  }

  public boolean isEmpty() {
    return errorStrings.isEmpty();
  }

  public String getErrorString(SessionData sdata) {
    if (errorStrings.size() == 0)
      return null;
    if (errorStrings.size() == 1)
      return errorStrings.get(0);
    StringBuffer buffer = new StringBuffer();
    String temp;
    for (String errorKey : errorStrings) {
      if (buffer.length() > 0)
        buffer.append("\n");
      buffer.append(errorKey);
    }
    return buffer.toString();
  }

  public String[] getErrorFields() {
    return (String[]) errorFields.toArray();
  }

  public boolean isErrorField(String name) {
    return errorFields.contains(name);
  }

}
