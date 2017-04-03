/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.content;

import de.net25.base.BaseData;
import de.net25.base.RequestError;
import de.net25.http.RequestData;
import de.net25.http.SessionData;

import java.util.*;

/**
 * Class SortData is the data class for sorting child pages. <br>
 * Usage:
 */
public class SortData extends BaseData implements Comparable {

  protected int ranking = 0;
  protected String name = "";
  protected ArrayList<SortData> children = new ArrayList<SortData>();

  /**
   * Constructor SortData creates a new SortData instance.
   */
  public SortData() {
  }

  /**
   * Method compareTo
   *
   * @param o of type Object
   * @return int
   */
  public int compareTo(Object o) {
    if (!(o instanceof SortData))
      return 0;
    SortData node = (SortData) o;
    return ranking - node.ranking;
  }

  /**
   * Method getRanking returns the ranking of this SortData object.
   *
   * @return the ranking (type int) of this SortData object.
   */
  public int getRanking() {
    return ranking;
  }

  /**
   * Method setRanking sets the ranking of this SortData object.
   *
   * @param ranking the ranking of this SortData object.
   */
  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  /**
   * Method getName returns the name of this SortData object.
   *
   * @return the name (type String) of this SortData object.
   */
  public String getName() {
    return name;
  }

  /**
   * Method setName sets the name of this SortData object.
   *
   * @param name the name of this SortData object.
   */
  public void setName(String name) {
    this.name = name;
  }

  /**
   * Method getChildren returns the children of this SortData object.
   *
   * @return the children (type ArrayList<SortData>) of this SortData object.
   */
  public ArrayList<SortData> getChildren() {
    return children;
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
    int idx = rdata.getParamInt("childIdx");
    int childRanking = rdata.getParamInt("childRanking");
    SortData child = children.remove(idx);
    if (childRanking >= children.size())
      children.add(child);
    else
      children.add(childRanking, child);
    for (int i = 0; i < children.size(); i++)
      children.get(i).setRanking(i);
    return true;
  }

}