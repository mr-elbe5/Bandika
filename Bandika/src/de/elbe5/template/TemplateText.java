package de.elbe5.template;

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
        String result = format(code, rdata.getTemplateAttributes());
        sb.append(result);
    }

}
