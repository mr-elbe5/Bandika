package de.elbe5.cms.servlet;

import de.elbe5.base.data.BinaryFileData;
import de.elbe5.base.data.KeyValueMap;
import de.elbe5.base.data.Locales;
import de.elbe5.base.log.Log;
import de.elbe5.base.util.StringUtil;
import de.elbe5.cms.application.Statics;
import de.elbe5.cms.application.Strings;
import de.elbe5.cms.rights.Right;
import de.elbe5.cms.rights.SystemZone;
import de.elbe5.cms.user.UserData;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class RequestData extends KeyValueMap {

    public static RequestData getRequestData(HttpServletRequest request){
        return (RequestData) request.getAttribute(Statics.KEY_REQUESTDATA);
    }

    private HttpServletRequest request;
    private HttpSession session;

    private String controllerName = "home";
    private String methodName = "index";
    private int id = 0;

    private String method;

    private FormError formError=null;

    private Controller controller;

    public RequestData(HttpServletRequest request, Controller controller) {
        this.request = request;
        this.controller=controller;
        method = request.getMethod().toUpperCase();
    }

    public HttpServletRequest getRequest() {
        return request;
    }

    public IActionResult invokeAction(){
        assert(controller.getKey().equals(controllerName));
        try {
            Method method = controller.getClass().getMethod(methodName, RequestData.class);
            Object result = method.invoke(controller, this);
            if (result instanceof IActionResult)
                return (IActionResult) result;
            return new ErrorActionResult(ResponseCode.INTERNAL_SERVER_ERROR);
        }
        catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e){
            return new ErrorActionResult(ResponseCode.BAD_REQUEST);
        }
    }

    public int getId() {
        return id;
    }

    public boolean isPostback() {
        return method.equals("POST");
    }

    /*********** message *********/

    public boolean hasMessage() {
        return containsKey(Statics.KEY_MESSAGE);
    }

    public void setMessage(String msg, String type){
        put(Statics.KEY_MESSAGE,msg);
        put(Statics.KEY_MESSAGETYPE,type);
    }

    /************ form error *************/

    public FormError getFormError(boolean create) {
        if (formError==null && create)
            formError=new FormError();
        return formError;
    }

    public void addFormError(String s){
        getFormError(true).addFormError(s);
    }

    public void addFormField(String field){
        getFormError(true).addFormField(field);
    }

    public void addIncompleteField(String field) {
        getFormError(true).addFormField(field);
        getFormError(false).setFormIncomplete();
    }

    public boolean hasFormError(){
        return formError!=null && !formError.isEmpty();
    }

    public boolean hasFormErrorField(String name){
        if (formError==null)
            return false;
        return formError.hasFormErrorField(name);
    }

    public boolean checkFormErrors(){
        if (formError==null)
            return true;
        if (formError.isFormIncomplete())
            formError.addFormError(Strings._notComplete.string(getSessionLocale()));
        return formError.isEmpty();
    }

    /************** uri path *****************/

    public void readUri(){
        String uri = request.getRequestURI();
        StringTokenizer stk = new StringTokenizer(uri, "/", false);
        if (stk.hasMoreTokens()) {
            controllerName = stk.nextToken();
            if (stk.hasMoreTokens()) {
                methodName = stk.nextToken();
                if (stk.hasMoreTokens()) {
                    id = StringUtil.toInt(stk.nextToken());
                }
            }
        }
    }

    public Controller getController(){
        return ControllerCache.getController(controllerName);
    }

    public Method getAction(Controller controller){
        try {
            return controller.getClass().getMethod(methodName, RequestData.class);
        }
        catch (NoSuchMethodException e){
            Log.error("Controller "+controller.getClass().getSimpleName()+" does not have the method "+methodName, e);
            return null;
        }
    }

    /************** request attributes *****************/

    public void readRequestParams() {
        String type = request.getContentType();
        if (type != null && type.toLowerCase().startsWith("multipart/form-data")) {
            getMultiPartParams();
        } else {
            getSinglePartParams();
        }
    }

    private void getSinglePartParams() {
        Enumeration<?> enm = request.getParameterNames();
        while (enm.hasMoreElements()) {
            String key = (String) enm.nextElement();
            String[] strings = request.getParameterValues(key);
            put(key, strings);
        }
    }

    private void getMultiPartParams() {
        Map<String, List<String>> params=new HashMap<>();
        try {
            Collection<Part> parts = request.getParts();
            for (Part part : parts) {
                String name = part.getName();
                String fileName = getFileName(part);
                if (fileName != null) {
                    BinaryFileData file = getMultiPartFile(part, fileName);
                    if (file != null) {
                        put(name, file);
                    }
                } else {
                    String param = getMultiPartParameter(part);
                    if (param != null) {
                        List<String> values;
                        if (params.containsKey(name))
                            values=params.get(name);
                        else {
                            values=new ArrayList<>();
                            params.put(name,values);
                        }
                        values.add(param);
                    }
                }
            }
        } catch (Exception e) {
            Log.error("error while parsing multipart params",e);
        }
        for (String key : params.keySet()){
            List<String> list = params.get(key);
            if (list.size() == 1) {
                put(key, list.get(0));
            } else {
                String[] strings=new String[list.size()];
                for (int i = 0; i < list.size(); i++) {
                    strings[i]=list.get(i);
                }
                put(key, strings);
            }
        }
    }

    private String getMultiPartParameter(Part part) {
        try {
            byte[] bytes = new byte[(int) part.getSize()];
            int read = part.getInputStream().read(bytes);
            if (read > 0) {
                return new String(bytes, Statics.ENCODING);
            }
        } catch (Exception e) {
            Log.error("could not extract parameter from multipart", e);
        }
        return null;
    }

    private BinaryFileData getMultiPartFile(Part part, String fileName) {
        try {
            BinaryFileData file = new BinaryFileData();
            file.setFileName(fileName);
            file.setContentType(part.getContentType());
            file.setFileSize((int) part.getSize());
            InputStream in = part.getInputStream();
            if (in == null) {
                return null;
            }
            ByteArrayOutputStream out = new ByteArrayOutputStream(file.getFileSize());
            byte[] buffer = new byte[8096];
            int len;
            while ((len = in.read(buffer, 0, 8096)) != -1) {
                out.write(buffer, 0, len);
            }
            file.setBytes(out.toByteArray());
            return file;
        } catch (Exception e) {
            Log.error("could not extract file from multipart", e);
            return null;
        }
    }

    private String getFileName(Part part) {
        for (String cd : part.getHeader("content-disposition").split(";")) {
            if (cd.trim().startsWith("filename")) {
                return cd.substring(cd.indexOf('=') + 1).trim().replace("\"", "");
            }
        }
        return null;
    }

    /************** session attributes ***************/

    public void initSession(){
        session = request.getSession(true);
        if (session.isNew()){
            Locale requestLocale=request.getLocale();
            if (Locales.getInstance().hasLocale(requestLocale))
                setSessionLocale(requestLocale);
            StringBuffer url = request.getRequestURL();
            String uri = request.getRequestURI();
            String host = url.substring(0, url.indexOf(uri));
            setSessionHost(host);
        }
    }

    public void setSessionObject(String key, Object obj) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.setAttribute(key, obj);
    }

    public Object getSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return null;
        }
        return request.getSession().getAttribute(key);
    }

    public void removeSessionObject(String key) {
        HttpSession session = request.getSession();
        if (session == null) {
            return;
        }
        session.removeAttribute(key);
    }

    public void setSessionUser(UserData data) {
        setSessionObject(Statics.KEY_LOGIN, data);
    }

    public UserData getSessionUser() {
        return (UserData) getSessionObject(Statics.KEY_LOGIN);
    }

    public String getLoginName() {
        UserData loginData = getSessionUser();
        return loginData == null ? "" : loginData.getName();
    }

    public int getLoginId() {
        UserData loginData = getSessionUser();
        return loginData == null ? 0 : loginData.getId();
    }

    public boolean isLoggedIn() {
        UserData loginData = getSessionUser();
        return loginData != null;
    }

    public boolean hasAnySystemRight() {
        UserData data = getSessionUser();
        return data != null && data.checkRights() && data.getRights().hasAnySystemRight();
    }

    public boolean hasAnyElevatedSystemRight() {
        UserData data = getSessionUser();
        return data != null && data.checkRights() && data.getRights().hasAnyElevatedSystemRight();
    }

    public boolean hasAnyContentRight() {
        UserData data = getSessionUser();
        return data != null && data.checkRights() && data.getRights().hasAnyContentRight();
    }

    public boolean hasSystemRight(SystemZone zone, Right right) {
        UserData data = getSessionUser();
        return data != null && data.checkRights() && data.getRights().hasSystemRight(zone, right);
    }

    public boolean hasContentRight(int id, Right right) {
        UserData data = getSessionUser();
        return data != null && data.checkRights() && data.getRights().hasContentRight(id, right);
    }

    public boolean isEditMode(){
        return request!=null && getSessionObject(Statics.KEY_EDITMODE) != null;
    }

    public void setSessionLocale() {
        setSessionLocale(Locales.getInstance().getDefaultLocale());
    }

    public Locale getSessionLocale() {
        Locale locale = (Locale) getSessionObject(Statics.KEY_LOCALE);
        if (locale == null) {
            return Locales.getInstance().getDefaultLocale();
        }
        return locale;
    }

    public void setSessionLocale(Locale locale) {
        if (Locales.getInstance().hasLocale(locale)) {
            setSessionObject(Statics.KEY_LOCALE, locale);
        } else {
            setSessionObject(Statics.KEY_LOCALE, Locales.getInstance().getDefaultLocale());
        }
    }

    public void setSessionHost(String host) {
        setSessionObject(Statics.KEY_HOST, host);
    }

    public String getSessionHost() {
        return (String) getSessionObject(Statics.KEY_HOST);
    }

    public void resetSession() {
        Locale locale = getSessionLocale();
        request.getSession(true);
        setSessionLocale(locale);
    }

    public void setEditMode(boolean flag){
        if (flag)
            setSessionObject(Statics.KEY_EDITMODE, "true");
        else
            removeSessionObject(Statics.KEY_EDITMODE);
    }

    /*************** static methods ***************/

    public void setNoCache(HttpServletResponse response) {
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
    }

}
