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
import de.bandika.cms.pagepart.PagePartData;
import de.bandika.cms.template.TemplateAttributes;
import de.bandika.servlet.RequestReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspWriter;
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
    public void appendFieldHtml(StringBuilder sb, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData, HttpServletRequest request) {
        boolean partEditMode = pageData.isEditMode() && partData == pageData.getEditPagePart();
        int height = attributes.getInt("height");
        if (partEditMode) {
            sb.append("<textarea class=\"editField\" name=\"").append(getIdentifier()).append("\" rows=\"5\" ");
            if (height == -1) {
                sb.append("style=\"height:").append(height).append("\"");
            }
            sb.append(" >").append(StringUtil.toHtmlInput(getCode())).append("</textarea>");
        } else {
            if (getCode().length() == 0) {
                sb.append(defaultContent);
            } else {
                sb.append("<script type=\"text/javascript\">").append(getCode()).append("</script>");
            }
        }
    }

    @Override
    public void appendFieldHtml(JspWriter writer, TemplateAttributes attributes, String defaultContent, PagePartData partData, PageData pageData, HttpServletRequest request) throws IOException {
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
