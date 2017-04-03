/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009-2011 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.bandika.base;

import de.bandika.data.ImageBaseData;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;

/**
 * Class ImageTool is a helper class for image manipulation. <br>
 * Usage:
 */
public class ImageTool {

	public static BufferedImage createImage(ImageBaseData img) throws IOException {
		BufferedImage image;
		boolean singleImage = true;
		if (img.getContentType().endsWith("gif"))
			singleImage = false;
		Iterator readers = ImageIO.getImageReadersByMIMEType(img.getContentType());
		ImageReader reader = (ImageReader) readers.next();
		ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(img.getBytes()));
		reader.setInput(iis, singleImage);
		image = reader.read(0);
		img.setWidth(reader.getWidth(0));
		img.setHeight(reader.getHeight(0));
		return image;
	}

	public static boolean createJpegThumbnail(ImageBaseData img, BufferedImage image, int maxWidth, int maxHeight) throws IOException {
		float wfactor = ((float) maxWidth) / img.getWidth();
		float hfactor = ((float) maxHeight) / img.getHeight();
		float factor = Math.min(wfactor, hfactor);
		if (factor > 1) {
			img.setThumbnail(img.getBytes());
			img.setThumbWidth(img.getWidth());
			img.setThumbHeight(img.getHeight());
			return false;
		}
		int width = (int) (img.getWidth() * factor);
		int height = (int) (img.getHeight() * factor);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
		g.drawRenderedImage(image, at);
		Iterator writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
		ImageWriter writer = (ImageWriter) writers.next();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
		writer.setOutput(ios);
		writer.write(bi);
		bout.flush();
		bout.close();
		img.setThumbnail(bout.toByteArray());
		img.setThumbWidth(width);
		img.setThumbHeight(height);
		return true;
	}

	public static BufferedImage shrinkImage(ImageBaseData img, BufferedImage image, int maxWidth, int maxHeight) throws IOException {
		float wfactor = ((float) maxWidth) / img.getWidth();
		float hfactor = ((float) maxHeight) / img.getHeight();
		float factor = Math.min(wfactor, hfactor);
		if (factor > 1) {
			return image;
		}
		int width = (int) (img.getWidth() * factor);
		int height = (int) (img.getHeight() * factor);
		BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		Graphics2D g = bi.createGraphics();
		g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
		AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
		g.drawRenderedImage(image, at);
		Iterator writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
		ImageWriter writer = (ImageWriter) writers.next();
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
		writer.setOutput(ios);
		writer.write(bi);
		bout.flush();
		bout.close();
		img.setBytes(bout.toByteArray());
		img.setWidth(width);
		img.setHeight(height);
		int pos = img.getImageName().lastIndexOf('.');
		if (pos != -1)
			img.setImageName(img.getImageName().substring(0, pos) + ".jpeg");
		return bi;
	}

	public static byte[] readImageBytes(String path) throws IOException {
		File f = new File(path);
		long size = f.length();
		byte[] bytes = new byte[(int) size];
		FileInputStream fin = new FileInputStream(f);
		if (fin.read(bytes) <= 0)
			throw new IOException("could not read file " + path);
		return bytes;
	}

	public static void writeImageBytes(byte[] bytes, String path) throws IOException {
		File f = new File(path);
		if (f.createNewFile()) {
			FileOutputStream fout = new FileOutputStream(f);
			fout.write(bytes);
			fout.flush();
			fout.close();
		}
	}

	public static void setContentType(ImageBaseData img) {
		String str = img.getImageName().toLowerCase();
		if (str.endsWith("gif"))
			img.setContentType("image/gif");
		else if (str.endsWith("jpg") || str.endsWith("jpeg"))
			img.setContentType("image/jpeg");
		else if (str.endsWith("png"))
			img.setContentType("image/png");
		else if (str.endsWith("bmp"))
			img.setContentType("image/bmp");
	}


}
