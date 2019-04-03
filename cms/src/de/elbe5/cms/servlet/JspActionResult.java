package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JspActionResult extends ForwardActionResult{

    protected String jspUrl;

    public JspActionResult(String jspUrl,String masterUrl){
        super(masterUrl);
        this.jspUrl=jspUrl;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        rdata.put(Statics.KEY_JSP, jspUrl);
        super.processAction(context, rdata, response);
    }
}
