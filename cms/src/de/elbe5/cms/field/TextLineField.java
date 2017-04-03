/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.base.util.XmlUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

public class TextLineField extends BaseField {
    public static String FIELDTYPE_TEXTLINE = "textline";

    public static void initialize() {
        BaseField.baseFieldClasses.put(FIELDTYPE_TEXTLINE, TextLineField.class);
    }

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

    @Override
    public Element generateXml(Document doc, Element parent) {
        Element elem = super.generateXml(doc, parent);
        XmlUtil.addCDATA(doc, elem, text);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        text = XmlUtil.getCData(node);
    }

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setText(RequestHelper.getString(request, getIdentifier()));
        return isComplete();
    }
}
