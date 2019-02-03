package de.elbe5.cms.file;

import de.elbe5.base.data.BaseIdData;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.servlet.IRequestData;
import de.elbe5.cms.servlet.RequestError;
import de.elbe5.cms.servlet.RequestReader;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import javax.servlet.http.HttpServletRequest;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FileData extends BaseIdData implements IRequestData, Comparable<FileData> {

    public static int MAX_THUMBNAIL_WIDTH = 200;
    public static int MAX_THUMBNAIL_HEIGHT = 200;

    protected LocalDateTime creationDate = null;
    protected int folderId = 0;
    protected FolderData folder = null;
    protected List<Integer> folderIds = new ArrayList<>();
    protected String name = "";
    protected String path = "";
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
        inheritFolderIdsFromFolder();
        inheritPathFromFolder();
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

    public List<Integer> getFolderIds() {
        return folderIds;
    }

    public void setFolderIds(List<Integer> folderIds) {
        this.folderIds = folderIds;
    }

    public void inheritFolderIdsFromFolder() {
        getFolderIds().clear();
        getFolderIds().addAll(folder.getParentIds());
        getFolderIds().add(folderId);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = StringUtil.toSafeWebName(name);
    }

    public String getPath() {
        return path;
    }

    public String getUrl() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public void setPathFromFolderPath(String folderPath) {
        path = folderPath;
        if (!path.endsWith("/") && name.length() > 0) {
            path += '/';
        }
        path += name;
    }

    public void inheritPathFromFolder() {
        setPathFromFolderPath(folder.getPath());
    }

    public String getDisplayName() {
        if (displayName == null || displayName.isEmpty()) {
            return getName();
        }
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
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
    public boolean readRequestData(HttpServletRequest request) {
        BinaryFileData file = RequestReader.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setName(file.getFileName());
            setContentType(file.getContentType());
            if (isImage()) {
                int width = RequestReader.getInt(request, "width");
                int height = RequestReader.getInt(request, "height");
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
        String newName = RequestReader.getString(request, "name").trim();
        if (!newName.isEmpty())
            setName(newName);
        setDisplayName(RequestReader.getString(request, "displayName").trim());
        if (getDisplayName().isEmpty())
            setDisplayName(getName());
        setDescription(RequestReader.getString(request, "description"));
        setKeywords(RequestReader.getString(request, "keywords"));
        RequestError error = new RequestError();
        if (name.isEmpty()) {
            error.addErrorField("name");
        }
        if (!error.isEmpty()){
            error.setError(request);
            return false;
        }
        return true;
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
    }

    @Override
    public int compareTo(FileData doc) {
        return getName().compareTo(doc.getName());
    }


}
