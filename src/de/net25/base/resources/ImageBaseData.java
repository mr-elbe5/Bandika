/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.resources;

import de.net25.base.BaseData;
import de.net25.base.SizedData;

/**
 * Class ImageBaseData is a data class holding image data and its thumbnail. <br>
 * Usage:
 */
public class ImageBaseData extends SizedData implements Cloneable {

  protected byte[] bytes = null;
  protected String imageName = "";
  protected String contentType = null;
  protected int width = 0;
  protected int height = 0;
  protected byte[] thumbnail = null;
  protected int thumbWidth = 0;
  protected int thumbHeight = 0;

  /**
   * Constructor ImageBaseData creates a new ImageBaseData instance.
   */
  public ImageBaseData() {
  }

  /**
   * Method clone
   *
   * @return Object
   * @throws CloneNotSupportedException when data processing is not successful
   */
  public Object clone() throws CloneNotSupportedException {
    ImageBaseData obj = (ImageBaseData) super.clone();
    if (bytes != null)
      obj.bytes = bytes.clone();
    if (thumbnail != null)
      obj.thumbnail = thumbnail.clone();
    return obj;
  }

  /**
   * Method getBytes returns the bytes of this ImageBaseData object.
   *
   * @return the bytes (type byte[]) of this ImageBaseData object.
   */
  public byte[] getBytes() {
    return bytes;
  }

  /**
   * Method setBytes sets the bytes of this ImageBaseData object.
   *
   * @param bytes the bytes of this ImageBaseData object.
   */
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  /**
   * Method getImageName returns the imageName of this ImageBaseData object.
   *
   * @return the imageName (type String) of this ImageBaseData object.
   */
  public String getImageName() {
    return imageName;
  }

  /**
   * Method getName returns the name of this ImageBaseData object.
   *
   * @return the name (type String) of this ImageBaseData object.
   */
  public String getName() {
    return getImageName();
  }

  /**
   * Method setImageName sets the imageName of this ImageBaseData object.
   *
   * @param imageName the imageName of this ImageBaseData object.
   */
  public void setImageName(String imageName) {
    this.imageName = imageName;
  }

  /**
   * Method getExtension returns the extension of this ImageBaseData object.
   *
   * @return the extension (type String) of this ImageBaseData object.
   */
  public String getExtension() {
    if (imageName == null)
      return null;
    int pos = imageName.lastIndexOf(".");
    if (pos == -1)
      return null;
    return imageName.substring(pos + 1).toLowerCase();
  }

  /**
   * Method getContentType returns the contentType of this ImageBaseData object.
   *
   * @return the contentType (type String) of this ImageBaseData object.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Method setContentType sets the contentType of this ImageBaseData object.
   *
   * @param contentType the contentType of this ImageBaseData object.
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Method setContentType
   */
  public void setContentType() {
    String ext = getExtension();
    if (ext == null)
      return;
    if ("jpg".equals(ext) || "jpeg".equals(ext) || "pjpeg".equals(ext))
      contentType = "image/jpeg";
    else if ("gif".equals(ext))
      contentType = "image/gif";
  }

  /**
   * Method getWidth returns the width of this ImageBaseData object.
   *
   * @return the width (type int) of this ImageBaseData object.
   */
  public int getWidth() {
    return width;
  }

  /**
   * Method setWidth sets the width of this ImageBaseData object.
   *
   * @param width the width of this ImageBaseData object.
   */
  public void setWidth(int width) {
    this.width = width;
  }

  /**
   * Method getHeight returns the height of this ImageBaseData object.
   *
   * @return the height (type int) of this ImageBaseData object.
   */
  public int getHeight() {
    return height;
  }

  /**
   * Method setHeight sets the height of this ImageBaseData object.
   *
   * @param height the height of this ImageBaseData object.
   */
  public void setHeight(int height) {
    this.height = height;
  }

  /**
   * Method getThumbnail returns the thumbnail of this ImageBaseData object.
   *
   * @return the thumbnail (type byte[]) of this ImageBaseData object.
   */
  public byte[] getThumbnail() {
    return thumbnail;
  }

  /**
   * Method setThumbnail sets the thumbnail of this ImageBaseData object.
   *
   * @param thumbnail the thumbnail of this ImageBaseData object.
   */
  public void setThumbnail(byte[] thumbnail) {
    this.thumbnail = thumbnail;
  }

  /**
   * Method getThumbWidth returns the thumbWidth of this ImageBaseData object.
   *
   * @return the thumbWidth (type int) of this ImageBaseData object.
   */
  public int getThumbWidth() {
    return thumbWidth;
  }

  /**
   * Method setThumbWidth sets the thumbWidth of this ImageBaseData object.
   *
   * @param thumbWidth the thumbWidth of this ImageBaseData object.
   */
  public void setThumbWidth(int thumbWidth) {
    this.thumbWidth = thumbWidth;
  }

  /**
   * Method getThumbHeight returns the thumbHeight of this ImageBaseData object.
   *
   * @return the thumbHeight (type int) of this ImageBaseData object.
   */
  public int getThumbHeight() {
    return thumbHeight;
  }

  /**
   * Method setThumbHeight sets the thumbHeight of this ImageBaseData object.
   *
   * @param thumbHeight the thumbHeight of this ImageBaseData object.
   */
  public void setThumbHeight(int thumbHeight) {
    this.thumbHeight = thumbHeight;
  }

  /**
   * Method clearBytes
   */
  public void clearBytes() {
    bytes = null;
    thumbnail = null;
  }

}
