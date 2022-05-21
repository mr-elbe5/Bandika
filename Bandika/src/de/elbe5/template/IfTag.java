package de.elbe5.template;

import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;

public class IfTag extends TemplateTag {

    public static final String TYPE = "if";

    public IfTag() {
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata) {
        boolean condition = getBooleanParam("condition", rdata, false);
        if (condition) {
            appendInner(sb, rdata);
        }
    }

}