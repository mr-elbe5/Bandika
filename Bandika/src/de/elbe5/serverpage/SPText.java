package de.elbe5.serverpage;

import de.elbe5.base.StringMap;

public class SPText implements SPNode {

    String code;

    public SPText(String code){
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    @Override
    public void appendHtml(StringBuilder sb, StringMap params){
        String result = replaceParams(code, params);
        sb.append(result);
    }

    @Override
    public void appendCode(StringBuilder sb, String prefix){
        sb.append(code);
    }
}
