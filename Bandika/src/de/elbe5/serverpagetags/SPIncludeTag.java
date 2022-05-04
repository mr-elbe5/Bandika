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

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String include = getStringParam("include", rdata, "");
        if (!include.isEmpty()) {
            ServerPage.includePage(sb, include, rdata);
        }
    }

}
