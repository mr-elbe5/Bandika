/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.file;

import de.elbe5.base.data.BaseData;
import de.elbe5.base.data.BinaryFile;
import de.elbe5.base.util.FileUtil;
import de.elbe5.base.util.StringUtil;
import de.elbe5.content.ContentData;
import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;

public abstract class FileData extends BaseData {

    private String fileName = "";
    private String extension = "";
    private String tempFileName = "";
    private String displayName = "";
    private String description = "";
    protected String contentType = null;
    protected int fileSize = 0;
    protected byte[] bytes = null;

    protected int parentId = 0;
    protected ContentData parent = null;

    protected String oldFileName = "";

    public FileData() {
    }

    public String getType() {
        return getClass().getSimpleName();
    }

    public void adjustFileNameToDisplayName(){
        if (getFileName().isEmpty() || getDisplayName().isEmpty())
            return;
        int pos= getFileName().lastIndexOf('.');
        if (pos==-1)
            return;
        setFileName(StringUtil.toSafeWebName(getDisplayName())+ getFileName().substring(pos));
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        if (!this.fileName.isEmpty() && !this.fileName.equals(fileName))
            oldFileName=this.fileName;
        this.fileName = fileName;
        extension = FileUtil.getExtension(fileName);
        tempFileName = getId() + extension;
    }

    public String getExtension() {
        return extension;
    }

    public String getTempFileName() {
        return tempFileName;
    }

    public String getOldFileName() {
        return oldFileName;
    }

    public boolean fileNameChanged(){
        return !oldFileName.isEmpty() && !oldFileName.equals(fileName);
    }

    public String getURL(){
        return "/files/"+getId() + extension;
    }

    public String getDisplayName() {
        if (displayName.isEmpty())
            return FileUtil.getFileNameWithoutExtension(getFileName());
        return displayName;
    }

    public String getDisplayFileName(){
        return getDisplayName() + extension;
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

    public String getContentType() {
        return contentType;
    }

    public boolean isImage() {
        return false;
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

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public int getParentId() {
        return parentId;
    }

    public void setParentId(int parentId) {
        this.parentId = parentId;
    }

    public ContentData getParent() {
        return parent;
    }

    public void setParent(ContentData parent) {
        this.parent = parent;
    }

    // multiple data

    public void setCreateValues(ContentData parent, RequestData rdata) {
        setNew(true);
        setId(FileBean.getInstance().getNextId());
        setCreatorId(rdata.getUserId());
        setChangerId(rdata.getUserId());
        setParentId(parent.getId());
        setParent(parent);
    }

    // helper

    public void createFromBinaryFile(BinaryFile file) {
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmpty(file.getContentType())) {
            setFileName(file.getFileName());
            setBytes(file.getBytes());
            setFileSize(file.getBytes().length);
            setContentType(file.getContentType());
        }
    }

    public void readSettingsRequestData(SessionRequestData rdata) {
        setDisplayName(rdata.getString("displayName").trim());
        setDescription(rdata.getString("description"));
    }

}
