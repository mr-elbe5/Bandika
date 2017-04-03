/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.document;

import de.net25.base.BaseData;
import de.net25.base.RequestError;
import de.net25.base.resources.FileData;
import de.net25.http.RequestData;
import de.net25.http.SessionData;
import de.net25.resources.statics.Strings;

/**
 * Class DocumentData is the data class for document files. <br>
 * Usage:
 */
public class DocumentData extends BaseData {

  protected String name = null;
  protected String contentType = null;
  protected byte[] bytes = null;
  protected int contentId = 0;

  /**
   * Method getName returns the name of this DocumentData object.
   *
   * @return the name (type String) of this DocumentData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method getExtension returns the extension of this DocumentData object.
   *
   * @return the extension (type String) of this DocumentData object.
   */
  public String getExtension() {
    if (name == null)
      return null;
    int pos = name.lastIndexOf(".");
    if (pos == -1)
      return null;
    return name.substring(pos + 1).toLowerCase();
  }

  /**
   * Method setName sets the name of this DocumentData object.
   *
   * @param name the name of this DocumentData object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getContentType returns the contentType of this DocumentData object.
   *
   * @return the contentType (type String) of this DocumentData object.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Method setContentType sets the contentType of this DocumentData object.
   *
   * @param contentType the contentType of this DocumentData object.
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Method getBytes returns the bytes of this DocumentData object.
   *
   * @return the bytes (type byte[]) of this DocumentData object.
   */
  public byte[] getBytes() {
    return bytes;
  }

  /**
   * Method setBytes sets the bytes of this DocumentData object.
   *
   * @param bytes the bytes of this DocumentData object.
   */
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

  /**
   * Method getContentId returns the contentId of this DocumentData object.
   *
   * @return the contentId (type int) of this DocumentData object.
   */
  public int getContentId() {
    return contentId;
  }

  /**
   * Method setContentId sets the contentId of this DocumentData object.
   *
   * @param contentId the contentId of this DocumentData object.
   */
  public void setContentId(int contentId) {
    this.contentId = contentId;
  }

  /**
   * Method copyMetaData
   *
   * @param doc of type DocumentData
   */
  public void copyMetaData(DocumentData doc) {
    name = doc.name;
    contentType = doc.contentType;
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
    FileData file = rdata.getParamFile("document");
    if (file == null || file.getBytes() == null ||
        file.getName().length() == 0 ||
        file.getContentType() == null || file.getContentType().length() == 0) {
      err.addErrorString(Strings.getString("err_not_complete", sdata.getLocale()));
      return false;
    }
    setBytes(file.getBytes());
    setName(file.getName());
    setContentType(file.getContentType());
    if (!err.isEmpty()) {
      rdata.setError(err);
      return false;
    }
    return true;
  }

}
