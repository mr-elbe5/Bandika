package de.elbe5.request;

import de.elbe5.base.data.BinaryFile;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;

public class BinaryActionResult implements IActionResult {

    private BinaryFile data;
    private boolean forceDownload;

    public BinaryActionResult(BinaryFile data, boolean forceDownload) {
        this.data = data;
        this.forceDownload = forceDownload;
    }

    @Override
    public void processAction(ServletContext context, RequestData rdata, HttpServletResponse response) {
        if (data.getContentType() != null && !data.getContentType().isEmpty()) {
            data.setContentType("*/*");
        }
        response.setContentType(data.getContentType());
        try {
            OutputStream out = response.getOutputStream();
            if (data.getBytes() == null) {
                response.setHeader("Content-Length", "0");
            } else {
                response.setHeader("Expires", "Tues, 01 Jan 1980 00:00:00 GMT");
                response.setHeader("Cache-Control", "no-cache");
                response.setHeader("Pragma", "no-cache");
                StringBuilder sb = new StringBuilder();
                if (forceDownload) {
                    sb.append("attachment;");
                }
                sb.append("filename=\"");
                sb.append(data.getFileName());
                sb.append('"');
                response.setHeader("Content-Disposition", sb.toString());
                response.setHeader("Content-Length", Integer.toString(data.getBytes().length));
                out.write(data.getBytes());
            }
            out.flush();
        } catch (IOException e) {
            response.setStatus(ResponseCode.NO_CONTENT);
        }
    }
}
