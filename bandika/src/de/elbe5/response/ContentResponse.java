package de.elbe5.response;

import de.elbe5.content.ContentData;
import de.elbe5.request.SessionRequestData;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;

public class ContentResponse extends MasterView {

    private final ContentData data;

    public ContentResponse(ContentData data) {
        this.data=data;
    }

    public ContentResponse(ContentData data, String master) {
        super(master);
        this.data=data;
    }

    @Override
    public void processView(ServletContext context, SessionRequestData rdata, HttpServletResponse response)  {
        //Log.log("process view");
        rdata.setCurrentRequestContent(data);
        super.processView(context, rdata, response);
    }
}
