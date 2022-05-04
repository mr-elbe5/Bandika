package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;

public class SPFormFileTag extends SPFormLineTag {

    public static final String TYPE = "file";

    String controlPreHtml = "<input type=\"file\" class=\"form-control-file\" id=\"{1}\" name=\"{2}\" {3}>";

    private boolean multiple=false;

    public SPFormFileTag(){
        this.type = TYPE;
    }

    @Override
    public void collectParameters(StringMap parameters) {
        super.collectParameters(parameters);
        multiple = parameters.getBoolean("multiple", false);
    }

    protected void appendPreControlHtml(StringBuilder sb, RequestData rdata) {
        sb.append(format(controlPreHtml, name, name, multiple ? "multiple" : ""));
    }

}
