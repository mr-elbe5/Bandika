package de.elbe5.cms.servlet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectActionResult extends ForwardActionResult{

    public RedirectActionResult(String url){
        super(url);
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        rdata.put("redirectUrl",url);
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/redirect.jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        }
        catch (ServletException | IOException e){
            response.setStatus(ResponseCode.NOT_FOUND);
        }
    }
}
