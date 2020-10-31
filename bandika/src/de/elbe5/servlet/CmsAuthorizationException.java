package de.elbe5.servlet;

import javax.servlet.http.HttpServletResponse;

public class CmsAuthorizationException extends CmsException{

    public CmsAuthorizationException(){
        super(HttpServletResponse.SC_UNAUTHORIZED);
    }

}
