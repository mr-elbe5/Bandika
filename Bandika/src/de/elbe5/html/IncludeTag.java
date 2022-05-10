package de.elbe5.html;

import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;
import de.elbe5.template.Template;

public class IncludeTag extends TemplateTag {

    public static final String TYPE = "include";

    public IncludeTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String include = getStringParam("include", rdata, "");
        if (!include.isEmpty()) {
            Template.includePage(sb, include, rdata);
        }
    }

}
