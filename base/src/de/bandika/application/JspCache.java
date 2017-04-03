package de.bandika.application;

import de.bandika._base.BaseCache;

import java.util.HashMap;

public class JspCache extends BaseCache {

  public static final String CACHEKEY = "cache|jsp";

  private static JspCache instance = null;

  public String getCacheKey() {
    return CACHEKEY;
  }

  public static JspCache getInstance() {
    if (instance == null) {
      instance = new JspCache();
    }
    return instance;
  }

  protected HashMap<String, String> jspMap = new HashMap<String, String>();

  public void load() {
    ApplicationBean bean = ApplicationBean.getInstance();
    jspMap = bean.getJsps();
  }

  public String getJsp(String name) {
    checkDirty();
    return jspMap.get(name);
  }

}
