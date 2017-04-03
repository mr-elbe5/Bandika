/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.data.FileData;

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

    public static BufferedImage createImage(FileData data) throws IOException {
        BufferedImage image;
        boolean singleImage = true;
        if (data.getContentType().endsWith("gif"))
            singleImage = false;
        Iterator<ImageReader> readers = ImageIO.getImageReadersByMIMEType(data.getContentType());
        ImageReader reader = readers.next();
        ImageInputStream iis = ImageIO.createImageInputStream(new ByteArrayInputStream(data.getBytes()));
        reader.setInput(iis, singleImage);
        image = reader.read(0);
        return image;
    }

    public static FileData createJpegThumbnail(ImageData data, BufferedImage image, int maxWidth, int maxHeight) throws IOException {
        FileData thumbnail = new FileData();
        float wfactor = ((float) maxWidth) / data.getWidth();
        float hfactor = ((float) maxHeight) / data.getHeight();
        float factor = Math.min(1, Math.min(wfactor, hfactor));
        int width = (int) (data.getWidth() * factor);
        int height = (int) (data.getHeight() * factor);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
        g.drawRenderedImage(image, at);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        ImageWriter writer = writers.next();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        writer.write(bi);
        bout.flush();
        bout.close();
        thumbnail.setId(data.getId());
        thumbnail.setFileName("thumbnail.jpg");
        thumbnail.setBytes(bout.toByteArray());
        thumbnail.setContentType("image/jpeg");
        thumbnail.setSize(thumbnail.getBytes().length);
        return thumbnail;
    }

    public static FileData createResizedImage(FileData data, int maxWidth) throws IOException {
        BufferedImage image = ImageTool.createImage(data);
        float factor = ((float) maxWidth) / image.getWidth();
        factor = Math.min(1, factor);
        if (factor == 1)
            return data;
        int width = (int) (image.getWidth() * factor);
        int height = (int) (image.getHeight() * factor);
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        AffineTransform at = AffineTransform.getScaleInstance(factor, factor);
        g.drawRenderedImage(image, at);
        ImageData newFile = new ImageData();
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(data.getContentType());
        if (writers.hasNext()) {
            newFile.setFileName(data.getFileName());
            newFile.setContentType(data.getContentType());
        } else {
            writers = ImageIO.getImageWritersBySuffix(data.getExtension());
            if (writers.hasNext()) {
                newFile.setFileName(data.getFileName());
                newFile.setContentType("");
            } else {
                newFile.setFileName(data.getFileNameWithoutExtension() + ".jpg");
                writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                newFile.setContentType("image/jpeg");
            }
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        writer.write(bi);
        bout.flush();
        bout.close();
        newFile.setId(data.getId());
        newFile.setBytes(bout.toByteArray());
        newFile.setSize(newFile.getBytes().length);
        newFile.setWidth(width);
        newFile.setHeight(height);
        return newFile;
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

    public static void writeImageBytes(byte[] bytes, String path)
            throws IOException {
        File f = new File(path);
        if (f.createNewFile()) {
            FileOutputStream fout = new FileOutputStream(f);
            fout.write(bytes);
            fout.flush();
            fout.close();
        }
    }

}
