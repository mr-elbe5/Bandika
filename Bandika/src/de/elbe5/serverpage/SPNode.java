package de.elbe5.serverpage;

import de.elbe5.base.LocalizedStrings;
import de.elbe5.base.Log;
import de.elbe5.base.StringMap;

public interface SPNode {

    void appendHtml(StringBuilder sb, StringMap params);

    void appendCode(StringBuilder sb, String prefix);

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
                       s += LocalizedStrings.html(key);
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
