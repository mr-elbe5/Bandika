/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.field;

import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.page.PageData;
import de.bandika.cms.page.PagePartData;
import de.bandika.webbase.servlet.RequestReader;
import de.bandika.webbase.util.TagAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import java.io.IOException;

public class ScriptField extends Field {

    public static String FIELDTYPE_SCRIPT = "script";

    @Override
    public String getFieldType() {
        return FIELDTYPE_SCRIPT;
    }

    protected String code = "";

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    /******************* HTML part *********************************/

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        setCode(RequestReader.getString(request, getIdentifier()));
        return isComplete();
    }

    @Override
    public void appendFieldHtml(PageContext context, JspWriter writer, HttpServletRequest request, TagAttributes attributes, String defaultContent, PagePartData partData, PageData pageData) throws IOException {
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        int height = attributes.getInt("height");
        if (partEditMode) {
            writer.write("<textarea class=\"editField\" name=\"" + getIdentifier() + "\" rows=\"5\" ");
            if (height == -1) {
                writer.write("style=\"height:" + height + "\"");
            }
            writer.write(" >" + StringUtil.toHtmlInput(getCode()) + "</textarea>");
        } else {
            if (getCode().length() == 0) {
                writer.write(defaultContent);
            } else {
                writer.write("<script type=\"text/javascript\">" + getCode() + "</script>");
            }
        }
    }

    /******************* XML part *********************************/

    @Override
    public Element toXml(Document xmlDoc, Element parentNode) {
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, code);
        return node;
    }

    @Override
    public void fromXml(Element node) {
        super.fromXml(node);
        code = XmlUtil.getCData(node);
    }

}
