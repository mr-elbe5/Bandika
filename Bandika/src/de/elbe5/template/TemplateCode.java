package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.HashSet;
import java.util.Set;

public class TemplateCode implements TemplateNode {

    String code;
    Set<String> params = new HashSet<>();

    public TemplateCode(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
        addParams(code, params);
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        String result = code;
        for (String param : params){
            String value = rdata.getString(param);
            result = result.replaceAll("${"+param+"}", value);
        }
        sb.append(result);
    }

    @Override
    public void appendCode(StringBuilder sb, String prefix){
        sb.append(code);
    }
}
