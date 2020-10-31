package de.elbe5.servlet;

import javax.servlet.http.HttpServletResponse;

public abstract class CmsException extends RuntimeException{

    private int responseCode= HttpServletResponse.SC_OK;
    private String message = "";

    protected CmsException(int responseCode){
        this.responseCode=responseCode;
    }

    protected CmsException(int responseCode, String message){
        this.responseCode=responseCode;
        this.message=message;
    }

    public int getResponseCode() {
        return responseCode;
    }
}
