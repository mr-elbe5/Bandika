/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

/**
 * Class FileData is a data class holding file data (name, bytes, content type). <br>
 * Usage:
 */
public class FileData extends BaseIdData implements ISizedData {

  public final static String DATAKEY = "data|file";

  protected String fileName = null;
  protected String contentType = null;
  protected boolean image = false;
  protected int size = 0;
  protected int width = 0;
  protected int height = 0;
  protected byte[] bytes = null;

  public FileData() {
  }

  public String getFileName() {
    return fileName;
  }

  public String getExtension() {
    if (fileName == null)
      return null;
    int pos = fileName.lastIndexOf(".");
    if (pos == -1)
      return null;
    return fileName.substring(pos + 1).toLowerCase();
  }

  public String getFileNameWithoutExtension() {
    if (fileName == null)
      return null;
    int pos = fileName.lastIndexOf(".");
    if (pos == -1)
      return fileName;
    return fileName.substring(0, pos);
  }

  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  public String getContentType() {
    return contentType;
  }

  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  public boolean isImage() {
    return image;
  }

  public void setImage(boolean image) {
    this.image = image;
  }

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public int getWidth() {
    return width;
  }

  public void setWidth(int width) {
    this.width = width;
  }

  public int getHeight() {
    return height;
  }

  public void setHeight(int height) {
    this.height = height;
  }

  public boolean hasDimension() {
    return getHeight() > 0 && getWidth() > 0;
  }

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public void clearBytes() {
    bytes = null;
  }

  @Override
  public boolean isComplete() {
    return DataHelper.isComplete(fileName)
      && DataHelper.isComplete(contentType)
      && DataHelper.isComplete(size)
      && DataHelper.isComplete(bytes);
  }

  public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
    if (StringHelper.isNullOrEmtpy(contentType))
      contentType = rdata.getContext().getMimeType(fileName);
  }

}
