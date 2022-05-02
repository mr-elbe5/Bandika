package de.elbe5.serverpagetags;

import de.elbe5.base.StringFormatter;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

import javax.servlet.http.HttpServletRequest;

public class SPFormFileTag extends SPFormLineTag {

    public static final String TYPE = "file";

    String controlPreHtml = "<input type=\"file\" class=\"form-control-file\" id=\"{1}\" name=\"{2}\" {3}>";

    private boolean multiple=false;

    public SPFormFileTag(){
        this.type = TYPE;
    }

    @Override
    public void collectVariables(RequestData rdata) {
        super.collectVariables(rdata);
        multiple = rdata.getPageAttributes().getBoolean("multiple", false);
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format(controlPreHtml, name, name, multiple ? "multiple" : ""));
    }

}
