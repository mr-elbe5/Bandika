package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;

public class SPTextFieldTag extends SPFieldTag {

    public static final String TYPE = "textfield";

    public SPTextFieldTag(){
        this.type = TYPE;
    }

    @Override
    public void collectVariables(RequestData rdata) {
        super.collectVariables(rdata);
    }

    @Override
    public void appendTagStart(StringBuilder sb, RequestData rdata){

    }

    @Override
    public void appendTagEnd(StringBuilder sb, RequestData rdata){

    }

}
