/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.page;

import de.elbe5.webbase.util.TagAttributes;

import java.util.Locale;

public class PageOutputData {

    public PageOutputData(PageData data, Locale locale){
        pageData=data;
        this.locale=locale;
    }

    private PageData pageData;
    private PagePartData partData=null;
    private Locale locale;
    private TagAttributes attributes=new TagAttributes();
    private String content="";

    public PageData getPageData() {
        return pageData;
    }

    public void setPageData(PageData pageData) {
        this.pageData = pageData;
    }

    public PagePartData getPartData() {
        return partData;
    }

    public void setPartData(PagePartData partData) {
        this.partData = partData;
    }

    public Locale getLocale() {
        return locale!=null ? locale : Locale.ENGLISH;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public TagAttributes getAttributes() {
        return attributes;
    }

    public void clearAttributes() {
        this.attributes.clear();
    }

    public void addAttributes(TagAttributes attributes) {
        this.attributes.putAll(attributes);
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
