package de.elbe5.cms.page;

import de.elbe5.base.cache.BaseCache;
import de.elbe5.base.log.Log;
import de.elbe5.cms.rights.RightsCache;

import java.util.*;

public class PageCache extends BaseCache {

    private static PageCache instance = null;

    public static PageCache getInstance() {
        if (instance == null) {
            instance = new PageCache();
        }
        return instance;
    }

    protected PageData rootPage = null;
    protected int version = 1;

    protected Map<Locale, Integer> homePageIds = new HashMap<>();
    protected Map<Integer, PageData> pageMap = new HashMap<>();

    @Override
    public synchronized void load() {
        //Log.info("loading pages");
        PageBean bean = PageBean.getInstance();
        Map<Locale, Integer> homeMap = bean.readLanguageRootIds();
        List<PageData> pageList = PageBean.getInstance().getAllPages();
        Map<Integer, PageData> pages = new HashMap<>();
        for (PageData node : pageList) {
            pages.put(node.getId(), node);
        }
        rootPage = pages.get(PageData.ID_ROOT);
        if (rootPage == null)
            return;
        for (PageData page : pageList) {
            PageData parent = pages.get(page.getParentId());
            page.setParent(parent);
            if (parent != null) {
                parent.addSubPage(page);
            }
        }
        rootPage.inheritToChildren();
        pageMap = pages;
        homePageIds = homeMap;
    }

    @Override
    public void setDirty() {
        increaseVersion();
        super.setDirty();
        RightsCache.getInstance().setDirty();
    }

    public void increaseVersion() {
        version++;
    }

    public int getVersion() {
        return version;
    }

    public PageData getRootPage() {
        checkDirty();
        return rootPage;
    }

    public PageData getHomePage(Locale locale) {
        checkDirty();
        int id = getHomePageId(locale);
        return id == 0 ? null : pageMap.get(id);
    }

    public int getHomePageId(Locale locale) {
        checkDirty();
        if (!homePageIds.containsKey(locale)) {
            return 0;
        }
        return homePageIds.get(locale);
    }

    public PageData getPage(int id) {
        checkDirty();
        return pageMap.get(id);
    }

    public int getParentPageId(int id) {
        checkDirty();
        PageData page = getPage(id);
        if (page == null) {
            return 0;
        }
        return page.getParentId();
    }

    public List<Integer> getParentPageIds(int id) {
        checkDirty();
        List<Integer> list=new ArrayList<>();
        PageData page = getPage(id);
        if (page == null) {
            return list;
        }
        while (page.getParentId()!=PageData.ID_ROOT){
            list.add(page.getParentId());
            page=page.getParent();
        }
        return list;
    }

    public List<Locale> getOtherLocales(Locale locale) {
        checkDirty();
        List<Locale> list = new ArrayList<>();
        for (Locale loc : homePageIds.keySet()) {
            if (loc.equals(locale)) {
                continue;
            }
            list.add(loc);
        }
        return list;
    }

}
