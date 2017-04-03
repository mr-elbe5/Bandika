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

import java.util.List;

public class DocumentData extends FileData {

    public static String FILETYPE = "document";

    protected String authorName = "";
    protected int pageId = 0;
    protected String searchContent = "";

    protected List<Integer> pageIds = null;

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

    public String getSearchContent() {
        return searchContent;
    }

    public void setSearchContent(String searchContent) {
        this.searchContent = searchContent;
    }

    public List<Integer> getPageIds() {
        return pageIds;
    }

    public void setPageIds(List<Integer> pageIds) {
        this.pageIds = pageIds;
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
        }
        String name=rdata.getString("name");
        if (!name.isEmpty())
            setFileName(name + "." + getExtension());
        boolean exclusive = rdata.getBoolean("exclusive");
        setPageId(exclusive ? rdata.getInt("id") : 0);
        return isComplete(rdata, sdata);
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

    public void prepareSave(RequestData rdata, SessionData sdata) throws Exception {
        super.prepareSave();
        setAuthorName(sdata.getUserName());
    }

}