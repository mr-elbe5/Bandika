package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CloseDialogActionResult extends ForwardActionResult{

    private String targetId="";

    public CloseDialogActionResult(String url){
        super(url);
    }

    public CloseDialogActionResult(String url, String targetId){
        super(url);
        this.targetId=targetId;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        rdata.put(Statics.KEY_URL, url);
        if (!targetId.isEmpty())
            rdata.put(Statics.KEY_TARGETID, targetId);
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/closeDialog.ajax.jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        }
        catch (ServletException | IOException e){
            response.setStatus(ResponseCode.NOT_FOUND);
        }
    }
}
