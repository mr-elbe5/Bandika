package de.elbe5.cms.field;

import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.application.Controller;
import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.event.Event;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.webserver.user.LoginController;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.file.PreviewCache;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.tree.TreeNodeRightsData;
import de.elbe5.webserver.tree.TreeRightsProvider;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FieldController extends Controller implements IActionController {
    public static String MASTER_POPUP = "popupMaster.jsp";
    private static FieldController instance = null;

    public static FieldController getInstance() {
        return instance;
    }

    public static void setInstance(FieldController instance) {
        FieldController.instance = instance;
    }

    public String getKey() {
        return "field";
    }

    public FieldController() {
        addListener(CmsTreeCache.getInstance());
        addListener(PreviewCache.getInstance());
    }

    @Override
    public boolean doAction(String method, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (!SessionHelper.isLoggedIn(request)){
            if (!isAjaxRequest(request))
                return LoginController.getInstance().openLogin(request, response);
            return forbidden();
        }
        if (SessionHelper.hasRight(request, TreeRightsProvider.RIGHTS_TYPE_TREENODE, TreeNodeRightsData.RIGHTS_EDITOR)) {
            if (method.equals("openLinkBrowser")) return openLinkBrowser(request, response);
            if (method.equals("openImageBrowser")) return openImageBrowser(request, response);
            if (method.equals("reopenImageBrowser")) return reopenImageBrowser(request, response);
            if (method.equals("showBrowsedLinks")) return showBrowsedLinks(request, response);
            if (method.equals("showBrowsedImages")) return showBrowsedImages(request, response);
            if (method.equals("openCreateBrowsedImage")) return openCreateBrowsedImage(request, response);
            if (method.equals("saveBrowsedImage")) return saveBrowsedImage(request, response);
            if (method.equals("openSelectImage")) return openSelectImage(request, response);
            if (method.equals("openSelectImageLink")) return openSelectImageLink(request, response);
            if (method.equals("openSelectTextLink")) return openSelectTextLink(request, response);
            if (method.equals("openSelectLink")) return openSelectLink(request, response);
        }
        return badRequest();
    }

    public boolean showImageBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/field/browseImages.jsp", MASTER_POPUP);
    }

    public boolean showLinkBrowserJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendJspResponse(request, response, "/WEB-INF/_jsp/field/browseLinks.jsp", MASTER_POPUP);
    }

    public boolean showBrowsedLinksJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browsedLinks.inc.jsp");
    }

    public boolean showBrowsedImagesJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/browsedImages.inc.jsp");
    }

    public boolean showCreateImageJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/createImage.ajax.jsp");
    }

    public boolean showSelectImageJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectImage.ajax.jsp");
    }

    public boolean showSelectImageLinkJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectImageLink.ajax.jsp");
    }

    public boolean showSelectTextLinkJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectTextLink.ajax.jsp");
    }

    public boolean showSelectLinkJsp(HttpServletRequest request, HttpServletResponse response) {
        return ResponseHelper.sendForwardResponse(request, response, "/WEB-INF/_jsp/field/selectLink.ajax.jsp");
    }

    public boolean openLinkBrowser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        int pageId = RequestHelper.getInt(request, "pageId");
        BrowseData browseData = new BrowseData();
        browseData.setPageId(pageId);
        browseData.setCkCallbackNum(RequestHelper.getInt(request, "CKEditorFuncNum", -1));
        browseData.setCallbackId(RequestHelper.getString(request, "CallbackId"));
        browseData.setBrowsedSiteId(siteId);
        SessionHelper.setSessionObject(request, "browseData", browseData);
        return showLinkBrowserJsp(request, response);
    }

    public boolean openImageBrowser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int siteId = RequestHelper.getInt(request, "siteId");
        int pageId = RequestHelper.getInt(request, "pageId");
        BrowseData browseData = new BrowseData();
        browseData.setPageId(pageId);
        browseData.setCkCallbackNum(RequestHelper.getInt(request, "CKEditorFuncNum", -1));
        browseData.setCallbackId(RequestHelper.getString(request, "CallbackId"));
        browseData.setBrowsedSiteId(siteId);
        SessionHelper.setSessionObject(request, "browseData", browseData);
        return showImageBrowserJsp(request, response);
    }

    public boolean reopenImageBrowser(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showImageBrowserJsp(request, response);
    }

    public boolean showBrowsedLinks(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BrowseData browseData = (BrowseData) SessionHelper.getSessionObject(request, "browseData");
        browseData.setBrowsedSiteId(RequestHelper.getInt(request, "siteId"));
        return showBrowsedLinksJsp(request, response);
    }

    public boolean showBrowsedImages(HttpServletRequest request, HttpServletResponse response) throws Exception {
        BrowseData browseData = (BrowseData) SessionHelper.getSessionObject(request, "browseData");
        browseData.setBrowsedSiteId(RequestHelper.getInt(request, "siteId"));
        return showBrowsedImagesJsp(request, response);
    }

    public boolean openCreateBrowsedImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showCreateImageJsp(request, response);
    }

    public boolean saveBrowsedImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        int parentId = RequestHelper.getInt(request, "siteId");
        FileBean ts = FileBean.getInstance();
        CmsTreeCache tc = CmsTreeCache.getInstance();
        SiteData parentNode = tc.getSite(parentId);
        FileData data = new FileData();
        readFileCreateRequestData(request, data);
        if (!data.isComplete()) return showCreateImageJsp(request, response);
        data.setNew(true);
        data.setId(FileBean.getInstance().getNextId());
        data.setParentId(parentNode.getId());
        data.setParent(parentNode);
        data.setAnonymous(parentNode.isAnonymous());
        data.setInheritsRights(true);
        data.inheritPathFromParent();
        data.inheritRightsFromParent();
        data.inheritParentIdsFromParent();
        data.setVisible(parentNode.isVisible());
        data.setRanking(parentNode.getSites().size());
        data.setAuthorName(SessionHelper.getUserName(request));
        data.prepareSave(request);
        data.setPublished(true);
        ts.createFile(data);
        sendEvent(new Event(BaseCache.EVENT_DIRTY));
        return ResponseHelper.closeLayerToUrl(request, response, "/field.srv?act=reopenImageBrowser&siteId=" + parentId, "_fileCreated");
    }

    public void readFileCreateRequestData(HttpServletRequest request, FileData data) {
        BinaryFileData file = RequestHelper.getFile(request, "file");
        if (file != null && file.getBytes() != null && file.getFileName().length() > 0 && !StringUtil.isNullOrEmtpy(file.getContentType())) {
            data.setBytes(file.getBytes());
            data.setFileSize(file.getBytes().length);
            data.setName(file.getFileName());
            data.setContentType(file.getContentType());
            data.setContentChanged(true);
            String s=RequestHelper.getString(request,"name");
            if (!s.isEmpty())
                data.setName(s);
            s=RequestHelper.getString(request,"displayName");
            if (!s.isEmpty())
                data.setDisplayName(s);
            else
                data.setDisplayName(data.getName());
        }
    }

    public boolean openSelectImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showSelectImageJsp(request, response);
    }

    public boolean openSelectImageLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showSelectImageLinkJsp(request, response);
    }

    public boolean openSelectTextLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showSelectTextLinkJsp(request, response);
    }

    public boolean openSelectLink(HttpServletRequest request, HttpServletResponse response) throws Exception {
        return showSelectLinkJsp(request, response);
    }
}
