/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.image;

import de.net25.base.RequestError;
import de.net25.base.resources.FileData;
import de.net25.base.resources.ImageBaseData;
import de.net25.base.resources.ImageTool;
import de.net25.base.Logger;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.resources.statics.Strings;

import java.awt.image.BufferedImage;

/**
 * Class ImageData is the data class for image files. <br>
 * Usage:
 */
public class ImageData extends ImageBaseData {

  public static int MAX_WIDTH = 100;
  public static int MAX_HEIGHT = 100;
  public static int MAX_PAGE_IMAGES = 100;

  protected int contentId = 0;

  /**
   * Method getContentId returns the contentId of this ImageData object.
   *
   * @return the contentId (type int) of this ImageData object.
   */
  public int getContentId() {
    return contentId;
  }

  /**
   * Method setContentId sets the contentId of this ImageData object.
   *
   * @param contentId the contentId of this ImageData object.
   */
  public void setContentId(int contentId) {
    this.contentId = contentId;
  }

  /**
   * Method prepareImage
   *
   * @return boolean
   */
  public boolean prepareImage() {
    BufferedImage image;
    try {
      ImageTool.setContentType(this);
      if (getContentType() == null || getContentType().length() == 0)
        return false;
      if (getBytes() == null)
        return false;
      image = ImageTool.createImage(this);
      ImageTool.createJpegThumbnail(this, image, MAX_WIDTH, MAX_HEIGHT);
    }
    catch (Exception e) {
      Logger.info(getClass(), "could not process file");
      return false;
    }
    return true;
  }

  /**
   * Method copyMetaData
   *
   * @param img of type ImageData
   */
  public void copyMetaData(ImageData img) {
    imageName = img.imageName;
    contentType = img.contentType;
    width = img.width;
    height = img.height;
    thumbWidth = img.thumbWidth;
    thumbHeight = img.thumbHeight;
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   * @return boolean
   */
  @Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    FileData file = rdata.getParamFile("image");
    if (file == null || file.getBytes() == null || file.getName().length() == 0 ||
        file.getContentType() == null || file.getContentType().length() == 0) {
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      return false;
    }
    setBytes(file.getBytes());
    setImageName(file.getName());
    ImageTool.setContentType(this);
    BufferedImage image;
    try {
      image = ImageTool.createImage(this);
      ImageTool.createJpegThumbnail(this, image, MAX_WIDTH, MAX_HEIGHT);
    }
    catch (Exception e) {
      setImageName("");
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
    }
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
