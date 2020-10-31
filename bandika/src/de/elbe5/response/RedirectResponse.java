package de.elbe5.response;

import de.elbe5.request.SessionRequestData;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class RedirectResponse implements IResponse {

    private final String url;

    public RedirectResponse(String url) {
        this.url=url;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response) {
        rdata.put("redirectUrl", url);
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/redirect.jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
