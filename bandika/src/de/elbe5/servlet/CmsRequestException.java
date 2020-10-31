package de.elbe5.servlet;

import javax.servlet.http.HttpServletResponse;

public class CmsRequestException extends CmsException{

    public CmsRequestException(){
        super(HttpServletResponse.SC_BAD_REQUEST);
    }

}
