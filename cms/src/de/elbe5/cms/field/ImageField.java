/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

public class ImageField extends HtmlBaseField {

    public static String FIELDTYPE_IMAGE = "image";

    @Override
    public String getFieldType() {
        return FIELDTYPE_IMAGE;
    }

    protected String html = "";

    /******************* HTML part *********************************/

    @Override
    protected String getCKCODE(){
        return "<div class=\"ckeditField\" id=\"%s\" contenteditable=\"true\">%s</div>" +
            "<input type=\"hidden\" name=\"%s\" value=\"%s\" />" +
            "<script type=\"text/javascript\">$('#%s').ckeditor({" +
            "toolbar : 'Image'," +
            "filebrowserBrowseUrl : '/field.srv?act=openLinkBrowser&siteId=%s&pageId=%s'," +
            "filebrowserImageBrowseUrl : '/field.srv?act=openImageBrowser&siteId=%s&pageId=%s'" +
            "});" +
            "</script>";
    }


}
