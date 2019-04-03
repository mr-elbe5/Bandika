package de.elbe5.cms.servlet;

import de.elbe5.cms.application.Statics;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.text.MessageFormat;

public class XmlActionResult extends TextActionResult{

    public XmlActionResult(String text){
        super(text);
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        response.setContentType(MessageFormat.format("text/xml;charset={0}", Statics.ENCODING));
        if (!sendTextResponse(response))
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
