/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.image;

import de.bandika.base.CkSelectData;

import java.util.ArrayList;

/**
 * Class ImageSelectData is the data class for image selection. <br>
 * Usage:
 */
public class ImageSelectData extends CkSelectData {

  protected ArrayList<ImageData> images = new ArrayList<ImageData>();
  protected boolean forHtmlEditor = false;
  protected int ckFuncNum=0;

  public int getItems(){
    return images.size();
  }

  public ArrayList<ImageData> getImages() {
    return images;
  }

  public void setImages(ArrayList<ImageData> images) {
    this.images = images;
  }

}
