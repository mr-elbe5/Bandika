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

public class ImageLinkField extends BaseField {
    public static String FIELDTYPE_IMAGELINK = "imagelink";

    public static void initialize() {
        BaseField.baseFieldClasses.put(FIELDTYPE_IMAGELINK, ImageLinkField.class);
    }

    public String getFieldType() {
        return FIELDTYPE_IMAGELINK;
    }

    protected int imgId = 0;
    protected String altText = "";
    protected String link = "";
    protected String target = "";

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

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    @Override
    public Element generateXml(Document doc, Element parent) {
        Element elem = super.generateXml(doc, parent);
        XmlUtil.addIntAttribute(doc, elem, "imgId", imgId);
        XmlUtil.addAttribute(doc, elem, "link", link);
        XmlUtil.addAttribute(doc, elem, "target", target);
        XmlUtil.addCDATA(doc, elem, altText);
        return elem;
    }

    @Override
    public void evaluateXml(Element node) {
        super.evaluateXml(node);
        imgId = XmlUtil.getIntAttribute(node, "imgId");
        link = XmlUtil.getStringAttribute(node, "link");
        target = XmlUtil.getStringAttribute(node, "target");
        altText = XmlUtil.getCData(node);
    }

    @Override
    public void getFileUsage(Set<Integer> list) {
        if (imgId != 0) list.add(imgId);
        String fileLink = "/file.srv?method=show&fileId=";
        int pos = link.indexOf(fileLink);
        if (pos != -1) {
            pos += fileLink.length();
            int fid = 0;
            try {
                fid = Integer.parseInt(fileLink.substring(pos));
            } catch (Exception ignore) {
            }
            if (fid > 0) list.add(fid);
        }
    }

    @Override
    public void getPageUsage(Set<Integer> list) {
        String pageLink = "/page.srv?method=show&pageId=";
        int pos = link.indexOf(pageLink);
        if (pos != -1) {
            pos += pageLink.length();
            int fid = 0;
            try {
                fid = Integer.parseInt(pageLink.substring(pos));
            } catch (Exception ignore) {
            }
            if (fid > 0) list.add(fid);
        }
    }

    @Override
    public boolean readPagePartRequestData(HttpServletRequest request) {
        String ident = getIdentifier();
        setLink(RequestHelper.getString(request, ident + "Link"));
        setTarget(RequestHelper.getString(request, ident + "Target"));
        setImgId(RequestHelper.getInt(request, ident + "ImgId"));
        setAltText(RequestHelper.getString(request, ident + "Alt"));
        return isComplete();
    }
}
