package de.elbe5.servlet;

import javax.servlet.http.HttpServletResponse;

public class CmsInternalException extends CmsException{

    public CmsInternalException(){
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
    }

    public CmsInternalException(String message){
        super(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, message);
    }

}
