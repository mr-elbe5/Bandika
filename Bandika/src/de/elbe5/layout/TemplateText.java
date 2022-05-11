package de.elbe5.layout;

import de.elbe5.request.RequestData;

public class TemplateText implements ITemplateNode {

    String code;

    public TemplateText(String code){
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
        String result = replaceParams(code, rdata.getTemplateAttributes());
        sb.append(result);
    }

    @Override
    public void appendCode(StringBuilder sb){
        sb.append(code);
    }
}
