package de.elbe5.serverpagetags;

import de.elbe5.base.StringHelper;
import de.elbe5.html.Html;
import de.elbe5.request.RequestData;
import de.elbe5.template.TemplateTag;
import de.elbe5.template.Template;

public class SPContactTag extends TemplateTag {

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String cssClass = getStringParam("cssClass", rdata, "");
        sb.append(Html.format("<div class=\"{1}\">",
                StringHelper.toHtml(cssClass)));
        Template.includePage(sb, "page/contact", rdata);
        sb.append("</div>");
    }

}
