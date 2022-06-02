package de.elbe5.template;

import de.elbe5.base.Log;
import de.elbe5.base.StringMap;
import de.elbe5.base.Strings;
import de.elbe5.request.RequestData;
import de.elbe5.response.IHtmlBuilder;

public interface ITemplateNode extends IHtmlBuilder {

    void appendHtml(StringBuilder sb, RequestData rdata);

    default String replaceParams(String src, StringMap params)  {
        String s = "";
        int p1 = 0;
        int p2 = 0;
        while (true) {
            int varStart = src.indexOf("{{", p1);
            if (varStart != -1){
                p2 = varStart;
                s += src.substring(p1,p2);
                varStart += 2;
                int varEnd = src.indexOf("}}", varStart);
                if (varEnd != -1){
                    String key = src.substring(varStart,varEnd);
                    if (key.contains("{{")){
                        p1 = p2;
                        Log.warn("parse error: no matching '}}'");
                        break;
                    }
                    if (key.startsWith("_")) {
                       s += getHtml(key);
                    }
                    else{
                        s += params.getString(key);
                    }
                    p1 = varEnd +2;
                }
                else{
                    p1 = p2;
                    Log.warn("parse error");
                    break;
                }
            }
            else{
                break;
            }
        }
        s += src.substring(p1);
        return s;
    }
}
