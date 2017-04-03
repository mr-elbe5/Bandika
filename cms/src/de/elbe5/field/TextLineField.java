/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.field;

import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.template.TemplateAttributes;
import de.elbe5.servlet.RequestReader;
import de.elbe5.base.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

public class TextLineField extends Field {

    public static String FIELDTYPE_TEXTLINE = "textLine";

    @Override
    public String getFieldType() {
        return FIELDTYPE_TEXTLINE;
    }

    protected String text = "";

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    /******************* HTML part *********************************/

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setText(RequestReader.getString(request, getIdentifier()));
        return isComplete();
    }

    @Override
    public void appendFieldHtml(StringBuilder sb, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData, HttpServletRequest request) {
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        if (partEditMode) {
            String content=getText();
            if (content.isEmpty())
                content=defaultContent;
            sb.append("<input type=\"text\" class=\"editField\" name=\"").append(getIdentifier()).append("\" value=\"").append(toHtmlInput(content)).append("\" />");
        } else {
            if (getText().length() == 0) {
                sb.append("&nbsp;");
            } else {
                sb.append(toHtml(getText()));
            }
        }
    }

    /******************* XML part *********************************/

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, text);
        return node;
    }

    @Override
    public void fromXml(Element node) {
        super.fromXml(node);
        text = XmlUtil.getCData(node);
    }

}
