/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.base;

/**
 * Class BaseData is the base class for all data classes. <br>
 * Usage:
 */
public class SizedData extends BaseData {

  protected long dataSize = 0;

  public long getDataSize() {
    return dataSize;
  }

  public String size2str() {
    return String.valueOf(computeDataSize());
  }

  public void setDataSize(long dataSize) {
    this.dataSize = dataSize;
  }

  public long computeDataSize() {
    return MeasureObject.sizeOf(this);
  }


}