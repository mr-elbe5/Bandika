package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPSectionTag extends SPTag {

    public static final String TYPE = "section";

    public SPSectionTag(){
        this.type = TYPE;
    }

    @Override
    public void collectVariables(RequestData rdata) {

    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){

    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){

    }

}
