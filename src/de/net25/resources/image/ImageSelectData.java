/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.image;

import java.util.ArrayList;

/**
 * Class ImageSelectData is the data class for image selection. <br>
 * Usage:
 */
public class ImageSelectData {

  protected ArrayList<ImageData> images = new ArrayList<ImageData>();
  protected boolean forFck = false;

  /**
   * Method getImages returns the images of this ImageSelectData object.
   *
   * @return the images (type ArrayList<ImageData>) of this ImageSelectData object.
   */
  public ArrayList<ImageData> getImages() {
    return images;
  }

  /**
   * Method setImages sets the images of this ImageSelectData object.
   *
   * @param images the images of this ImageSelectData object.
   */
  public void setImages(ArrayList<ImageData> images) {
    this.images = images;
  }

  /**
   * Method isForFck returns the forFck of this ImageSelectData object.
   *
   * @return the forFck (type boolean) of this ImageSelectData object.
   */
  public boolean isForFck() {
    return forFck;
  }

  /**
   * Method setForFck sets the forFck of this ImageSelectData object.
   *
   * @param forFck the forFck of this ImageSelectData object.
   */
  public void setForFck(boolean forFck) {
    this.forFck = forFck;
  }

}
