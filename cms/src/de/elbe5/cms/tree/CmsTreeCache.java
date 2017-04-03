package de.elbe5.cms.tree;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.file.FileDataComparator;
import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.site.SiteBean;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webserver.configuration.Configuration;
import de.elbe5.webserver.tree.TreeBean;
import de.elbe5.webserver.tree.TreeNode;

import java.util.*;

public class CmsTreeCache extends BaseCache {

    private static CmsTreeCache instance = null;

    public static CmsTreeCache getInstance() {
        if (instance==null)
            instance=new CmsTreeCache();
        return instance;
    }

    protected SiteData rootSite = null;
    protected int version = 1;

    protected Map<Locale, Integer> languageRootIds = new HashMap<>();
    protected Map<Integer, SiteData> siteMap = null;
    protected Map<Integer, FileData> fileMap = null;
    protected Map<Integer, PageData> pageMap = null;
    protected Map<Integer, TreeNode> nodeMap = null;
    protected Map<String, SiteData> sitePathMap = null;
    protected Map<String, FileData> filePathMap = null;
    protected Map<String, PageData> pagePathMap = null;
    protected Map<String, TreeNode> nodePathMap = null;

    public synchronized void load() {
        TreeBean bean = TreeBean.getInstance();
        Map<Locale, Integer> rootMap = bean.readLanguageRootIds();
        Map<Integer, TreeNode> nodes = new HashMap<>();
        List<SiteData> siteList = SiteBean.getInstance().getAllSites();
        Map<Integer, SiteData> sites = new HashMap<>();
        for (SiteData node : siteList) {
            sites.put(node.getId(), node);
            nodes.put(node.getId(), node);
        }
        List<FileData> fileList = FileBean.getInstance().getAllFiles();
        Map<Integer, FileData> files = new HashMap<>();
        for (FileData node : fileList) {
            files.put(node.getId(), node);
            nodes.put(node.getId(), node);
        }
        List<PageData> pageList = PageBean.getInstance().getAllPages();
        Map<Integer, PageData> pages = new HashMap<>();
        for (PageData node : pageList) {
            pages.put(node.getId(), node);
            nodes.put(node.getId(), node);
        }
        rootSite = sites.get(TreeNode.ROOT_ID);
        rootSite.setPath("/");
        for (SiteData site : siteList) {
            SiteData parent = sites.get(site.getParentId());
            site.setParent(parent);
            if (parent != null) parent.addSite(site);
        }
        for (FileData file : fileList) {
            SiteData parent = sites.get(file.getParentId());
            file.setParent(parent);
            if (parent != null) parent.addFile(file);
        }
        for (PageData page : pageList) {
            SiteData parent = sites.get(page.getParentId());
            page.setParent(parent);
            if (parent != null) parent.addPage(page);
        }
        rootSite.inheritToChildren();
        Map<String, TreeNode> nodePaths = new HashMap<>();
        Map<String, SiteData> sitePaths = new HashMap<>();
        for (SiteData site : sites.values()) {
            sitePaths.put(site.getUrl(), site);
            nodePaths.put(site.getUrl(), site);
        }
        Map<String, FileData> filePaths = new HashMap<>();
        for (FileData file : files.values()) {
            filePaths.put(file.getUrl(), file);
            nodePaths.put(file.getUrl(), file);
        }
        Map<String, PageData> pagePaths = new HashMap<>();
        for (PageData page : pages.values()) {
            pagePaths.put(page.getUrl(), page);
            nodePaths.put(page.getUrl(), page);
        }
        siteMap = sites;
        fileMap = files;
        pageMap = pages;
        languageRootIds = rootMap;
        nodeMap = nodes;
        sitePathMap = sitePaths;
        filePathMap = filePaths;
        pagePathMap = pagePaths;
        nodePathMap = nodePaths;
    }

    public void setDirty() {
        increaseVersion();
        super.setDirty();
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    public SiteData getRootSite() {
        checkDirty();
        return rootSite;
    }

    public SiteData getSite(int id) {
        checkDirty();
        return siteMap.get(id);
    }

    public FileData getFile(int id) {
        checkDirty();
        FileData data=fileMap.get(id);
        if (!data.isLoaded() && data.getPublishedVersion()!=0)
            FileBean.getInstance().loadFileContent(data, data.getPublishedVersion());
        return data;
    }

    public List<FileData> getAllFiles(){
        List<FileData> files=new ArrayList<>();
        for (FileData file : fileMap.values()){
            files.add(file);
        }
        Collections.sort(files, new FileDataComparator());
        return files;
    }

    public PageData getPage(int id) {
        checkDirty();
        PageData data=pageMap.get(id);
        if (!data.isLoaded() && data.getPublishedVersion()!=0){
            PageBean.getInstance().loadPageContent(data, data.getPublishedVersion());
        }
        return data;
    }

    public TreeNode getNode(int id) {
        checkDirty();
        return nodeMap.get(id);
    }

    public SiteData getSite(String path) {
        checkDirty();
        return sitePathMap.get(path);
    }

    public FileData getFile(String path) {
        checkDirty();
        return filePathMap.get(path);
    }

    public PageData getPage(String path) {
        checkDirty();
        return pagePathMap.get(path);
    }

    public TreeNode getNode(String path) {
        checkDirty();
        return nodePathMap.get(path);
    }

    public int getParentNodeId(int id) {
        checkDirty();
        TreeNode node = getNode(id);
        if (node == null) return 0;
        return node.getParentId();
    }

    public void inheritFromParent(SiteData child) {
        checkDirty();
        SiteData site = getSite(child.getParentId());
        if (site == null) return;
        site.inheritToChild(child);
        if (child.inheritsMaster()) child.setTemplateName(site.getTemplateName());
    }

    public void inheritFromParent(PageData child) {
        checkDirty();
        SiteData site = getSite(child.getParentId());
        if (site == null) return;
        site.inheritToChild(child);
        if (site.hasDefaultPage() && site.getDefaultPageId() == child.getId()) child.setDefaultPage(true);
    }

    public void inheritFromParent(FileData child) {
        checkDirty();
        SiteData site = getSite(child.getParentId());
        if (site == null) return;
        site.inheritToChild(child);
    }

    public int getLanguageRootSiteId(Locale locale) {
        return getLanguageRootId(locale);
    }

    public SiteData getLanguageRootSite(Locale locale) {
        checkDirty();
        int id = getLanguageRootSiteId(locale);
        return id == 0 ? null : siteMap.get(id);
    }

    public boolean isLanguageRootId(int id) {
        return languageRootIds.containsValue(id);
    }

    public int getLanguageRootId(Locale locale) {
        checkDirty();
        if (!languageRootIds.containsKey(locale)) return 0;
        return languageRootIds.get(locale);
    }

    public List<Locale> getOtherLocales(Locale locale) {
        checkDirty();
        List<Locale> list = new ArrayList<>();
        for (Locale loc : languageRootIds.keySet()) {
            if (loc.equals(locale)) continue;
            list.add(loc);
        }
        return list;
    }

    public Locale getLocale(int id) {
        checkDirty();
        TreeNode node = getNode(id);
        if (node == null) return Configuration.getInstance().getStdLocale();
        return node.getLocale();
    }

}
