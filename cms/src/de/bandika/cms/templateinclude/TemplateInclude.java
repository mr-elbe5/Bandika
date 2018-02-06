/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.templateinclude;

import de.bandika.base.util.StringUtil;
import de.bandika.cms.page.PageOutputContext;
import de.bandika.cms.page.PageOutputData;
import de.bandika.webbase.util.TagAttributes;

import java.io.IOException;
import java.io.Serializable;
import java.util.Locale;

public abstract class TemplateInclude implements Serializable {

    protected String content="";
    protected TagAttributes attributes;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public TagAttributes getAttributes() {
        return attributes;
    }

    public void setAttributes(TagAttributes attributes) {
        this.attributes = attributes;
    }

    public abstract void writeHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException;

    public void completeOutputData(PageOutputData outputData){
        outputData.attributes=attributes;
        outputData.content=content;
    }

    // conveniance methods

    protected String toHtml(String src) {
        return StringUtil.toHtml(src);
    }

    protected String getHtml(String key, Locale locale) {
        return StringUtil.getHtml(key, locale);
    }

}