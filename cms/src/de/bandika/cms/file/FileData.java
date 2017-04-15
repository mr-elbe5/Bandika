/*
 Bandika  - A Java based modular Content Management System
 Copyright (C) 2009-2017 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.cms.file;

import de.bandika.base.data.BinaryFileData;
import de.bandika.base.log.Log;
import de.bandika.base.search.ISearchTextProvider;
import de.bandika.base.util.FileUtil;
import de.bandika.base.util.ImageUtil;
import de.bandika.base.util.StringUtil;
import de.bandika.base.util.XmlUtil;
import de.bandika.cms.tree.ResourceNode;
import de.bandika.cms.search.SearchHelper;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.RequestReader;
import de.bandika.servlet.SessionReader;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Iterator;
import java.util.List;

public class FileData extends ResourceNode implements ISearchTextProvider {

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

    public void copy(FileData data) {
        super.copy(data);
        setMediaType(data.getMediaType());
        setContentType(data.getContentType());
        setFileSize(data.getFileSize());
        setWidth(data.getWidth());
        setHeight(data.getHeight());
        setPreviewContentType(data.getPreviewContentType());
        setHasPreview(data.hasPreview());
        if (data.getBytes() == null) {
            setBytes(null);
        } else {
            setBytes(new byte[data.getBytes().length]);
            System.arraycopy(data.getBytes(), 0, getBytes(), 0, data.getBytes().length);
        }
        if (data.getPreviewBytes() == null) {
            setPreviewBytes(null);
        } else {
            setPreviewBytes(new byte[data.getPreviewBytes().length]);
            System.arraycopy(data.getPreviewBytes(), 0, getPreviewBytes(), 0, data.getPreviewBytes().length);
        }
        getPageIds().clear();
        getPageIds().addAll(data.getPageIds());
    }

    public void cloneData(FileData data) {
        super.cloneData(data);
        setMediaType(data.getMediaType());
        setContentType(data.getContentType());
        setFileSize(data.getFileSize());
        setWidth(data.getWidth());
        setHeight(data.getHeight());
        setPreviewContentType(data.getPreviewContentType());
        setHasPreview(data.hasPreview());
        if (data.getBytes() == null) {
            setBytes(null);
        } else {
            setBytes(new byte[data.getBytes().length]);
            System.arraycopy(data.getBytes(), 0, getBytes(), 0, data.getBytes().length);
        }
        if (data.getPreviewBytes() == null) {
            setPreviewBytes(null);
        } else {
            setPreviewBytes(new byte[data.getPreviewBytes().length]);
            System.arraycopy(data.getPreviewBytes(), 0, getPreviewBytes(), 0, data.getPreviewBytes().length);
        }
    }

    @Override
    public void setName(String name) {
        if (!name.contains(".") && this.name.contains("."))
            name = name + FileUtil.getExtension(this.name);
        this.name = StringUtil.toSafeWebName(name);
    }

    @Override
    public String getUrl() {
        return path;
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
            if (pos != -1) {
                setMediaType(contentType.substring(0, pos));
            }
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
            err.addErrorString(StringUtil.getHtml("_notComplete", SessionReader.getSessionLocale(request)));
            RequestError.setError(request, err);
        }
        return err == null;
    }

    public void readFileCreateRequestData(HttpServletRequest request) {
        BinaryFileData file = RequestReader.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setName(file.getFileName());
            setContentType(file.getContentType());
            setContentChanged(true);
            String name = RequestReader.getString(request, "displayName").trim();
            setDisplayName(name.isEmpty() ? getName() : name);
            name = RequestReader.getString(request, "name").trim();
            setName(name.isEmpty() ? getDisplayName() : name);
        }
    }

    protected void readFileEditRequestData(HttpServletRequest request) {
        BinaryFileData file = RequestReader.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setName(file.getFileName());
            setMediaType("");
            setContentType(file.getContentType());
            setContentChanged(true);
        }
    }

    protected void readFileRequestData(HttpServletRequest request) throws Exception {
        String oldName = getName();
        readResourceNodeRequestData(request);
        if (getName().indexOf('.') == -1)
            setName(getName() + FileUtil.getExtension(oldName));
        int width = RequestReader.getInt(request, "width");
        int height = RequestReader.getInt(request, "height");
        if (width == getWidth()) {
            width = 0;
        }
        if (height == getHeight()) {
            height = 0;
        }
        if (width != 0 || height != 0) {
            createResizedImage(width, height);
        }
    }

    public void prepareSave() throws Exception {
        super.prepareSave();
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
            writers = ImageIO.getImageWritersBySuffix(FileUtil.getExtension(name));
            if (writers.hasNext()) {
                setContentType("");
            } else {
                setName(FileUtil.getFileNameWithoutExtension(name) + ".jpg");
                writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
                setContentType("image/jpeg");
            }
        }
        ImageWriter writer = writers.next();
        ByteArrayOutputStream bout = new ByteArrayOutputStream();
        ImageOutputStream ios = ImageIO.createImageOutputStream(bout);
        writer.setOutput(ios);
        assert bi != null;
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

    /******************* XML part *********************************/

    @Override
    protected Element getNewNode(Document xmlDoc) {
        return xmlDoc.createElement("file");
    }

    @Override
    public void addXmlAttributes(Document xmlDoc, Element node) {
        super.addXmlAttributes(xmlDoc, node);
        XmlUtil.addAttribute(xmlDoc, node, "mediaType", StringUtil.toXml(getMediaType()));
        XmlUtil.addAttribute(xmlDoc, node, "contentType", StringUtil.toXml(getContentType()));
        XmlUtil.addIntAttribute(xmlDoc, node, "fileSize", getFileSize());
        XmlUtil.addIntAttribute(xmlDoc, node, "width", getWidth());
        XmlUtil.addIntAttribute(xmlDoc, node, "height", getHeight());
        XmlUtil.addAttribute(xmlDoc, node, "previewContentType", StringUtil.toXml(getPreviewContentType()));
        XmlUtil.addBooleanAttribute(xmlDoc, node, "hasPreview", hasPreview());
    }

    public Element toXml(Document xmlDoc, Element parentNode) {
        if (!isLoaded())
            FileBean.getInstance().loadFileContent(this, getMaxVersion());
        String encodedBytes = "";
        try {
            BinaryFileData file = FileBean.getInstance().getBinaryFileData(getId(), getMaxVersion());
            encodedBytes = Base64.getEncoder().encodeToString(file.getBytes());
        } catch (Exception e) {
            Log.error("could not encode file", e);
        }
        Element node = super.toXml(xmlDoc, parentNode);
        XmlUtil.addCDATA(xmlDoc, node, encodedBytes);
        return node;
    }

    public void fromXml(Element node) throws ParseException {
        super.fromXml(node);
        String cdata = XmlUtil.getCData(node);
        bytes = Base64.getDecoder().decode(cdata);
        fileSize = bytes.length;
    }

    public void getXmlAttributes(Element node) {
        super.getXmlAttributes(node);
        setMediaType(XmlUtil.getStringAttribute(node, "mediaType"));
        setContentType(XmlUtil.getStringAttribute(node, "contentType"));
        setFileSize(XmlUtil.getIntAttribute(node, "fileSize"));
        setWidth(XmlUtil.getIntAttribute(node, "width"));
        setHeight(XmlUtil.getIntAttribute(node, "height"));
        setPreviewContentType(XmlUtil.getStringAttribute(node, "previewContentType"));
        setHasPreview(XmlUtil.getBooleanAttribute(node, "hasPreview"));
    }

    /******************* search part *********************************/

    //todo
    @Override
    public String getSearchText() {
        StringBuilder sb;
        sb = new StringBuilder();
        sb.append(name).append(" ").append(getDisplayName()).append(" ").append(getDescription()).append(" ").append(getMediaType()).append(" ").append(SearchHelper.getSearchContent(bytes, name, contentType));
        return sb.toString();
    }

}
