package de.elbe5.servlet;

import javax.servlet.http.HttpServletResponse;

public class CmsAssertionException extends CmsException{

    public CmsAssertionException(){
        super(HttpServletResponse.SC_CONFLICT);
    }

    public CmsAssertionException(String message){
        super(HttpServletResponse.SC_CONFLICT, message);
    }

}
