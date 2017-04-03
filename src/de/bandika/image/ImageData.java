/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.image;

import de.bandika.base.*;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.awt.image.BufferedImage;

/**
 * Class ImageData is the data class for image files. <br>
 * Usage:
 */
public class ImageData extends ImageBaseData{

	public boolean prepareImage() {
    BufferedImage image;
    try {
      ImageTool.setContentType(this);
      if (getContentType() == null || getContentType().length() == 0)
        return false;
      if (getBytes() == null)
        return false;
      image = ImageTool.createImage(this);
      ImageTool.createJpegThumbnail(this, image, ImageController.MAX_WIDTH, ImageController.MAX_HEIGHT);
    }
    catch (Exception e) {
      Logger.info(getClass(), "could not process file");
      return false;
    }
    return true;
  }

  public void copyMetaData(ImageData img) {
    imageName = img.imageName;
    contentType = img.contentType;
    size = img.size;
    width = img.width;
    height = img.height;
    thumbWidth = img.thumbWidth;
    thumbHeight = img.thumbHeight;
  }

  @Override
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    FileData file = rdata.getParamFile("image");
    if (file == null || file.getBytes() == null || file.getName().length() == 0 ||
        file.getContentType() == null || file.getContentType().length() == 0) {
      err.addErrorString(AdminStrings.notcomplete);
      return false;
    }
    setBytes(file.getBytes());
    setSize(getBytes().length);
    setImageName(file.getName());
    ImageTool.setContentType(this);
    BufferedImage image;
    try {
      image = ImageTool.createImage(this);
      ImageTool.createJpegThumbnail(this, image, ImageController.MAX_WIDTH, ImageController.MAX_HEIGHT);
    }
    catch (Exception e) {
      setImageName("");
      err.addErrorString(AdminStrings.notcomplete);
    }
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
