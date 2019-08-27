package de.elbe5.request;

import de.elbe5.base.data.BinaryStreamFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class BinaryStreamActionResult implements IActionResult {

    private BinaryStreamFile data;
    private boolean forceDownload;

    public BinaryStreamActionResult(BinaryStreamFile data, boolean forceDownload) {
        this.data = data;
        this.forceDownload = forceDownload;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        String contentType = data.getContentType();
        if (contentType != null && !contentType.isEmpty()) {
            contentType = "*/*";
        }
        StringBuilder contentDisposition = new StringBuilder();
        if (forceDownload) {
            contentDisposition.append("attachment;");
        }
        contentDisposition.append("filename=\"");
        contentDisposition.append(data.getFileName());
        contentDisposition.append('"');
        response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
        response.setHeader("Cache-Control", "no-cache");
        response.setHeader("Pragma", "no-cache");
        response.setContentType(contentType);
        response.setHeader("Content-Disposition", contentDisposition.toString());
        try {
            OutputStream out = response.getOutputStream();
            data.writeToStream(out);
            out.flush();
        } catch (IOException e) {
            response.setStatus(ResponseCode.NO_CONTENT);
        }
    }
}
