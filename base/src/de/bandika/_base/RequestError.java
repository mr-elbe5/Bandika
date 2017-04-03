/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

import java.util.ArrayList;
import java.util.HashSet;
import java.io.StringWriter;
import java.io.PrintWriter;

/**
 * Class RequestError is a data class holding error messages from request
 * evaluation. <br>
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

  public RequestError(Exception e) {
    addError(e);
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

  public void addError(Exception e) {
    if (e != null) {
      errorStrings.add(e.getMessage());
      StringWriter sWriter = new StringWriter();
      PrintWriter pWriter = new PrintWriter(sWriter);
      e.printStackTrace(pWriter);
      pWriter.flush();
      errorStrings.add(sWriter.toString());
    }
  }

  public boolean isEmpty() {
    return errorStrings.isEmpty();
  }

  public String getErrorString() {
    if (errorStrings.size() == 0)
      return null;
    if (errorStrings.size() == 1)
      return errorStrings.get(0);
    StringBuffer buffer = new StringBuffer();
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
