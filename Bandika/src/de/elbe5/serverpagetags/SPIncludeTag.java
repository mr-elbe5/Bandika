package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
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
    public void collectParameters(StringMap parameters) {
        pagePath = parameters.getString("include");
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        if (!pagePath.isEmpty()) {
            ServerPage.includePage(sb, pagePath, rdata);
        }
    }

}
