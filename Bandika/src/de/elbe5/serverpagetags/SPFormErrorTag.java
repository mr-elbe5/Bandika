package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPFormErrorTag extends SPTag {

    public static final String TYPE = "formerror";

    public SPFormErrorTag(){
        this.type = TYPE;
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata) {
        if (rdata.hasFormError()) {
            sb.append("<div class=\"formError\">\n")
                    .append(toHtmlMultiline(rdata.getFormError(false).getFormErrorString()))
                    .append("</div>");
        }
    }

}
