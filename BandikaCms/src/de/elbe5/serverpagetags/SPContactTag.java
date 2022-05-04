package de.elbe5.serverpagetags;

import de.elbe5.base.StringHelper;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;
import de.elbe5.serverpage.ServerPage;

public class SPContactTag extends SPTag {

    private String cssClass = "";

    @Override
    public void collectParameters() {
        cssClass = getParameters().getString("cssClass", "");
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        collectParameters();
        sb.append(format("<div class=\"{1}\">",
                StringHelper.toHtml(cssClass)));
        ServerPage.includePage(sb, "page/contact", rdata);
        sb.append("</div>");
    }

}
