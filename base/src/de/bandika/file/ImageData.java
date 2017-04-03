/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika._base.*;
import de.bandika._base.SessionData;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class ImageData extends LinkedFileData {

  public static String DATAKEY = "data|image";

  public static String FILETYPE = "image";

  public boolean readRequestData(RequestData rdata) {
    FileData file = rdata.getParamFile("file");
    if (file != null && file.getBytes() != null
      && file.getFileName().length() > 0
      && !StringHelper.isNullOrEmtpy(file.getContentType())) {
      setBytes(file.getBytes());
      setSize(getBytes().length);
      setFileName(file.getFileName());
      setContentType(file.getContentType());
      int imgWidth = rdata.getParamInt("imgWidth");
      if (imgWidth != 0) {
        try {
          file = ImageTool.createResizedImage(file, imgWidth);
          setBytes(file.getBytes());
          setSize(getBytes().length);
          setFileName(file.getFileName());
          setContentType(file.getContentType());
        } catch (IOException ignore) {
        }
      }
    }
    boolean exclusive = rdata.getParamBoolean("exclusive");
    setPageId(exclusive ? rdata.getParamInt("id") : 0);
    setName(rdata.getParamString("name"));
    return isComplete(rdata);
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    thumbnail = null;
    super.prepareSave(rdata, sdata);
    try {
      if (StringHelper.isNullOrEmtpy(getContentType()) || getBytes() == null)
        return;
      BufferedImage image = ImageTool.createImage(this);
      setWidth(image.getWidth());
      setHeight(image.getHeight());
      if (!hasDimension())
        return;
      thumbnail = ImageTool.createJpegThumbnail(this, image, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
    } catch (Exception e) {
      Logger.info(getClass(), "could not create thumbnail");
    }
  }

}