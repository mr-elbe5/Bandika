/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.webbase.servlet.RequestError;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Writer;

public class MessageControl extends TemplateInclude {

    public static final String KEY = "message";

    private static MessageControl instance = null;

    public static MessageControl getInstance() {
        if (instance == null)
            instance = new MessageControl();
        return instance;
    }

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        Writer writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        RequestError error = RequestError.getError(request);
        String message = RequestReader.getMessage(request);
        if (error != null) {
            writer.write("<div class=\"error\">" + toHtml(error.getErrorString()) + "<button type=\"button\" class=\"close\" onclick=\"$(this).closest('.error').hide();\">&times;</button></div>");
        } else if (message != null && message.length() > 0) {
            writer.write("<div class=\"message\">" + toHtml(message) + "<button type=\"button\" class=\"close\" onclick=\"$(this).closest('.message').hide();\">&times;</button></div>");
        }
    }

}
