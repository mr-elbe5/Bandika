package de.elbe5.response;

import de.elbe5.request.SessionRequestData;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class MasterView implements IResponse {

    public static String DEFAULT_MASTER = "defaultMaster";

    protected String master=DEFAULT_MASTER;

    public MasterView() {
    }

    public MasterView(String master) {
        this.master=master;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        RequestDispatcher rd = context.getRequestDispatcher("/WEB-INF/_jsp/_layout/"+master+".jsp");
        try {
            rd.forward(rdata.getRequest(), response);
        } catch (ServletException | IOException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}
