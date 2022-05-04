package de.elbe5.serverpagetags;

import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPIfTag extends SPTag {

    public static final String TYPE = "if";

    public SPIfTag(){
        this.type = TYPE;
    }

    boolean condition = false;

    @Override
    public void collectParameters(StringMap parameters) {
        condition = parameters.getBoolean("condition");
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        if (condition) {
            appendInner(sb, rdata);
        }
    }

}
