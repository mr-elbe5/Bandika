/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.template;

import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PagePartData;
import de.bandika.util.TagAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class TemplateTagPart extends TemplatePart {

    TemplateTagType tagType;
    protected String content="";
    protected TagAttributes attributes=new TagAttributes();

    public TemplateTagPart(TemplateTagType tagType, String content, String attributeString){
        this.tagType=tagType;
        this.content=content;
        attributes.setAttributes(attributeString);
    }

    public void writeTemplatePart(PageContext context, JspWriter writer, HttpServletRequest request, PageData pageData, PagePartData partData) throws IOException {
        tagType.writeTemplatePart(context, writer, request, pageData, partData, content, attributes);
    }

}
