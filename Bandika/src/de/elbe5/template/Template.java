package de.elbe5.template;

import de.elbe5.request.RequestData;

public class Template extends TemplateTag{

    String docType = "";

    public Template(){
        super("template");
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendChildHtml(sb, rdata);
    }

    public String getHtml(RequestData rdata){
        StringBuilder sb = new StringBuilder();
        appendHtml(sb, rdata);
        return sb.toString();
    }

}
