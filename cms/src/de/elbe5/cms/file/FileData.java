package de.elbe5.cms.file;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class FileData extends FileBaseData implements IRequestData {

    public static int MAX_THUMBNAIL_WIDTH = 200;
    public static int MAX_THUMBNAIL_HEIGHT = 200;

    protected int folderId = 0;
    protected FolderData folder = null;
    protected String displayName = "";
    protected String description = "";
    protected String keywords = "";

    public FileData() {
    }

    public void setCreateValues(FolderData folder) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setFolderId(folder.getId());
        setFolder(folder);
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

    @Override
    public void readRequestData(RequestData rdata) {
        BinaryFile file = rdata.getFile("file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setName(file.getFileName());
            setContentType(file.getContentType());
            createPreview();
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

    public void createPreview() {
        if (isImage()) {
            try {
                BufferedImage source = ImageUtil.createImage(bytes, contentType);
                if (source != null) {
                    setWidth(source.getWidth());
                    setHeight(source.getHeight());
                    float factor = ImageUtil.getResizeFactor(source, MAX_THUMBNAIL_WIDTH, MAX_THUMBNAIL_HEIGHT);
                    BufferedImage image = ImageUtil.copyImage(source, factor);
                    createJpegPreview(image);
                }
            } catch (IOException e) {
                Log.error("could not create preview image", e);
            }
        }
    }


}
