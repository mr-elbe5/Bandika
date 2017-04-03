package de.elbe5.cms.field;

public class BrowseData {
    protected int pageId = 0;
    protected int browsedSiteId = 0;
    protected int ckCallbackNum = -1;
    protected String callbackId = "";

    public int getPageId() {
        return pageId;
    }

    public void setPageId(int pageId) {
        this.pageId = pageId;
    }

    public int getBrowsedSiteId() {
        return browsedSiteId;
    }

    public void setBrowsedSiteId(int browsedSiteId) {
        this.browsedSiteId = browsedSiteId;
    }

    public int getCkCallbackNum() {
        return ckCallbackNum;
    }

    public boolean isCkBrowser() {
        return ckCallbackNum != -1;
    }

    public void setCkCallbackNum(int ckCallbackNum) {
        this.ckCallbackNum = ckCallbackNum;
    }

    public String getCallbackId() {
        return callbackId;
    }

    public void setCallbackId(String callbackId) {
        this.callbackId = callbackId;
    }

    public String getCallbackLink(String url) {
        if (isCkBrowser()) return String.format("return callCkBrowserCallback(%s,'%s');", getCkCallbackNum(), url);
        else return String.format("return callBrowserCallback('%s','%s');", getCallbackId(), url);
    }

    public String getImageCallbackLink(int id) {
        if (isCkBrowser()) return String.format("return callCkBrowserCallback(%s,'/file.srv?act=show&fileId=%s');", getCkCallbackNum(), id);
        return String.format("return callBrowserImageCallback('%s','%s');", getCallbackId(), id);
    }
}