/*
 Bandika CMS - A Java based modular Content Management System
 Copyright (C) 2009-2021 Michael Roennau

 This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
 This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.content;

import de.elbe5.data.IJsonDataPackage;
import de.elbe5.log.Log;
import de.elbe5.file.FileBean;
import de.elbe5.file.FileData;
import org.json.JSONObject;

import java.util.*;

public class ContentCache implements IJsonDataPackage {

    private static final ContentCache instance = new ContentCache();

    public static ContentCache getInstance() {
        return instance;
    }

    private ContentData contentRoot = null;
    private int version = 1;
    private volatile boolean dirty = true;
    private static final Integer lockObj = 1;

    private Map<Integer, ContentData> contentMap = new HashMap<>();
    private Map<String, ContentData> pathMap = new HashMap<>();
    private Map<Integer, FileData> fileMap = new HashMap<>();
    private List<ContentData> footerList = new ArrayList<>();

    public synchronized void load() {
        List<ContentData> contentList = ContentBean.getInstance().getAllContents();
        List<FileData> fileList = FileBean.getInstance().getAllFiles();
        Map<Integer, ContentData> contents = new HashMap<>();
        Map<String, ContentData> paths = new HashMap<>();
        List<ContentData> footer = new ArrayList<>();
        for (ContentData contentData : contentList) {
            contents.put(contentData.getId(), contentData);
        }
        Map<Integer, FileData> files = new HashMap<>();
        for (FileData fileData : fileList) {
            files.put(fileData.getId(), fileData);
            ContentData contentData=contents.get(fileData.getParentId());
            if (contentData!=null) {
                contentData.addFile(fileData);
                fileData.setParent(contentData);
            }
        }
        contentRoot = contents.get(ContentData.ID_ROOT);
        if (contentRoot == null)
            return;
        for (ContentData content : contentList) {
            ContentData parent = contents.get(content.getParentId());
            content.setParent(parent);
            if (parent != null) {
                parent.addChild(content);
            }
        }
        contentRoot.initializeChildren();
        for (ContentData contentData : contentList) {
            paths.put(contentData.getUrl(), contentData);
            if (contentData.isActive() && contentData.isInFooterNav()){
                footer.add(contentData);
            }
        }
        Collections.sort(footer);
        fileMap = files;
        pathMap = paths;
        footerList=footer;
        contentMap = contents;
        Log.log("content cache reloaded");
    }

    public void checkDirty() {
        if (dirty) {
            synchronized (lockObj) {
                if (dirty) {
                    load();
                    dirty = false;
                }
            }
        }
    }

    public void setDirty() {
        increaseVersion();
        dirty=true;
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    public ContentData getContentRoot() {
        checkDirty();
        return contentRoot;
    }

    public ContentData getContent(int id) {
        checkDirty();
        return contentMap.get(id);
    }

    public <T extends ContentData> T getContent(int id,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(contentMap.get(id));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public <T extends ContentData> List<T> getContents(Class<T> cls) {
        checkDirty();
        List<T> list = new ArrayList<>();
        try {
            for (ContentData data : contentMap.values()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return list;
        }
        return list;
    }

    public ContentData getContent(String url) {
        checkDirty();
        return pathMap.get(url);
    }

    public <T extends ContentData> T getContent(String url,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(pathMap.get(url));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public int getParentContentId(int id) {
        checkDirty();
        ContentData contentData = getContent(id);
        if (contentData == null) {
            return 0;
        }
        return contentData.getParentId();
    }

    public List<Integer> getParentContentIds(ContentData data) {
        checkDirty();
        List<Integer> list = new ArrayList<>();
        while (data!=null) {
            list.add(data.getId());
            data = data.getParent();
        }
        return list;
    }

    public List<Integer> getParentContentIds(int contentId) {
        ContentData data=getContent(contentId);
        return getParentContentIds(data);
    }

    public List<ContentData> getFooterList() {
        return footerList;
    }

    public FileData getFile(int id) {
        checkDirty();
        return fileMap.get(id);
    }

    public <T extends FileData> T getFile(int id,Class<T> cls) {
        checkDirty();
        try {
            return cls.cast(fileMap.get(id));
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
    }

    public <T extends FileData> List<T> getFiles(Class<T> cls) {
        checkDirty();
        List<T> list = new ArrayList<>();
        try {
            for (FileData data : fileMap.values()){
                if (cls.isInstance(data))
                    list.add(cls.cast(data));
            }
        }
        catch(NullPointerException | ClassCastException e){
            return null;
        }
        return list;
    }

    public int getFileParentId(int id) {
        checkDirty();
        FileData fileData = getFile(id);
        if (fileData == null) {
            return 0;
        }
        return fileData.getParentId();
    }

    @Override
    public String getName() {
        return "contentData";
    }

    @Override
    public JSONObject saveAsJson() {
        return getContentRoot().toJSONObject();
    }

    @Override
    public void loadFromJson(JSONObject jsonObject) {
        //todo
    }
}
