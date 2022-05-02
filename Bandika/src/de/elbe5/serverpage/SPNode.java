package de.elbe5.serverpage;

import de.elbe5.base.LocalizedStrings;
import de.elbe5.base.Log;
import de.elbe5.base.StringHelper;
import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;

public interface SPNode {

    public static final String TAG_PREFIX = "spg";

    void appendHtml(StringBuilder sb, RequestData rdata);

    void appendCode(StringBuilder sb);

    default String toHtml(String src){
        return StringHelper.toHtml(src);
    }

    default String toHtmlMultiline(String src){
        return StringHelper.toHtmlMultiline(src);
    }

    default String localizedString(String key){
        return LocalizedStrings.html(key);
    }

    default String format(String src, String... params) {
        StringBuilder sb = new StringBuilder();
        int p1 = 0;
        int p2;
        String placeholder;
        for (int i = 0; i < params.length; i++) {
            placeholder = "{" + (i + 1) + "}";
            p2 = src.indexOf(placeholder, p1);
            if (p2 == -1)
                break;
            sb.append(src, p1, p2);
            sb.append(params[i]);
            p1 = p2 + placeholder.length();
        }
        sb.append(src.substring(p1));
        return sb.toString();
    }

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
