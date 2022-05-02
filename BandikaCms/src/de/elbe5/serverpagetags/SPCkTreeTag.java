package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPCkTreeTag extends SPTag {

    public static final String TYPE = "cktree";

    public SPCkTreeTag(){
        this.type = TYPE;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){

    }

}
