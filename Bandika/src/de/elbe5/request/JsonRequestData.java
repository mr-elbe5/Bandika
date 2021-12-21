package de.elbe5.request;

import de.elbe5.application.Configuration;
import de.elbe5.user.UserBean;
import de.elbe5.user.UserData;

import javax.servlet.http.HttpServletRequest;

import java.util.Locale;

public class JsonRequestData extends RequestData {

    private UserData user=null;

    public static JsonRequestData getRequestData(HttpServletRequest request) {
        return (JsonRequestData) request.getAttribute(KEY_REQUESTDATA);
    }

    public JsonRequestData(String method, HttpServletRequest request) {
        super(method, request);
    }

    @Override
    public void init(){
        super.init();
        String token = request.getHeader("Authentication");
        if (token==null || token.isEmpty())
            token = request.getHeader("token");
        if (token==null)
            token="";
        if (!token.isEmpty())
            user = UserBean.getInstance().loginUserByToken(token);
    }

    @Override
    public UserData getLoginUser() {
        return user;
    }

    @Override
    public Locale getLocale() {
        return Configuration.getDefaultLocale();
    }

}
