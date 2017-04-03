/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import java.util.ArrayList;

/**
 * Class ImageBaseData is a data class holding image data and its thumbnail. <br>
 * Usage:
 */
public class BinaryBaseData extends VersionedData implements ISizedData {

	protected byte[] bytes = null;
  protected int size=0;
  protected String contentType = null;

  ArrayList<Integer> pageIds=null;

	public BinaryBaseData() {
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

  public int getSize() {
    return size;
  }

  public void setSize(int size) {
    this.size = size;
  }

  public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return contentType;
	}

  public ArrayList<Integer> getPageIds() {
    return pageIds;
  }

  public void setPageIds(ArrayList<Integer> pageIds) {
    this.pageIds = pageIds;
  }
}