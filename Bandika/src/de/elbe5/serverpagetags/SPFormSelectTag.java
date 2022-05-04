package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;

public class SPFormSelectTag extends SPFormLineTag {

    public static final String TYPE = "select";

    String controlPreHtml = "<select id=\"{1}\" name=\"{2}\" class=\"form-control\" {3}>";
    String controlPostHtml = "</select>\n";

    public SPFormSelectTag(){
        this.type = TYPE;
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        padded = true;
        String onchange = getStringParam("onchange", rdata,"");
        sb.append(format(controlPreHtml, name, name, onchange.isEmpty() ? "" : "onchange=\"" + onchange + "\""));
    }

    protected void appendPostControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(controlPostHtml);
    }

}
