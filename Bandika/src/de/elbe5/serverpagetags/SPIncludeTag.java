package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;
import de.elbe5.serverpage.ServerPage;

public class SPIncludeTag extends SPTag {

    public static final String TYPE = "include";

    public SPIncludeTag(){
        this.type = TYPE;
    }

    String pagePath = "";

    @Override
    public void collectParameters() {
        pagePath = getParameters().getString("include");
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        collectParameters();
        if (!pagePath.isEmpty()) {
            ServerPage.includePage(sb, pagePath, rdata);
        }
    }

}
