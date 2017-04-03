/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika._base;

public class BinaryResponse implements Response {

  protected String fileName;
  protected String contentType;
  protected byte[] bytes;
  protected boolean forceDownload=false;

  public BinaryResponse() {
  }

  public BinaryResponse(String fileName, String contentType, byte[] bytes) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  public BinaryResponse(String fileName, String contentType, byte[] bytes, boolean forceDownload) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.bytes = bytes;
    this.forceDownload=forceDownload;
  }

  public String getFileName() {
    return fileName;
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

  public byte[] getBytes() {
    return bytes;
  }

  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  public boolean isForceDownload() {
    return forceDownload;
  }

  public void setForceDownload(boolean forceDownload) {
    this.forceDownload = forceDownload;
  }
}
