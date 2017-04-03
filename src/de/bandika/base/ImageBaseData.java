/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

/**
 * Class ImageBaseData is a data class holding image data and its thumbnail. <br>
 * Usage:
 */
public class ImageBaseData extends BinaryBaseData implements Cloneable, ISizedData {

	protected String imageName = "";
	protected int width = 0;
	protected int height = 0;
	protected byte[] thumbnail = null;
	protected int thumbWidth = 0;
	protected int thumbHeight = 0;

	public ImageBaseData() {
	}

	public Object clone() throws CloneNotSupportedException {
		ImageBaseData obj = (ImageBaseData) super.clone();
		if (bytes != null)
			obj.bytes = bytes.clone();
		if (thumbnail != null)
			obj.thumbnail = thumbnail.clone();
		return obj;
	}

	public String getImageName() {
		return imageName;
	}

	public String getName() {
		return getImageName();
	}

	public void setImageName(String imageName) {
		this.imageName = imageName;
	}

	public String getExtension() {
		if (imageName == null)
			return null;
		int pos = imageName.lastIndexOf(".");
		if (pos == -1)
			return null;
		return imageName.substring(pos + 1).toLowerCase();
	}

	public void setContentType() {
		String ext = getExtension();
		if (ext == null)
			return;
		if ("jpg".equals(ext) || "jpeg".equals(ext) || "pjpeg".equals(ext))
			contentType = "image/jpeg";
		else if ("gif".equals(ext))
			contentType = "image/gif";
	}

  public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public byte[] getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(byte[] thumbnail) {
		this.thumbnail = thumbnail;
	}

	public int getThumbWidth() {
		return thumbWidth;
	}

	public void setThumbWidth(int thumbWidth) {
		this.thumbWidth = thumbWidth;
	}

	public int getThumbHeight() {
		return thumbHeight;
	}

	public void setThumbHeight(int thumbHeight) {
		this.thumbHeight = thumbHeight;
	}

	public void clearBytes() {
		bytes = null;
		thumbnail = null;
	}

}
