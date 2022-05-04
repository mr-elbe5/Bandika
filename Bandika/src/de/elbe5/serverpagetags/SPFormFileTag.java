package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;

public class SPFormFileTag extends SPFormLineTag {

    public static final String TYPE = "file";

    String controlPreHtml = "<input type=\"file\" class=\"form-control-file\" id=\"{1}\" name=\"{2}\" {3}>";

    public SPFormFileTag(){
        this.type = TYPE;
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        boolean multiple = getBooleanParam("multiple", rdata, false);
        sb.append(format(controlPreHtml, name, name, multiple ? "multiple" : ""));
    }

}
