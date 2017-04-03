/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content.fields;

import de.net25.base.XmlData;
import de.net25.base.Formatter;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.resources.statics.Statics;

import java.util.Locale;

/**
 * Class ImageField is the data class for editable Fields used as an image area. <br>
 * Usage:
 */
public class ImageField extends BaseField {

  protected int imgId = 0;
  protected int width = 0;
  protected int height = 0;
  protected String altText = "";

  /**
   * Method getImgId returns the imgId of this ImageField object.
   *
   * @return the imgId (type int) of this ImageField object.
   */
  public int getImgId() {
    return imgId;
  }

  /**
   * Method setImgId sets the imgId of this ImageField object.
   *
   * @param imgId the imgId of this ImageField object.
   */
  public void setImgId(int imgId) {
    this.imgId = imgId;
  }

  /**
   * Method getWidth returns the width of this ImageField object.
   *
   * @return the width (type int) of this ImageField object.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Method setWidth sets the width of this ImageField object.
   *
   * @param width the width of this ImageField object.
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Method getHeight returns the height of this ImageField object.
   *
   * @return the height (type int) of this ImageField object.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Method setHeight sets the height of this ImageField object.
   *
   * @param height the height of this ImageField object.
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Method getAltText returns the altText of this ImageField object.
   *
   * @return the altText (type String) of this ImageField object.
   */
  public String getAltText() {
    return altText;
  }

  /**
   * Method setAltText sets the altText of this ImageField object.
   *
   * @param altText the altText of this ImageField object.
   */
  public void setAltText(String altText) {
    this.altText = altText;
  }

  /**
   * Method addNodes
   *
   * @param buffer of type StringBuffer
   */
  protected void addNodes(StringBuffer buffer) {
    super.addNodes(buffer);
    XmlData.addIntNode(buffer, "imgId", imgId);
    XmlData.addIntNode(buffer, "width", width);
    XmlData.addIntNode(buffer, "height", height);
    XmlData.addNode(buffer, "altText", altText);
  }

  /**
   * Method readNodes
   */
  protected void readNodes() {
    super.readNodes();
    imgId = XmlData.getIntNode(xml, "imgId");
    width = XmlData.getIntNode(xml, "width");
    height = XmlData.getIntNode(xml, "height");
    altText = XmlData.getNode(xml, "altText");
  }

  /**
   * Method getHtml returns the html of this BaseField object.
   *
   * @param locale of type Locale
   * @return the html (type String) of this BaseField object.
   */
  @Override
  public String getHtml(Locale locale) {
    StringBuffer buffer = new StringBuffer();
    if (imgId > 0) {
      buffer.append("<img src=\"srv25?ctrl=");
      buffer.append(Statics.KEY_IMAGE);
      buffer.append("&method=show&iid=");
      buffer.append(imgId);
      buffer.append("\" alt=\"");
      buffer.append(altText);
      buffer.append("\"");
      if (width > 0) {
        buffer.append(" width=\"");
        buffer.append(width);
        buffer.append("\"");
      }
      if (height > 0) {
        buffer.append(" height=\"");
        buffer.append(height);
        buffer.append("\"");
      }
      buffer.append("/>");
    } else {
      buffer.append("&nbsp;");
    }
    return buffer.toString();
  }

  /**
   * Method getEditHtml returns the editHtml of this BaseField object.
   *
   * @param locale of type Locale
   * @return the editHtml (type String) of this BaseField object.
   */
  @Override
  public String getEditHtml(Locale locale) {
    StringBuffer buffer = new StringBuffer("<input type=\"hidden\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("ImgId\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("ImgId\" value=\"");
    buffer.append(imgId);
    buffer.append("\" /><input type=\"hidden\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("Width\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("Width\" value=\"");
    buffer.append(width);
    buffer.append("\" /><input type=\"hidden\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("Height\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("Height\" value=\"");
    buffer.append(height);
    buffer.append("\" /><input type=\"hidden\" id=\"");
    buffer.append(getIdentifier());
    buffer.append("Alt\" name=\"");
    buffer.append(getIdentifier());
    buffer.append("Alt\" value=\"");
    buffer.append(Formatter.toHtml(altText));
    buffer.append("\" />");
    buffer.append("<a href=\"#\" onclick=\"return openSetImage('");
    buffer.append(getIdentifier());
    buffer.append("');\">");
    buffer.append("<img id=\"");
    buffer.append(getIdentifier());
    buffer.append("\" src=\"");
    if (imgId > 0) {
      buffer.append("srv25?ctrl=");
      buffer.append(Statics.KEY_IMAGE);
      buffer.append("&method=show&iid=");
      buffer.append(imgId);
      buffer.append("\" alt=\"");
      buffer.append(altText);
      buffer.append("\"");
      if (width > 0) {
        buffer.append(" width=\"");
        buffer.append(width);
        buffer.append("\"");
      }
      if (height > 0) {
        buffer.append(" height=\"");
        buffer.append(height);
        buffer.append("\"");
      }
    } else {
      buffer.append(Statics.IMG_PATH);
      buffer.append("dummy.gif\"");
    }
    buffer.append("/></a>");
    return buffer.toString();
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   */
  @Override
  public void readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    imgId = rdata.getParamInt(getIdentifier() + "ImgId");
    width = rdata.getParamInt(getIdentifier() + "Width");
    height = rdata.getParamInt(getIdentifier() + "Height");
    altText = rdata.getParamString(getIdentifier() + "Alt");
  }

}