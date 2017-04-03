/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.page;

import de.bandika.base.BaseData;
import de.bandika.base.RequestError;
import de.bandika.http.RequestData;
import de.bandika.http.SessionData;

import java.util.ArrayList;

/**
 * Class SortData is the data class for sorting child pages. <br>
 * Usage:
 */
public class PageSortData extends BaseData implements Comparable {

	protected int ranking = 0;
	protected String name = "";
	protected ArrayList<PageSortData> children = new ArrayList<PageSortData>();

	public PageSortData() {
	}

	public int compareTo(Object o) {
		if (!(o instanceof PageSortData))
			return 0;
		PageSortData node = (PageSortData) o;
		return ranking - node.ranking;
	}

	public int getRanking() {
		return ranking;
	}

	public void setRanking(int ranking) {
		this.ranking = ranking;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public ArrayList<PageSortData> getChildren() {
		return children;
	}

	@Override
	public boolean readRequestData(RequestData rdata, SessionData sdata, RequestError err) {
		int idx = rdata.getParamInt("childIdx");
		int childRanking = rdata.getParamInt("childRanking");
		PageSortData child = children.remove(idx);
		if (childRanking >= children.size())
			children.add(child);
		else
			children.add(childRanking, child);
		for (int i = 0; i < children.size(); i++)
			children.get(i).setRanking(i);
		return true;
	}

}