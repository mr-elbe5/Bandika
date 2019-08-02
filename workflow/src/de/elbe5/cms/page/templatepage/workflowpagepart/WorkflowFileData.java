package de.elbe5.cms.page.templatepage.workflowpagepart;

import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.ImageUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileBaseData;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.request.IRequestData;
import de.elbe5.cms.request.RequestData;

import java.awt.image.BufferedImage;
import java.io.IOException;

public class WorkflowFileData extends FileBaseData implements IRequestData {

    public static int MAX_THUMBNAIL_WIDTH = 200;
    public static int MAX_THUMBNAIL_HEIGHT = 200;

    protected int workflowId = 0;
    protected int worklowTaskId = 0;
    protected String notes = "";

    public int getWorkflowId() {
        return workflowId;
    }

    public void setWorkflowId(int workflowId) {
        this.workflowId = workflowId;
    }

    public int getWorkflowTaskId() {
        return worklowTaskId;
    }

    public void setWorkflowTaskId(int worklowTaskId) {
        this.worklowTaskId = worklowTaskId;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public WorkflowFileData() {
    }

    public void setCreateValues(WorkflowPagePartData workflow) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setWorkflowId(workflow.getId());
    }

    public void setCreateValues(WorkflowTaskData task) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setWorkflowTaskId(task.getId());
    }

    @Override
    public void readRequestData(RequestData rdata) {
        BinaryFile file = rdata.getFile("file");
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
                    } catch (IOException e) {
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
                } catch (IOException e) {
                    Log.error("could not create preview image", e);
                }
            }
        }
        String newName = rdata.getString("name").trim();
        if (!newName.isEmpty())
            setName(newName);
        setNotes(rdata.getString("notes").trim());
        if (name.isEmpty()) {
            rdata.addIncompleteField("name");
        }
    }

}
