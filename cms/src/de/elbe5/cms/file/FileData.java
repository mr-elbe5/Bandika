/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.file;

import de.elbe5.base.data.DataProperties;
import de.elbe5.webserver.servlet.RequestError;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.webserver.tree.ResourceNode;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

public class FileData extends ResourceNode {
    public static int MAX_THUMBNAIL_WIDTH = 200;
    public static int MAX_THUMBNAIL_HEIGHT = 200;
    protected String mediaType = null;
    protected String contentType = null;
    protected int fileSize = 0;
    protected int width = 0;
    protected int height = 0;
    protected String previewContentType = "";
    protected boolean hasPreview = false;
    protected byte[] bytes = null;
    protected byte[] previewBytes = null;
    protected List<Integer> pageIds = new ArrayList<>();

    public FileData() {
    }

    public FileData(FileData source) {
        copyFromTree(source);
    }

    public void fillTreeXml(Document xmlDoc, Element parentNode, boolean withContent) {
        Element node = getElement(xmlDoc, parentNode, "file");
        XmlUtil.addAttribute(xmlDoc, node,"mediaType", StringUtil.toXml(getMediaType()));
        XmlUtil.addAttribute(xmlDoc, node,"contentType", StringUtil.toXml(getContentType()));
        XmlUtil.addIntAttribute(xmlDoc, node,"fileSize", getFileSize());
        XmlUtil.addIntAttribute(xmlDoc, node,"width", getWidth());
        XmlUtil.addIntAttribute(xmlDoc, node,"height", getHeight());
        XmlUtil.addAttribute(xmlDoc, node,"previewContentType", StringUtil.toXml(getPreviewContentType()));
        XmlUtil.addBooleanAttribute(xmlDoc, node,"hasPreview", hasPreview());
    }

    protected void copyFromTree(FileData data) {
        super.copyFromTree(data);
        setMediaType(data.getMediaType());
        setContentType(data.getContentType());
        setFileSize(data.getFileSize());
        setWidth(data.getWidth());
        setHeight(data.getHeight());
        setPreviewContentType(data.getPreviewContentType());
        setHasPreview(data.hasPreview());
        if (data.getBytes() == null) setBytes(null);
        else {
            setBytes(new byte[data.getBytes().length]);
            System.arraycopy(data.getBytes(), 0, getBytes(), 0, data.getBytes().length);
        }
        if (data.getPreviewBytes() == null) setPreviewBytes(null);
        else {
            setPreviewBytes(new byte[data.getPreviewBytes().length]);
            System.arraycopy(data.getPreviewBytes(), 0, getPreviewBytes(), 0, data.getPreviewBytes().length);
        }
        getPageIds().clear();
        getPageIds().addAll(data.getPageIds());
    }

    public String getExtension() {
        if (name == null) return null;
        int pos = name.lastIndexOf('.');
        if (pos == -1) return null;
        return name.substring(pos + 1).toLowerCase();
    }

    public String getFileNameWithoutExtension() {
        if (name == null) return null;
        int pos = name.lastIndexOf('.');
        if (pos == -1) return name;
        return name.substring(0, pos);
    }

    public String getMediaType() {
        return mediaType == null ? "" : mediaType;
    }

    public boolean isImage() {
        return getMediaType().equals("image");
    }

    public void setMediaType(String mediaType) {
        this.mediaType = mediaType.toLowerCase();
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
        if (mediaType == null || mediaType.isEmpty()) {
            int pos = contentType.indexOf('/');
            if (pos != -1) setMediaType(contentType.substring(0, pos));
        }
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
    }

    public int getFileSize() {
        return fileSize;
    }

    public boolean hasPreview() {
        return hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    public String getPreviewContentType() {
        return previewContentType;
    }

    public void setPreviewContentType(String previewContentType) {
        this.previewContentType = previewContentType;
    }

    public byte[] getPreviewBytes() {
        return previewBytes;
    }

    public void setPreviewBytes(byte[] previewBytes) {
        this.previewBytes = previewBytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    @Override
    public boolean isComplete() {
        return isCompleteSettings() && isComplete(bytes);
    }

    public boolean isCompleteSettings() {
        return isComplete(name) && isComplete(mediaType) && isComplete(contentType) && isComplete(fileSize);
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

    public List<Integer> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean isComplete(HttpServletRequest request) {
        RequestError err = null;
        boolean valid = isComplete(name);
        valid &= !isNew() || isComplete(bytes);
        if (!valid) {
            err = new RequestError();
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionHelper.getSessionLocale(request)));
            RequestHelper.setError(request, err);
        }
        return err == null;
    }

    public void prepareSave(HttpServletRequest request) throws Exception {
        super.prepareSave();
        setAuthorName(SessionHelper.getUserName(request));
        if (isImage() && getBytes() != null) {
            BufferedImage source = ImageUtil.createImage(bytes, contentType);
            if (source != null) {
                setWidth(source.getWidth());
                setHeight(source.getHeight());
                float factor = ImageUtil.getResizeFactor(source, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                BufferedImage image = ImageUtil.copyImage(source, factor);
                createJpegPreview(image);
            }
        }
    }

    public void createResizedImage(int width, int height) throws IOException {
        BufferedImage bi = ImageUtil.createResizedImage(getBytes(), getContentType(), width, height);
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType(getContentType());
        if (writers.hasNext()) {
            setContentType(getContentType());
        } else {
            writers = ImageIO.getImageWritersBySuffix(getExtension());
            if (writers.hasNext()) {
                setContentType("");
            } else {
                setName(getFileNameWithoutExtension() + ".jpg");
                writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                setContentType("image/jpeg");
            }
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        writer.write(bi);
        bout.flush();
        bout.close();
        setBytes(bout.toByteArray());
        setFileSize(getBytes().length);
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
    }

    private void createJpegPreview(BufferedImage image) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        ImageWriter writer = writers.next();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        writer.write(image);
        bout.flush();
        bout.close();
        setPreviewBytes(bout.toByteArray());
        setPreviewContentType("image/jpeg");
    }

    protected void fillProperties(DataProperties properties, Locale locale){
        super.fillProperties(properties, locale);
        properties.setKeyHeader("_file", locale);
        properties.addKeyProperty("_mediaType", getMediaType(),locale);
        properties.addKeyProperty("_contentType", getContentType(),locale);
        if (isImage()){
            String val=String.format("<img src=\"/file.srv?act=showPreview&fileId=%s\" alt=\"\" />",getId());
            properties.addHtmlKeyProperty("_preview", val, locale);
        }
    }

}