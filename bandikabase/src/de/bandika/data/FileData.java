/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.data;

/**
 * Class FileData is a data class holding file data (name, bytes, content type). <br>
 * Usage:
 */
public class FileData extends BinaryBaseData {

	protected String name = null;

	public FileData(){
	}

	public FileData(String name){
		this.name=name;
	}

	public String getName() {
		return name;
	}

	public String getExtension() {
		if (name == null)
			return null;
		int pos = name.lastIndexOf(".");
		if (pos == -1)
			return null;
		return name.substring(pos + 1).toLowerCase();
	}

	public void setName(String name) {
		this.name = name;
	}

  @Override
  public boolean isComplete() {
    return DataHelper.isComplete(name) && super.isComplete();
  }

}
