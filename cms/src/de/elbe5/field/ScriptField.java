/*
 Elbe 5 CMS  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.field;

import de.elbe5.base.util.XmlUtil;
import de.elbe5.page.PageData;
import de.elbe5.pagepart.PagePartData;
import de.elbe5.servlet.RequestReader;
import de.elbe5.template.TemplateAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;

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
            sb.append(" >").append(toHtmlInput(getCode())).append("</textarea>");
        } else {
            if (getCode().length() == 0) {
                sb.append(defaultContent);
            } else {
                sb.append("<script type=\"text/javascript\">").append(getCode()).append("</script>");
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
