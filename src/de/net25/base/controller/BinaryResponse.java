/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base.controller;

/**
 * Class BinaryResponse is the response class for binary data (e.g. images or documents) <br>
 * Usage: create with file name, bytes and content type and return from Controller method
 */
public class BinaryResponse extends Response {

  protected String fileName;
  protected String contentType;
  protected byte[] bytes;

  /**
   * Constructor BinaryResponse creates a new BinaryResponse instance.
   */
  public BinaryResponse() {
  }

  /**
   * Method getType returns the type of this Response object.
   *
   * @return the type (type int) of this Response object.
   */
  public int getType() {
    return Response.TYPE_BINARY;
  }

  /**
   * Constructor BinaryResponse creates a new BinaryResponse instance.
   *
   * @param fileName    of type String
   * @param contentType of type String
   * @param bytes       of type byte[]
   */
  public BinaryResponse(String fileName, String contentType, byte[] bytes) {
    this.fileName = fileName;
    this.contentType = contentType;
    this.bytes = bytes;
  }

  /**
   * Method getFileName returns the fileName of this BinaryResponse object.
   *
   * @return the fileName (type String) of this BinaryResponse object.
   */
  public String getFileName() {
    return fileName;
  }

  /**
   * Method setFileName sets the fileName of this BinaryResponse object.
   *
   * @param fileName the fileName of this BinaryResponse object.
   */
  public void setFileName(String fileName) {
    this.fileName = fileName;
  }

  /**
   * Method getContentType returns the contentType of this BinaryResponse object.
   *
   * @return the contentType (type String) of this BinaryResponse object.
   */
  public String getContentType() {
    return contentType;
  }

  /**
   * Method setContentType sets the contentType of this BinaryResponse object.
   *
   * @param contentType the contentType of this BinaryResponse object.
   */
  public void setContentType(String contentType) {
    this.contentType = contentType;
  }

  /**
   * Method getBytes returns the bytes of this BinaryResponse object.
   *
   * @return the bytes (type byte[]) of this BinaryResponse object.
   */
  public byte[] getBytes() {
    return bytes;
  }

  /**
   * Method setBytes sets the bytes of this BinaryResponse object.
   *
   * @param bytes the bytes of this BinaryResponse object.
   */
  public void setBytes(byte[] bytes) {
    this.bytes = bytes;
  }

}
