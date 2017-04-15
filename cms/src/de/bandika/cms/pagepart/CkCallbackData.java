package de.bandika.cms.pagepart;

public class CkCallbackData {

    protected int pageId = 0;
    protected int siteId = 0;
    protected int ckCallbackNum = -1;

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getSiteId() {
        return siteId;
    }

    public void setSiteId(int siteId) {
        this.siteId = siteId;
    }

    public int getCkCallbackNum() {
        return ckCallbackNum;
    }

    public void setCkCallbackNum(int ckCallbackNum) {
        this.ckCallbackNum = ckCallbackNum;
    }

}
