/*
  Elbe 5 CMS  - A Java based modular Content Management System
  Copyright (C) 2009-2015 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either pageVersion 3 of the License, or (at your option) any later pageVersion.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.
 */
package de.elbe5.cms.application;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.controller.IActionController;
import de.elbe5.base.log.Log;
import de.elbe5.base.user.UserData;
import de.elbe5.base.util.XmlUtil;
import de.elbe5.base.util.ZipUtil;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileController;
import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.site.SiteBean;
import de.elbe5.cms.tree.CmsTreeCache;
import de.elbe5.webserver.application.Controller;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.page.PageData;
import de.elbe5.webserver.servlet.RequestHelper;
import de.elbe5.webserver.servlet.ResponseHelper;
import de.elbe5.cms.site.SiteData;
import de.elbe5.cms.template.TemplateCache;
import de.elbe5.cms.template.TemplateData;
import de.elbe5.webserver.servlet.SessionHelper;
import de.elbe5.webserver.tree.TreeBean;
import de.elbe5.webserver.user.UserBean;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.zip.ZipOutputStream;

public class CmsClientController extends Controller implements IActionController {

    private static CmsClientController instance = null;

    public static void setInstance(CmsClientController controller) {
        instance = controller;
    }

    public static CmsClientController getInstance() {
        return instance;
    }

    @Override
    public String getKey() {
        return "client";
    }

    @Override
    public boolean doAction(String action, HttpServletRequest request, HttpServletResponse response) throws Exception {
        if ("downloadFile".equals(action)){
            return SessionHelper.isLoggedIn(request) && FileController.getInstance().download(request, response);
        }
        Document xmlDoc = XmlUtil.createXmlDocument();
        Element root = XmlUtil.createRootNode(xmlDoc, "result");
        XmlUtil.addAttribute(xmlDoc, root, "action", action);
        if ("login".equals(action)) return login(request, response, xmlDoc, root);
        if (!SessionHelper.isLoggedIn(request)) {
            return error(request, response,xmlDoc,root);
        }
        if ("getTree".equals(action)) return getTree(request, response, xmlDoc, root);
        if ("getLayouts".equals(action)) return getLayouts(request, response, xmlDoc, root);
        if ("synchronizeDeleted".equals(action)) return synchronizeDeleted(request, response, xmlDoc, root);
        if ("synchronizeSite".equals(action)) return synchronizeSite(request, response, xmlDoc, root);
        if ("synchronizePage".equals(action)) return synchronizePage(request, response, xmlDoc, root);
        if ("synchronizeFile".equals(action)) return synchronizeFile(request, response, xmlDoc, root);
        if ("getExport".equals(action)) return getExport(request, response, xmlDoc, root);
        return badRequest();
    }

    private boolean success(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception{
        XmlUtil.addAttribute(xmlDoc, root, "status", "ok");
        sendXml(request, response, xmlDoc);
        return true;
    }

    private boolean error(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception{
        XmlUtil.addAttribute(xmlDoc, root, "status", "error");
        sendXml(request, response, xmlDoc);
        return true;
    }

    protected boolean sendXml(HttpServletRequest request, HttpServletResponse response, Document xmlDoc) throws Exception{
        String xml = XmlUtil.xmlToString(xmlDoc);
        return ResponseHelper.sendXmlResponse(request, response, xml);
    }

    protected boolean login(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception {
        if (!RequestHelper.isPostback(request))
            return badRequest();
        String login=RequestHelper.getString(request,"login");
        String pwd=RequestHelper.getString(request,"pwd");
        if (login.length() == 0 || pwd.length() == 0) {
            return error(request, response, xmlDoc, root);
        }
        UserBean ts = UserBean.getInstance();
        UserData data = ts.loginUser(login, pwd);
        if (data == null) {
            return error(request, response, xmlDoc, root);
        }
        data.checkRights();
        SessionHelper.setSessionUserData(request, data);
        SessionHelper.setSessionLocale(request);
        return success(request, response, xmlDoc, root);
    }

    protected boolean getTree(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception{
        Element structElement = XmlUtil.addNode(xmlDoc, root, "tree");
        CmsTreeCache.getInstance().setDirty();
        SiteData site = CmsTreeCache.getInstance().getRootSite();
        site.fillTreeXml(xmlDoc, structElement, false);
        return success(request, response,xmlDoc,root);
    }

    protected boolean getLayouts(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception{
        Element layoutElement = XmlUtil.addNode(xmlDoc, root, "layouts");
        List<TemplateData> layouts = TemplateCache.getInstance().getPageTemplates();
        for (TemplateData layout : layouts) {
            Element node = XmlUtil.addNode(xmlDoc, layoutElement, "layout");
            XmlUtil.addAttribute(xmlDoc, node, "name", layout.getFileName());
        }
        return success(request, response,xmlDoc,root);
    }

    protected boolean synchronizeDeleted(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception {
        String ids=RequestHelper.getString(request,"ids");
        if (ids.length()==0 || !TreeBean.getInstance().deleteTreeNodes(ids))
            return error(request, response,xmlDoc,root);
        CmsTreeCache.getInstance().setDirty();
        return success(request, response,xmlDoc,root);
    }

    protected boolean synchronizeSite(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception {
        int parentId = RequestHelper.getInt(request,"parentId");
        if (parentId==0)
            return error(request, response,xmlDoc,root);
        int originalParentId = RequestHelper.getInt(request,"originalParentId");
        int siteId= RequestHelper.getInt(request,"siteId");
        String name= RequestHelper.getString(request, "name");
        String displayName= RequestHelper.getString(request,"displayName");
        SiteData data;
        if (siteId==0){
            data=new SiteData();
            data.setNew(true);
            data.setId(SiteBean.getInstance().getNextId());
            data.setParentId(parentId);
        }
        else{
            data=CmsTreeCache.getInstance().getSite(siteId);
            if (data==null)
                return error(request, response,xmlDoc,root);
            if (originalParentId!=0 && originalParentId!=data.getParentId()){
                Log.error("wrong parent: "+originalParentId+" vs "+parentId);
                return error(request, response,xmlDoc,root);
            }
            if (originalParentId!=0 && parentId!=originalParentId){
                Log.info("moving site with id "+siteId);
            }
            data.setParentId(parentId);
        }
        data.setName(name);
        data.setDisplayName(displayName);
        data.prepareSave();
        SiteBean.getInstance().saveSiteSettings(data);
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        XmlUtil.addIntAttribute(xmlDoc, root, "id", data.getId());
        return success(request, response,xmlDoc,root);
    }

    protected boolean synchronizePage(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception {
        int parentId = RequestHelper.getInt(request,"parentId");
        int originalParentId = RequestHelper.getInt(request,"originalParentId");
        int pageId= RequestHelper.getInt(request, "pageId");
        String name= RequestHelper.getString(request,"name");
        String displayName= RequestHelper.getString(request,"displayName");
        String templateName= RequestHelper.getString(request, "templateName");
        PageData data;
        if (pageId==0){
            data=new PageData();
            data.setNew(true);
            data.setId(SiteBean.getInstance().getNextId());
            data.setParentId(parentId);
        }
        else{
            data=CmsTreeCache.getInstance().getPage(pageId);
            if (data==null)
                return error(request, response,xmlDoc,root);
            if (originalParentId!=0 && originalParentId!=data.getParentId()){
                Log.error("wrong parent: "+originalParentId+"-"+parentId);
                return error(request, response,xmlDoc,root);
            }
            if (originalParentId!=0 && parentId!=originalParentId){
                Log.info("moving page with id "+pageId);
            }
            data.setParentId(parentId);
        }
        data.setName(name);
        data.setDisplayName(displayName);
        data.setTemplateName(templateName);
        data.prepareSave(request);
        data.setPublished(false);
        if (data.isNew())
            PageBean.getInstance().createPage(data);
        else
            PageBean.getInstance().savePageSettings(data);
        data.stopEditing();
        CmsTreeCache.getInstance().setDirty();
        XmlUtil.addIntAttribute(xmlDoc, root, "id", data.getId());
        return success(request, response,xmlDoc,root);
    }

    protected boolean synchronizeFile(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception {
        int parentId = RequestHelper.getInt(request,"parentId");
        int originalParentId = RequestHelper.getInt(request,"originalParentId");
        int fileId= RequestHelper.getInt(request, "fileId");
        String name= RequestHelper.getString(request,"name");
        String displayName= RequestHelper.getString(request,"displayName");
        String contentType= RequestHelper.getString(request, "contentType");
        String mediaType= RequestHelper.getString(request, "mediaType");
        BinaryFileData file = RequestHelper.getFile(request, "file");
        FileData data;
        if (fileId==0){
            if (file==null)
                return error(request, response,xmlDoc,root);
            data=new FileData();
            data.setNew(true);
            data.setId(SiteBean.getInstance().getNextId());
            data.setParentId(parentId);
        }
        else{
            data=CmsTreeCache.getInstance().getFile(fileId);
            if (data==null)
                return error(request, response,xmlDoc,root);
            if (originalParentId!=0 && originalParentId!=data.getParentId()){
                Log.error("wrong parent: "+originalParentId+"-"+parentId);
                return error(request, response,xmlDoc,root);
            }
            if (originalParentId!=0 && parentId!=originalParentId){
                Log.info("moving file with id "+fileId);
            }
            data.setParentId(parentId);
        }
        data.setName(name);
        data.setDisplayName(displayName);
        if (file!=null){
            data.setBytes(file.getBytes());
            data.setFileSize(file.getBytes().length);
            data.setMediaType(mediaType);
            data.setContentType(contentType);
            data.setContentChanged(true);
        }
        data.prepareSave(request);
        if (data.isNew())
            FileBean.getInstance().createFile(data);
        else{
            FileBean.getInstance().saveFileSettings(data);
            if (data.isContentChanged())
                FileBean.getInstance().saveFileContent(data);
        }
        data.stopEditing();
        XmlUtil.addIntAttribute(xmlDoc, root, "id", data.getId());
        return success(request, response,xmlDoc,root);
    }

    protected boolean getExport(HttpServletRequest request, HttpServletResponse response, Document xmlDoc, Element root) throws Exception{
        SiteData rootSite=CmsTreeCache.getInstance().getRootSite();
        rootSite.fillTreeXml(xmlDoc,root,true);
        String xml=XmlUtil.xmlToString(xmlDoc);
        List<FileData> files=CmsTreeCache.getInstance().getAllFiles();
        ResponseHelper.setResponseType(request, ResponseHelper.RESPONSE_TYPE_STREAM);
        response.setContentType("application/zip");
        response.addHeader("content-disposition", "attachment; filename=export.zip");
        ZipOutputStream zout= new ZipOutputStream(response.getOutputStream());
        ZipUtil.addEntry(zout, "export.xml", xml.getBytes("UTF-8"));
        for (FileData file : files){
            BinaryFileData binData=FileBean.getInstance().getBinaryFileData(file.getId(),file.getMaxVersion());
            if (binData==null)
                continue;
            ZipUtil.addEntry(zout, String.format("%s.%s",file.getId(),file.getExtension()), binData.getBytes());
        }
        zout.flush();
        zout.close();
        return true;
    }

}
