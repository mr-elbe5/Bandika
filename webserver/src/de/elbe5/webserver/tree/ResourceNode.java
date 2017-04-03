package de.elbe5.webserver.tree;

import de.elbe5.webserver.servlet.SessionHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

public abstract class ResourceNode extends TreeNode {
    protected String keywords = "";
    protected int publishedVersion = 0;
    protected int draftVersion = 0;
    protected int loadedVersion = 0;
    protected boolean contentChanged = false;
    protected Date contentChangeDate = new Date();
    protected boolean published = false;

    public ResourceNode() {
    }

    protected void copyFromTree(ResourceNode data) {
        super.copyFromTree(data);
        setKeywords(data.getKeywords());
        setPublishedVersion(data.getPublishedVersion());
        setDraftVersion(data.getDraftVersion());
        setLoadedVersion(0);
        setPublished(data.isPublished());
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    public int getPublishedVersion() {
        return publishedVersion;
    }

    public void setPublishedVersion(int publishedVersion) {
        this.publishedVersion = publishedVersion;
    }

    public int getDraftVersion() {
        return draftVersion;
    }

    public void setDraftVersion(int draftVersion) {
        this.draftVersion = draftVersion;
    }

    public int getLoadedVersion() {
        return loadedVersion;
    }

    public void setLoadedVersion(int version) {
        this.loadedVersion = version;
    }

    public boolean isLoaded() {
        return loadedVersion != 0;
    }

    public void setContentChanged(boolean contentChanged) {
        this.contentChanged = contentChanged;
    }

    public boolean isContentChanged() {
        return contentChanged;
    }

    public Date getContentChangeDate() {
        return contentChangeDate;
    }

    public java.sql.Timestamp getSqlContentChangeDate() {
        return new java.sql.Timestamp(contentChangeDate.getTime());
    }

    public void setContentChangeDate(Date contentChangeDate) {
        this.contentChangeDate = contentChangeDate;
    }

    public void setContentChangeDate() {
        this.contentChangeDate = getChangeDate();
    }

    public void clearContent() {
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public int getVersionForUser(HttpServletRequest request) {
        if (SessionHelper.hasRightForId(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, getId(), TreeNodeRightsData.RIGHT_EDIT))
            return Math.max(draftVersion, publishedVersion);
        return publishedVersion;
    }

    public int getMaxVersion() {
        return Math.max(draftVersion, publishedVersion);
    }

}
