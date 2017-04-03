/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

import de.net25.http.RequestData;
import de.net25.http.SessionData;

/**
 * Class BaseData is the base class for all data classes. <br>
 * Usage:
 */
public class BaseData {

  protected int id = 0;
  protected int version = 0;
  protected boolean beingCreated = false;

  /**
   * Method getId returns the id of this BaseData object.
   *
   * @return the id (type int) of this BaseData object.
   */
  public int getId() {
    return id;
  }

  /**
   * Method setId sets the id of this BaseData object.
   *
   * @param id the id of this BaseData object.
   */
  public void setId(int id) {
    this.id = id;
  }

  /**
   * Method getVersion returns the version of this BaseData object.
   *
   * @return the version (type int) of this BaseData object.
   */
  public int getVersion() {
    return version;
  }

  /**
   * Method setVersion sets the version of this BaseData object.
   *
   * @param version the version of this BaseData object.
   */
  public void setVersion(int version) {
    this.version = version;
  }

  /**
   * Method increaseVersion increases version by 1
   */
  public void increaseVersion() {
    version++;
  }

  /**
   * Method isBeingCreated returns the beingCreated of this BaseData object.
   *
   * @return the beingCreated (type boolean) of this BaseData object.
   */
  public boolean isBeingCreated() {
    return beingCreated;
  }

  /**
   * Method setBeingCreated sets the beingCreated of this BaseData object.
   *
   * @param beingCreated the beingCreated of this BaseData object.
   */
  public void setBeingCreated(boolean beingCreated) {
    this.beingCreated = beingCreated;
  }

  /**
   * Method prepareEditing
   *
   * @throws Exception when data processing is not successful
   */
  public void prepareEditing() throws Exception {
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @return boolean
   */
  public boolean readRequestData(RequestData rdata, SessionData sdata) {
    RequestError err = new RequestError();
    return readRequestData(rdata, sdata, err);
  }

  /**
   * Method readRequestData
   *
   * @param rdata of type RequestData
   * @param sdata of type SessionData
   * @param err   of type RequestError
   * @return boolean
   */
  public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
    return err.isEmpty();
  }

  /**
   * Method isComplete
   *
   * @param s of type String
   * @return boolean
   */
  public static boolean isComplete(String s) {
    return s != null && s.length() > 0;
  }

  /**
   * Method isComplete
   *
   * @param i of type int
   * @return boolean
   */
  public static boolean isComplete(int i) {
    return i != 0;
  }

}
