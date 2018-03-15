/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.field;

import de.elbe5.base.data.XmlData;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.StringWriteUtil;
import de.elbe5.cms.page.PageOutputContext;
import de.elbe5.cms.page.PageOutputData;
import de.elbe5.webbase.servlet.RequestReader;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
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
    public void appendFieldHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        boolean partEditMode = outputData.pageData.isPageEditMode() && outputData.partData == outputData.pageData.getEditPagePart();
        int height = outputData.attributes.getInt("height");
        if (partEditMode) {
            writer.write("<textarea class=\"editField\" name=\"{1}\" rows=\"5\" ",
                    getIdentifier());
            if (height == -1) {
                writer.write("style=\"height:{1}\"",
                        String.valueOf(height));
            }
            writer.write(" >{1}</textarea>",
                    StringUtil.toHtmlInput(getCode()));
        } else {
            if (getCode().length() == 0) {
                writer.write(outputData.content);
            } else {
                writer.write("<script type=\"text/javascript\">" + getCode() + "</script>");
            }
        }
    }

    /******************* XML part *********************************/

    public void createXml(XmlData data, Element parentNode) {
        Element node = data.addNode(parentNode, "field");
        data.addAttribute(node, "fieldType", getFieldType());
        data.addAttribute(node, "name", StringUtil.toXml(getName()));
        data.addCDATA(node, code);
    }

    public void parseXml(XmlData data, Element node) {
        name = data.getStringAttribute(node, "name");
        code = data.getCData(node);
    }

}
