/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2018 Michael Roennau

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

public class TextField extends Field {

    public static String FIELDTYPE_TEXT = "text";

    @Override
    public String getFieldType() {
        return FIELDTYPE_TEXT;
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
    public void appendFieldHtml(PageOutputContext outputContext, PageOutputData outputData) throws IOException {
        StringWriteUtil writer=outputContext.getWriter();
        boolean partEditMode = outputData.getPageData().isPageEditMode() && outputData.getPartData() == outputData.getPageData().getEditPagePart();
        int rows = outputData.getAttributes().getInt("rows");
        if (partEditMode) {
            String content = getText();
            if (content.isEmpty())
                content = outputData.getContent();
            if (rows > 1)
                writer.write("<textarea class=\"editField\" name=\"" + getIdentifier() + "\" rows=\"" + rows + "\" >" + StringUtil.toHtmlInput(content) + "</textarea>");
            else
                writer.write("<input type=\"text\" class=\"editField\" name=\"" + getIdentifier() + "\" value=\"" + StringUtil.toHtmlInput(content) + "\" />");
        } else {
            if (getText().length() == 0) {
                writer.write("&nbsp;");
            } else {
                writer.write(StringUtil.toHtmlText(getText()));
            }
        }
    }

    /******************* XML part *********************************/

    public void createXml(XmlData data, Element parentNode) {
        Element node = data.addNode(parentNode, "field");
        data.addAttribute(node, "fieldType", getFieldType());
        data.addAttribute(node, "name", StringUtil.toXml(getName()));
        data.addCDATA(node, text);
    }

    public void parseXml(XmlData data, Element node) {
        name = data.getStringAttribute(node, "name");
        text = data.getCData(node);
    }

    /******************* search part *********************************/

    public void appendSearchText(StringBuilder sb) {
        sb.append(" ").append(text);
    }

}
