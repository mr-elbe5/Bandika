/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms;

import de.bandika.data.XmlHelper;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.SessionData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

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
    protected String width = "";
    protected String height = "";
    protected String altText = "";

    public int getImgId() {
        return imgId;
    }

    public void setImgId(int imgId) {
        this.imgId = imgId;
    }

    public String getWidth() {
        return width;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getHeight() {
        return height;
    }

    public void setHeight(String height) {
        this.height = height;
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
        XmlHelper.createIntAttribute(doc, elem, "imgId", imgId);
        XmlHelper.createAttribute(doc, elem, "width", width);
        XmlHelper.createAttribute(doc, elem, "height", height);
        XmlHelper.createCDATA(doc, elem, altText);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        imgId = XmlHelper.getIntAttribute(node, "imgId");
        width = XmlHelper.getStringAttribute(node, "width");
        height = XmlHelper.getStringAttribute(node, "height");
        altText = XmlHelper.getCData(node);
    }

    @Override
    public void getImageUsage(Set<Integer> list) {
        if (imgId != 0)
            list.add(imgId);
    }

    @Override
    public boolean readPagePartRequestData(RequestData rdata, SessionData sdata) {
        String ident = getIdentifier();
        setImgId(rdata.getInt(ident + "ImgId"));
        setWidth(rdata.getString(ident + "Width"));
        setHeight(rdata.getString(ident + "Height"));
        setAltText(rdata.getString(ident + "Alt"));
        return isComplete(sdata);
    }

}
