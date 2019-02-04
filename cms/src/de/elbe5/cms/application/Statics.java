package de.elbe5.cms.application;

import de.elbe5.cms.page.PagePartFlexClass;

public class Statics {

    public static final String PARAM_CALL = "cll";
    public static final String PARAM_SUFFIX = "sfx";
    public static final String PARAM_ACTION = "act";
    public static final String NO_SUFFIX = "";
    public static final String SERVLET_SUFFIX = ".srv";
    public static final String AJAX_SUFFIX = ".ajx";
    public static final String HTML_SUFFIX = ".html";
    public static final int RESPONSE_TYPE_DIRECT = 0;
    public static final int RESPONSE_TYPE_FORWARD = 1;
    public static final String KEY_RESPONSE_TYPE = "$RESPONSE";
    public static final String KEY_FORWARD_URL = "$FORWARDURL";
    public static final String KEY_JSP = "$JSP";
    public static final String KEY_ERROR = "$ERROR";
    // takes Message class
    public static final String KEY_MESSAGE = "$MESSAGE";
    // takes String
    public static final String KEY_MESSAGEKEY = "$MESSAGEKEY";
    public static final String KEY_CLOSESCRIPT = "$CLOSESCRIPT";
    public static final String KEY_TITLE = "$TITLE";
    public static final String KEY_LOGIN = "$LOGIN";
    public static final String KEY_LOCALE = "$LOCALE";
    public static final String KEY_CAPTCHA = "$CAPTCHA";
    public static final String KEY_HOST = "$HOST";
    public static final String KEY_EDITMODE = "$EDITMODE";
    public static String MODAL_DIALOG_ID = "modalDialog";

    public static String MODAL_DIALOG_JQID = "#"+MODAL_DIALOG_ID;

    public static String ENCODING = "UTF-8";
    public static String PAGE_CONTAINER_ID = "pageContainer";
    public static String PAGE_CONTAINER_JQID = "#"+PAGE_CONTAINER_ID;
    public static String PAGE_CONTENT_ID = "pageContent";
    public static String PAGE_CONTENT_JQID = "#"+PAGE_CONTENT_ID;
    public static PagePartFlexClass DEFAULT_CLASS= PagePartFlexClass.COL12;
}
