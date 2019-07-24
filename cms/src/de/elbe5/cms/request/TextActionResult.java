package de.elbe5.cms.request;

import de.elbe5.base.log.Log;
import de.elbe5.cms.application.Statics;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.text.MessageFormat;

public class TextActionResult implements IActionResult {

    private String text;

    public TextActionResult(String text) {
        this.text = text;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        response.setContentType(MessageFormat.format("text/html;charset={0}", Statics.ENCODING));
        if (!sendTextResponse(response))
            response.setStatus(ResponseCode.INTERNAL_SERVER_ERROR);
    }

    protected boolean sendTextResponse(HttpServletResponse response) {
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        try {
            OutputStream out = response.getOutputStream();
            if (text == null || text.length() == 0) {
                response.setHeader("Content-Length", "0");
            } else {
                byte[] bytes = text.getBytes(Statics.ENCODING);
                response.setHeader("Content-Length", Integer.toString(bytes.length));
                out.write(bytes);
            }
            out.flush();
        } catch (IOException ioe) {
            Log.error("response error", ioe);
            return false;
        }
        return true;
    }
}
