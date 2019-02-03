/*
 Elbe 5 CMS - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;

import javax.servlet.http.HttpServletRequest;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public final class RequestError {

    public static RequestError getError(HttpServletRequest request) {
        return (RequestError) request.getAttribute(Statics.KEY_ERROR);
    }

    List<String> errorStrings = new ArrayList<>();
    Set<String> errorFields = new HashSet<>();

    public RequestError() {
    }

    public RequestError(String errorKey) {
        addErrorString(errorKey);
    }

    public void setError(HttpServletRequest request) {
        request.setAttribute(Statics.KEY_ERROR, this);
    }

    public void addErrorString(String s) {
        if (!errorStrings.contains(s)) {
            errorStrings.add(s);
        }
    }

    public void addErrorField(String field) {
        if (!errorFields.contains(field)) {
            errorFields.add(field);
        }
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
        return errorFields.isEmpty() && errorStrings.isEmpty();
    }

    public String getErrorString() {
        if (errorStrings.isEmpty()) {
            return null;
        }
        if (errorStrings.size() == 1) {
            return errorStrings.get(0);
        }
        StringBuilder sb = new StringBuilder();
        for (String errorKey : errorStrings) {
            if (sb.length() > 0) {
                sb.append("\n");
            }
            sb.append(errorKey);
        }
        return sb.toString();
    }

}
