package de.elbe5.cms.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestData;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Iterator;

public class FileData extends BaseIdData implements IRequestData {

    public static int MAX_THUMBNAIL_WIDTH = 200;
    public static int MAX_THUMBNAIL_HEIGHT = 200;

    protected LocalDateTime creationDate = null;
    protected int folderId = 0;
    protected FolderData folder = null;
    protected String name = "";
    protected String displayName = "";
    protected String description = "";
    protected String keywords = "";
    protected String authorName = "";

    protected String contentType = null;
    protected int fileSize = 0;
    protected int width = 0;
    protected int height = 0;
    protected byte[] bytes = null;
    protected byte[] previewBytes = null;
    protected boolean hasPreview=false;

    public FileData() {
    }

    public void setCreateValues(FolderData folder) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setFolderId(folder.getId());
        setFolder(folder);
    }

    public LocalDateTime getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(LocalDateTime d) {
        creationDate = d;
    }

    public int getFolderId() {
        return folderId;
    }

    public void setFolderId(int folderId) {
        this.folderId = folderId;
    }

    public FolderData getFolder() {
        return folder;
    }

    public void setFolder(FolderData folder) {
        this.folder = folder;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.toSafeWebName(name);
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public String getContentType() {
        return contentType;
    }

    public boolean isImage(){
        return contentType.startsWith("image/");
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public int getFileSize() {
        return fileSize;
    }

    public void setFileSize(int fileSize) {
        this.fileSize = fileSize;
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

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getPreviewBytes() {
        return previewBytes;
    }

    public void setPreviewBytes(byte[] previewBytes) {
        this.previewBytes = previewBytes;
    }

    public boolean hasPreview() {
        return hasPreview;
    }

    public void setHasPreview(boolean hasPreview) {
        this.hasPreview = hasPreview;
    }

    @Override
    public void readRequestData(RequestData rdata) {
        BinaryFileData file = rdata.getFile("file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setName(file.getFileName());
            setContentType(file.getContentType());
            if (isImage()) {
                int width = rdata.getInt("width");
                int height = rdata.getInt("height");
                if (width == getWidth()) {
                    width = 0;
                }
                if (height == getHeight()) {
                    height = 0;
                }
                if (width != 0 || height != 0) {
                    try {
                        createResizedImage(width, height);
                    }
                    catch (IOException e){
                        Log.error("could not create resized image", e);
                    }
                }
                try {
                    BufferedImage source = ImageUtil.createImage(bytes, contentType);
                    if (source != null) {
                        setWidth(source.getWidth());
                        setHeight(source.getHeight());
                        float factor = ImageUtil.getResizeFactor(source, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                        BufferedImage image = ImageUtil.copyImage(source, factor);
                        createJpegPreview(image);
                    }
                }
                catch (IOException e){
                    Log.error("could not create preview image", e);
                }
            }
        }
        String newName = rdata.getString("name").trim();
        if (!newName.isEmpty())
            setName(newName);
        setDisplayName(rdata.getString("displayName").trim());
        if (getDisplayName().isEmpty())
            setDisplayName(getName());
        setDescription(rdata.getString("description"));
        setKeywords(rdata.getString("keywords"));
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
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
        assert(bi!=null);
        setBytes(ImageUtil.writeImage(writer, bi));
        setFileSize(getBytes().length);
        setWidth(bi.getWidth());
        setHeight(bi.getHeight());
    }

    private void createJpegPreview(BufferedImage image) throws IOException {
        Iterator<ImageWriter> writers = ImageIO.getImageWritersByMIMEType("image/jpeg");
        ImageWriter writer = writers.next();
        assert(image!=null);
        setPreviewBytes(ImageUtil.writeImage(writer, image));
    }



}
