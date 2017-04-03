/*
  Bandika! - A Java based modular Framework
  Copyright (C) 2009-2014 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.bandika.file;

import de.bandika.data.FileData;
import de.bandika.data.StringCache;
import de.bandika.data.StringFormat;
import de.bandika.servlet.RequestData;
import de.bandika.servlet.RequestError;
import de.bandika.servlet.SessionData;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;

public class ImageData extends FileData {

    public static String FILETYPE = "image";
    public static String THUMBNAIL_CONTENT_TYPE = "image/jpeg";
    public static int MAX_THUMBNAIL_WIDTH = 60;
    public static int MAX_THUMBNAIL_HEIGHT = 50;

    public static String getThumbnailFileName(String fileName){
        int pos=fileName.lastIndexOf('.');
        String name= (pos== -1) ? fileName : fileName.substring(0,pos);
        return name+"_thumb.jpg";
    }

    protected int width = 0;
    protected int height = 0;
    protected String authorName = "";
    protected int pageId = 0;
    protected boolean hasThumbnail=false;
    protected FileData thumbnail = null;

    protected List<Integer> pageIds = null;

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

    public String getAuthorName() {
        return authorName;
    }

    public void setAuthorName(String authorName) {
        this.authorName = authorName;
    }

    public int getPageId() {
        return pageId;
    }

    public boolean isExclusive() {
        return pageId != 0;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public FileData getThumbnail() {
        return thumbnail;
    }

    public boolean hasThumbnail() {
        return hasThumbnail;
    }

    public void setHasThumbnail(boolean hasThumbnail) {
        this.hasThumbnail = hasThumbnail;
    }

    public void setThumbnail(FileData thumbnail) {
        this.thumbnail = thumbnail;
        hasThumbnail=thumbnail!=null;
    }

    public List<Integer> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<Integer> pageIds) {
        this.pageIds = pageIds;
    }

    public boolean isComplete(RequestData rdata, SessionData sdata) {
        RequestError err = null;
        boolean valid = isComplete(fileName);
        valid &= !isNew() || isComplete(bytes);
        if (!valid) {
            err = new RequestError();
            err.addErrorString(StringCache.getHtml("webapp_notComplete",sdata.getLocale()));
            rdata.setError(err);
        }
        return err == null;
    }

    public boolean readRequestData(RequestData rdata, SessionData sdata) {
        FileData file = rdata.getFile("file");
        if (file != null && file.getBytes() != null
                && file.getFileName().length() > 0
                && !StringFormat.isNullOrEmtpy(file.getContentType())) {
            setBytes(file.getBytes());
            setSize(getBytes().length);
            setFileName(file.getFileName());
            setContentType(file.getContentType());
            int imgWidth = rdata.getInt("imgWidth");
            if (imgWidth != 0) {
                try {
                    file = ImageTool.createResizedImage(file, imgWidth);
                    setBytes(file.getBytes());
                    setSize(getBytes().length);
                    setContentType(file.getContentType());
                } catch (IOException ignore) {
                }
            }
            try{
                BufferedImage image = ImageTool.createImage(this);
                setWidth(image.getWidth());
                setHeight(image.getHeight());
            } catch (IOException ignore) {
            }
        }
        String name=rdata.getString("name");
        if (!name.isEmpty())
            setFileName(name + "." + getExtension());
        boolean exclusive = rdata.getBoolean("exclusive");
        setPageId(exclusive ? rdata.getInt("id") : 0);
        return isComplete(rdata, sdata);
    }

    public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
        super.prepareSave();
        setAuthorName(sdata.getUserName());
    }

}