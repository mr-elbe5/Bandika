package de.elbe5.serverpage;

import de.elbe5.request.RequestData;

public class ServerPage extends SPTag {

    public ServerPage(){
        super("template");
    }

    public String getHtml(RequestData rdata){
        StringBuilder sb = new StringBuilder();
        appendChildHtml(sb, rdata);
        return sb.toString();
    }

    public String getCode(){
        return getCode("tpl");
    }
    public String getCode(String prefix){
        StringBuilder sb = new StringBuilder();
        appendChildCode(sb, prefix);
        return sb.toString();
    }

}
