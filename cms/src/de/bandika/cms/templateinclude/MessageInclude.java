/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringWriteUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.webbase.servlet.RequestError;
import de.bandika.webbase.servlet.RequestReader;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class MessageInclude extends TemplateInclude {

    public static final String KEY = "message";

    private static MessageInclude instance = null;

    public static MessageInclude getInstance() {
        if (instance == null)
            instance = new MessageInclude();
        return instance;
    }

    public boolean isDynamic(){
        return true;
    }

    public void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        HttpServletRequest request=outputContext.getRequest();
        RequestError error = RequestError.getError(request);
        String message = RequestReader.getMessage(request);
        if (error != null) {
            writer.write("<div class=\"error\">{1}<button type=\"button\" class=\"close\" onclick=\"$(this).closest('.error').hide();\">&times;</button></div>",
                    toHtml(error.getErrorString()));
        } else if (message != null && message.length() > 0) {
            writer.write("<div class=\"message\">{1}<button type=\"button\" class=\"close\" onclick=\"$(this).closest('.message').hide();\">&times;</button></div>",
                    toHtml(message));
        }
    }

}