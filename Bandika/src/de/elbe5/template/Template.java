package de.elbe5.template;

import de.elbe5.request.RequestData;

public class Template extends TemplateTag{

    public Template(){
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
