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
import java.util.Set;

/**
 * Class ImageField is the data class for editable Fields used as an image area. <br>
 * Usage:
 */
public class ImageField extends BaseField {
    public static String FIELDTYPE_IMAGE = "image";

    public static void initialize() {
        BaseField.baseFieldClasses.put(FIELDTYPE_IMAGE, ImageField.class);
    }

    public String getFieldType() {
        return FIELDTYPE_IMAGE;
    }

    protected int imgId = 0;
    protected String altText = "";

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getAltText() {
        return altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    @Override
    public Element generateXml(Document doc, Element parent) {
        Element elem = super.generateXml(doc, parent);
        XmlUtil.addIntAttribute(doc, elem, "imgId", imgId);
        XmlUtil.addCDATA(doc, elem, altText);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        imgId = XmlUtil.getIntAttribute(node, "imgId");
        altText = XmlUtil.getCData(node);
    }

    @Override
    public void getFileUsage(Set<Integer> list) {
        if (imgId != 0) list.add(imgId);
    }

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        String ident = getIdentifier();
        setImgId(RequestHelper.getInt(request, ident + "ImgId"));
        setAltText(RequestHelper.getString(request, ident + "Alt"));
        return isComplete();
    }
}
