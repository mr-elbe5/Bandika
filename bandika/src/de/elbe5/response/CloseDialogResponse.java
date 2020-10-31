package de.elbe5.response;

import de.elbe5.request.RequestData;
import de.elbe5.request.SessionRequestData;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class CloseDialogResponse extends ForwardResponse {

    private String targetId = "";

    public CloseDialogResponse(String url) {
        super(url);
    }

    public CloseDialogResponse(String url, String targetId) {
        super(url);
        this.targetId = targetId;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        rdata.put(SessionRequestData.KEY_URL, url);
        if (!targetId.isEmpty())
            rdata.put(RequestData.KEY_TARGETID, targetId);
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/closeDialog.ajax.jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
