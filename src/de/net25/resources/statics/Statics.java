/*
  Bandika! - A Java based Content Management System
  Copyright (C) 2009 Michael Roennau

  This program is free software; you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation; either version 3 of the License, or (at your option) any later version.
  This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
  You should have received a copy of the GNU General Public License along with this program; if not, see <http://www.gnu.org/licenses/>.

  Code format: This code uses 2 blanks per indent!
*/

package de.net25.resources.statics;

import de.net25.base.controller.Controller;
import de.net25.base.BaseBean;
import de.net25.base.DataCache;
import de.net25.content.*;
import de.net25.content.fields.*;
import de.net25.resources.template.TemplateController;
import de.net25.resources.template.TemplateBean;
import de.net25.user.UserController;
import de.net25.user.UserBean;
import de.net25.user.RightBean;
import de.net25.resources.image.ImageController;
import de.net25.resources.image.ImageBean;
import de.net25.resources.document.DocumentController;
import de.net25.resources.document.DocumentBean;
import de.net25.resources.cache.CacheController;
import de.net25.communication.CommunicationController;
import de.net25.communication.CommunicationBean;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.ServletConfig;
import java.util.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.io.Writer;
import java.io.IOException;
import java.io.File;

/**
 * Class Statics is a static class for holding central static data. <br>
 * Usage:
 */
public class Statics {

  public static String ISOCODE = "ISO-8859-1";

  public static final String KEY_CONTENT = "cpg";
  public static final String KEY_USER = "usr";
  public static final String KEY_MENU = "mnu";
  public static final String KEY_IMAGE = "img";
  public static final String KEY_DOCUMENT = "doc";
  public static final String KEY_RIGHT = "rgt";
  public static final String KEY_TEMPLATE = "tpl";
  public static final String KEY_COMMUNICATION = "com";
  public static final String KEY_CACHE = "cac";

  public static final String FIELD_TEXTLINE = "textline";
  public static final String FIELD_TEXTAREA = "textarea";
  public static final String FIELD_NUMBER = "number";
  public static final String FIELD_HTML = "html";
  public static final String FIELD_IMAGE = "image";
  public static final String FIELD_DOCUMENT = "document";
  public static final String FIELD_BLOG = "blog";

  public static String BASE_PATH = "/opt/tomcat5.5/webapps/ROOT";
  public static String STATIC_BASE = "/";
  public static String DYNAMIC_BASE = "/";
  public static String STYLE_PATH = "/css/";
  public static String IMG_PATH = "/img/";
  public static String JS_PATH = "/jscript/";
  public static String FCK_PATH = "/fckeditor/";
  public static String TEMPLATE_DIR = "templates";
  public static String TEMPLATE_PATH = "";
  public static String IMAGE_PATH = null;
  public static String THUMBNAIL_PATH = null;
  public static String DOCUMENT_PATH = null;
  public static String STRING_BUNDLE = "bandika";
  public static String[] LANGUAGES = {"en", "de"};
  public static Locale[] LOCALES = null;
  public static String MAIL_SERVER = null;
  public static String MAIL_SENDER = null;
  public static String MAIL_ADDRESS = null;

  public static String HTML_TYPE;
  public static String HTML_HEADERS;

  public static final int ERROR_NONE = 0;
  public static final int ERROR_INCOMPLETE = 1;
  public static final int ERROR_TEXT_TOO_LONG = 2;
  public static final int ERROR_NOT_FOUND = 3;

  public static final int RIGHT_NONE = 0;
  public static final int RIGHT_READ = 1;
  public static final int RIGHT_EDIT = 2;

  public static final int STD_TEMPLATE = 100;

  public static final int CONT_HOME_EN = 100;
  public static final int CONT_IMPRINT_EN = 110;
  public static final int CONT_CONTACT_EN = 111;

  public static final int CONT_HOME_DE = 200;
  public static final int CONT_IMPRINT_DE = 210;
  public static final int CONT_CONTACT_DE = 211;

  public static final int CONT_MIN = 1000;

  public static long PAGE_CACHE_SIZE = 0;
  public static long DOC_CACHE_SIZE = 0;
  public static long IMG_CACHE_SIZE = 0;


  public static final Date ERROR_DATE = new Date(0x0);

  public static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss.SSS");

  public static String DATA_SOURCE_NAME = null;

  protected static Context context = null;
  protected static DataSource dataSource = null;
  private static boolean initialized=false;

  protected static HashMap<String, BaseBean> beans = new HashMap<String, BaseBean>();

  protected static HashMap<String, Controller> controllers = new HashMap<String, Controller>();

  protected static ArrayList<String> contentTypes = new ArrayList<String>();

  protected static HashMap<String, DataCache> caches = new HashMap<String, DataCache>();

  /**
   * Method init ...
   *
   * @throws Exception when
   */
  public static void init(String appName) throws Exception {
    if (initialized)
      return;
    System.out.println("initializing application...");
    context = new InitialContext();
    initStatics(appName);
    initDataSource();
    initStrings();
    initControllers();
    initBeans();
    initFields();
    initTemplates();
    for (int i = 0; i < LOCALES.length; i++)
        MenuCache.getInstance(LOCALES[i]);
    initialized=true;
    System.out.println("application initialized");
  }


  /**
   * Method initStatics
   *
   */
  protected static void initStatics(String appName) throws NamingException {
    Context envCtx = (Context) context.lookup("java:comp/env");
    StaticResources res = (StaticResources) envCtx.lookup("config_"+appName);
    DATA_SOURCE_NAME=res.getDataSource();
    System.out.println("data source is: " + DATA_SOURCE_NAME);
    BASE_PATH = res.getBasePath();
    System.out.println("base path is: " + BASE_PATH);
    DYNAMIC_BASE = res.getDynamicBase();
    System.out.println("dynamic base is: " + DYNAMIC_BASE);
    STATIC_BASE = res.getStaticBase();
    System.out.println("static base is: " + STATIC_BASE);
    STYLE_PATH = STATIC_BASE + "css/";
    System.out.println("style base is: " + STYLE_PATH);
    IMG_PATH = STATIC_BASE + "img/";
    System.out.println("img base is: " + IMG_PATH);
    JS_PATH = STATIC_BASE + "jscript/";
    System.out.println("JavaScript base is: " + JS_PATH);
    FCK_PATH = STATIC_BASE + "fckeditor/";
    System.out.println("FCKeditor base is: " + FCK_PATH);
    TEMPLATE_PATH = BASE_PATH + "/" + TEMPLATE_DIR;
    System.out.println("template path is: " + TEMPLATE_PATH);
    try {
      PAGE_CACHE_SIZE = Long.parseLong(res.getPageCacheSize());
    }
    catch (Exception e) {
    }
    try {
      DOC_CACHE_SIZE = Long.parseLong(res.getDocCacheSize());
    }
    catch (Exception e) {
    }
    try {
      IMG_CACHE_SIZE = Long.parseLong(res.getImgCacheSize());
    }
    catch (Exception e) {
    }
    System.out.println("page cache size is: " + PAGE_CACHE_SIZE + " kB");
    try {
      IMAGE_PATH = res.getImagePath();
      if (IMAGE_PATH.length() == 0) {
        IMAGE_PATH = null;
        THUMBNAIL_PATH = null;
      } else {
        THUMBNAIL_PATH = IMAGE_PATH + "/thumbnails";
        EnsureImagePaths();
      }
    }
    catch (Exception ignore) {
    }
    System.out.println("image path is: " + IMAGE_PATH);
    System.out.println("thumbnail path is: " + THUMBNAIL_PATH);
    if (IMAGE_PATH == null)
      System.out.println("images will be stored in the database");
    System.out.println("image cache size is: " + IMG_CACHE_SIZE + " kB");
    try {
      DOCUMENT_PATH = res.getDocumentPath();
      if (DOCUMENT_PATH.length() == 0)
        DOCUMENT_PATH = null;
      else
        EnsureDocumentPath();
    }
    catch (Exception ignore) {
    }
    System.out.println("document path is: " + DOCUMENT_PATH);
    if (DOCUMENT_PATH == null)
      System.out.println("documents will be stored in the database");
    System.out.println("document cache size is: " + DOC_CACHE_SIZE + " kB");
    String lang = res.getLanguages();
    StringTokenizer stk = new StringTokenizer(lang, ",");
    LANGUAGES = new String[stk.countTokens()];
    LOCALES = new Locale[stk.countTokens()];
    for (int i = 0; i < LANGUAGES.length; i++) {
      LANGUAGES[i] = stk.nextToken().toLowerCase();
      LOCALES[i] = new Locale(LANGUAGES[i]);
    }
    System.out.println("languages are: " + lang);
    MAIL_SERVER = res.getMailServer();
    System.out.println("mail server is: " + MAIL_SERVER);
    MAIL_ADDRESS = res.getMailAddress();
    System.out.println("mail address is: " + MAIL_ADDRESS);
    MAIL_SENDER = res.getMailSender();
    System.out.println("mail sender is: " + MAIL_SENDER);
    HTML_TYPE = "xmlns=\"http://www.w3.org/1999/xhtml\" xml:lang=\"en\" lang=\"en\"";
    HTML_HEADERS = "<meta http-equiv=\"Cache-Control\" content=\"no-cache\">\n " +
        "<meta http-equiv=\"Pragma\" content=\"no-cache\">\n  " +
        "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=" + ISOCODE + "\">";
    initialized=true;
  }

  public static boolean isInitialized() {
    return initialized;
  }

  /**
   * Method initDataSource
   *
   * @throws Exception when data processing is not successful
   */
  protected static void initDataSource() throws Exception {
    Context envCtx = (Context) context.lookup("java:comp/env");
    dataSource = (DataSource) envCtx.lookup(Statics.DATA_SOURCE_NAME);
  }

  /**
   * Method initStrings
   *
   * @throws Exception when data processing is not successful
   */
  protected static void initStrings() throws Exception {
    Strings.init(STRING_BUNDLE);
  }

  /**
   * Method getConnection returns the connection of this Statics object.
   *
   * @return the connection (type Connection) of this Statics object.
   * @throws SQLException when data processing is not successful
   */
  public static Connection getConnection() throws SQLException {
    return dataSource.getConnection();
  }

  /**
   * Method EnsureImagePaths
   *
   * @return success
   */
  public static boolean EnsureImagePaths() {
    boolean success = false;
    try {
      File f = new File(IMAGE_PATH);
      if (!f.exists())
        success = f.mkdir();
      f = new File(THUMBNAIL_PATH);
      if (!f.exists())
        success = f.mkdir();
    }
    catch (Exception e) {
    }
    return success;
  }

  /**
   * Method EnsureDocumentPath
   *
   * @return success
   */
  public static boolean EnsureDocumentPath() {
    boolean success = false;
    try {
      File f = new File(DOCUMENT_PATH);
      if (!f.exists())
        success = f.mkdir();
    }
    catch (Exception e) {
    }
    return success;
  }

  /**
   * Method setDocAndContentType
   *
   * @param out      of type Writer
   * @param response of type HttpServletResponse
   * @throws IOException when data processing is not successful
   */
  public static void setDocAndContentType(Writer out, HttpServletResponse response) throws IOException {
    out.write("<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.0 Transitional//EN\" \"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd\">");
    response.setContentType("text/html; charset=" + ISOCODE);
  }

  /**
   * Method setNoCache sets the noCache of this Statics object.
   *
   * @param response the noCache of this Statics object.
   */
  public static void setNoCache(HttpServletResponse response) {
    response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
    response.setHeader("Cache-Control", "no-cache");
    response.setHeader("Pragma", "no-cache");
  }

  /**
   * Method getStdLocale returns the stdLocale of this Statics object.
   *
   * @return the stdLocale (type Locale) of this Statics object.
   */
  public static Locale getStdLocale() {
    return LOCALES[0];
  }

  /**
   * Method getLocale
   *
   * @param lang of type String
   * @return Locale
   */
  public static Locale getLocale(String lang) {
    for (int i = 0; i < LANGUAGES.length; i++)
      if (LANGUAGES[i].equalsIgnoreCase(lang))
        return LOCALES[i];
    return getStdLocale();
  }

  /**
   * Method getBestLocale
   *
   * @param locale of type Locale
   * @return Locale
   */
  public static Locale getBestLocale(Locale locale) {
    for (Locale LOCALE : LOCALES) {
      if (locale.getLanguage().equals(LOCALE.getLanguage()))
        return LOCALE;
    }
    return getStdLocale();
  }

  /**
   * Method getStdLanguage returns the stdLanguage of this Statics object.
   *
   * @return the stdLanguage (type String) of this Statics object.
   */
  public static String getStdLanguage() {
    return LANGUAGES[0];
  }

  /**
   * Method getLanguage
   *
   * @param locale of type Locale
   * @return String
   */
  public static String getLanguage(Locale locale) {
    for (int i = 0; i < LOCALES.length; i++)
      if (LOCALES[i].equals(locale))
        return LANGUAGES[i];
    return getStdLanguage();
  }

  /**
   * Method getContentHomeId
   *
   * @param locale of type Locale
   * @return int
   */
  public static int getContentHomeId(Locale locale) {
    if (locale.getLanguage().equalsIgnoreCase("de"))
      return CONT_HOME_DE;
    return CONT_HOME_EN;
  }

  /**
   * Method getContentImprintId
   *
   * @param locale of type Locale
   * @return int
   */
  public static int getContentImprintId(Locale locale) {
    if (locale.getLanguage().equalsIgnoreCase("de"))
      return CONT_IMPRINT_DE;
    return CONT_IMPRINT_EN;
  }

  /**
   * Method getContentContactId
   *
   * @param locale of type Locale
   * @return int
   */
  public static int getContentContactId(Locale locale) {
    if (locale.getLanguage().equalsIgnoreCase("de"))
      return CONT_CONTACT_DE;
    return CONT_CONTACT_EN;
  }

  /**
   * Method initControllers
   */
  protected static void initControllers() {
    addController(KEY_CONTENT, new ContentController());
    addController(KEY_USER, new UserController());
    addController(KEY_IMAGE, new ImageController());
    addController(KEY_DOCUMENT, new DocumentController());
    addController(KEY_TEMPLATE, new TemplateController());
    addController(KEY_COMMUNICATION, new CommunicationController());
    addController(KEY_CACHE, new CacheController());
  }

  /**
   * Method initBeans
   */
  protected static void initBeans() {
    addBean(Statics.KEY_CONTENT, new ContentBean());
    addBean(KEY_USER, new UserBean());
    addBean(KEY_IMAGE, new ImageBean());
    addBean(KEY_DOCUMENT, new DocumentBean());
    addBean(KEY_TEMPLATE, new TemplateBean());
    addBean(KEY_RIGHT, new RightBean());
    addBean(KEY_COMMUNICATION, new CommunicationBean());
  }

  /**
   * Method initFields
   */
  protected static void initFields() {
    BaseField.addBaseFieldClass(FIELD_TEXTLINE, TextLineField.class);
    BaseField.addBaseFieldClass(FIELD_TEXTAREA, TextAreaField.class);
    BaseField.addBaseFieldClass(FIELD_HTML, HtmlField.class);
    BaseField.addBaseFieldClass(FIELD_IMAGE, ImageField.class);
    BaseField.addBaseFieldClass(FIELD_DOCUMENT, DocumentField.class);
    BaseField.addBaseFieldClass(FIELD_BLOG, BlogField.class);
  }

  /**
   * Method initTemplates
   */
  protected static void initTemplates() {
    TemplateBean bean = (TemplateBean) getBean(KEY_TEMPLATE);
    bean.loadNewTemplates();
    bean.writeTemplateList();
  }

  /**
   * Method addBean
   *
   * @param key  of type String
   * @param bean of type BaseBean
   */
  public static void addBean(String key, BaseBean bean) {
    bean.init();
    beans.put(key, bean);
    DataCache cache = bean.getCache();
    if (cache != null)
      caches.put(key, cache);
  }

  /**
   * Method getBean
   *
   * @param key of type String
   * @return BaseBean
   */
  public static BaseBean getBean(String key) {
    if (key == null || key.length() == 0)
      key = KEY_CONTENT;
    return beans.get(key);
  }

  /**
   * Method getCache
   *
   * @param key of type String
   * @return DataCache
   */
  public static DataCache getCache(String key) {
    if (key == null || key.length() == 0)
      key = KEY_CONTENT;
    return caches.get(key);
  }


  /**
   * Method addController
   *
   * @param key  of type String
   * @param data of type Controller
   */
  public static void addController(String key, Controller data) {
    controllers.put(key, data);
  }

  /**
   * Method getController
   *
   * @param key of type String
   * @return Controller
   */
  public static Controller getController(String key) {
    if (key == null || key.length() == 0)
      key = KEY_CONTENT;
    return controllers.get(key);
  }

  /**
   * Method getContentTypes returns the contentTypes of this Statics object.
   *
   * @return the contentTypes (type ArrayList<String>) of this Statics object.
   */
  public static ArrayList<String> getContentTypes() {
    return contentTypes;
  }

}
