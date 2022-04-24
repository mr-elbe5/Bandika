package de.elbe5.template;

import de.elbe5.request.RequestData;

public class TemplateCode implements TemplateNode {

    String code;

    public TemplateCode(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        sb.append(code);
    }
}
