/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.webbase.servlet.RequestReader;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import java.io.IOException;
import java.io.StringWriter;

public class PageOutputContext {

    private PageContext context;
    private StringWriteUtil writer;

    public PageOutputContext(PageContext context){
        this.context=context;
        writer=context!=null ? new StringWriteUtil(context.getOut()) : null;
    }

    public PageOutputContext(StringWriter stringWriter){
        this.context=null;
        writer=new StringWriteUtil(stringWriter);
    }

    // consumer should declare itself as dynamic
    public void includeJsp(String jsp) throws ServletException, IOException {
        if (context!=null)
            context.include(jsp);
    }

    // consumer should (probably) declare itself as dynamic
    public String getParamString(String key){
        if (context!=null)
            return RequestReader.getString((HttpServletRequest) context.getRequest(), key);
        return "";
    }

    // consumer should declare itself as dynamic
    public HttpServletRequest getRequest(){
        return context!=null ? (HttpServletRequest) context.getRequest() : null;
    }

    public StringWriteUtil getWriter() {
        return writer;
    }

}
