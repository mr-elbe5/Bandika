package de.elbe5.serverpagetags;

import de.elbe5.base.StringFormatter;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import javax.servlet.http.HttpServletRequest;

public class SPFormTextAreaTag extends SPFormLineTag {

    public static final String TYPE = "textarea";

    public SPFormTextAreaTag(){
        this.type = TYPE;
    }

    String height = "";

    String controlPreHtml = """
            <textarea id="{1}" name="{2}" class="form-control" {3}>""";
    String controlPostHtml = "</textarea>\n";

    @Override
    public void collectVariables(RequestData rdata) {
        super.collectVariables(rdata);
        height = rdata.getPageAttributes().getString("height", "");
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(StringFormatter.format(controlPreHtml, name, name, height.isEmpty() ? "" : "style=\"height:" + height + "\""));
    }

    protected void appendPostControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(controlPostHtml);
    }

}
