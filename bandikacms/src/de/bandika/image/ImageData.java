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
import de.bandika.data.ImageBaseData;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

/**
 * Class ImageData is the data class for image files. <br>
 * Usage:
 */
public class ImageData extends ImageBaseData {

  ArrayList<Integer> pageIds=null;

  public ArrayList<Integer> getPageIds() {
    return pageIds;
  }

  public void setPageIds(ArrayList<Integer> pageIds) {
    this.pageIds = pageIds;
  }

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

}
