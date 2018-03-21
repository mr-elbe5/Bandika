package de.elbe5.cms.tree;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.cms.file.FileBean;
import de.elbe5.cms.file.FileData;
import de.elbe5.cms.file.FileDataComparator;
import de.elbe5.cms.page.PageBean;
import de.elbe5.cms.page.PageData;
import de.elbe5.cms.site.SiteBean;
import de.elbe5.cms.site.SiteData;
import de.elbe5.webbase.servlet.SessionReader;

import javax.servlet.http.HttpServletRequest;
import java.util.*;

public class TreeCache extends BaseCache {

    private static TreeCache instance = null;

    public static TreeCache getInstance() {
        if (instance == null) {
            instance = new TreeCache();
        }
        return instance;
    }

    protected SiteData rootSite = null;
    protected int version = 1;

    protected Map<Locale, Integer> languageRootIds = new HashMap<>();
    protected Map<Integer, SiteData> siteMap = new HashMap<>();
    protected Map<Integer, FileData> fileMap = new HashMap<>();
    protected Map<Integer, PageData> pageMap = new HashMap<>();
    protected Map<Integer, TreeNode> nodeMap = new HashMap<>();
    protected Map<String, Integer> sitePathMap = new HashMap<>();
    protected Map<String, Integer> filePathMap = new HashMap<>();
    protected Map<String, Integer> pagePathMap = new HashMap<>();
    protected Map<String, Integer> nodePathMap = new HashMap<>();

    @Override
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
        rootSite = sites.get(TreeNode.ID_ROOT);
        if (rootSite == null)
            return;
        rootSite.setPath("/");
        for (SiteData site : siteList) {
            SiteData parent = sites.get(site.getParentId());
            site.setParent(parent);
            if (parent != null) {
                parent.addSite(site);
            }
        }
        for (FileData file : fileList) {
            SiteData parent = sites.get(file.getParentId());
            file.setParent(parent);
            if (parent != null) {
                parent.addFile(file);
            }
        }
        for (PageData page : pageList) {
            SiteData parent = sites.get(page.getParentId());
            page.setParent(parent);
            if (parent != null) {
                parent.addPage(page);
            }
        }
        rootSite.inheritToChildren();
        Map<String, Integer> nodePaths = new HashMap<>();
        Map<String, Integer> sitePaths = new HashMap<>();
        for (SiteData site : sites.values()) {
            sitePaths.put(site.getUrl(), site.getId());
            nodePaths.put(site.getUrl(), site.getId());
        }
        Map<String, Integer> filePaths = new HashMap<>();
        for (FileData file : files.values()) {
            filePaths.put(file.getUrl(), file.getId());
            nodePaths.put(file.getUrl(), file.getId());
        }
        Map<String, Integer> pagePaths = new HashMap<>();
        for (PageData page : pages.values()) {
            pagePaths.put(page.getUrl(), page.getId());
            nodePaths.put(page.getUrl(), page.getId());
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

    @Override
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

    public TreeNode getNode(int id) {
        checkDirty();
        return nodeMap.get(id);
    }

    public TreeNode getNode(String path) {
        checkDirty();
        int id = nodePathMap.get(path);
        return getNode(id);
    }

    public SiteData getRootSite() {
        checkDirty();
        return rootSite;
    }

    public int getLanguageRootSiteId(Locale locale) {
        return getLanguageRootId(locale);
    }

    public SiteData getLanguageRootSite(Locale locale) {
        checkDirty();
        int id = getLanguageRootSiteId(locale);
        return id == 0 ? null : siteMap.get(id);
    }

    public int getLanguageRootId(Locale locale) {
        checkDirty();
        if (!languageRootIds.containsKey(locale)) {
            return 0;
        }
        return languageRootIds.get(locale);
    }

    public int getFallbackPageId(HttpServletRequest request){
       return getLanguageRootSite(SessionReader.getSessionLocale(request)).getDefaultPage().getId();
    }

    public SiteData getSite(int id) {
        checkDirty();
        return siteMap.get(id);
    }

    public SiteData getSite(String path) {
        checkDirty();
        int id = sitePathMap.get(path);
        return getSite(id);
    }

    public PageData getPage(int id) {
        checkDirty();
        return pageMap.get(id);
    }

    public PageData getPage(String path) {
        checkDirty();
        int id = pagePathMap.get(path);
        return getPage(id);
    }

    public List<FileData> getAllFiles() {
        List<FileData> files = new ArrayList<>(fileMap.values());
        files.sort(new FileDataComparator());
        return files;
    }

    public FileData getFile(int id) {
        checkDirty();
        return fileMap.get(id);
    }

    public FileData getFile(String path) {
        checkDirty();
        int id = filePathMap.get(path);
        return getFile(id);
    }

    public int getParentNodeId(int id) {
        checkDirty();
        TreeNode node = getNode(id);
        if (node == null) {
            return 0;
        }
        return node.getParentId();
    }

    public void inheritFromParent(SiteData child) {
        checkDirty();
        SiteData site = getSite(child.getParentId());
        if (site == null) {
            return;
        }
        site.inheritToChild(child);
        if (child.inheritsMaster()) {
            child.setTemplateName(site.getTemplateName());
        }
    }

    public List<Locale> getOtherLocales(Locale locale) {
        checkDirty();
        List<Locale> list = new ArrayList<>();
        for (Locale loc : languageRootIds.keySet()) {
            if (loc.equals(locale)) {
                continue;
            }
            list.add(loc);
        }
        return list;
    }

}