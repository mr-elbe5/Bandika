/*
  Bandika! - A Java based modular Framework including Content Management and other features
  Copyright (C) 2009-2012 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.link;

import de.bandika._base.BaseData;

import java.util.HashSet;

public class LinkData extends BaseData {

  public final static String DATAKEY = "data|link";

  protected String linkKey = "";
  protected String link = "";
  protected int ranking = 0;

  protected HashSet<Integer> groupIds = new HashSet<Integer>();

  public LinkData() {
  }

  public String getLinkKey() {
    return linkKey;
  }

  public void setLinkKey(String linkKey) {
    this.linkKey = linkKey;
  }

  public String getLink() {
    return link;
  }

  public void setLink(String link) {
    this.link = link;
  }

  public int getRanking() {
    return ranking;
  }

  public void setRanking(int ranking) {
    this.ranking = ranking;
  }

  public HashSet<Integer> getGroupIds() {
    return groupIds;
  }

  public boolean hasGroupId(int id) {
    return groupIds.contains(id);
  }

}
