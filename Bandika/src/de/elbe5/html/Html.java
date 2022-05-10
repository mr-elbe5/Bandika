package de.elbe5.html;

import de.elbe5.base.LocalizedStrings;
import org.apache.commons.text.StringEscapeUtils;

public class Html {

    public static String html(String src){
        if (src == null) {
            return "";
        }
        return StringEscapeUtils.escapeHtml4(src);
    }

    public static String htmlMultiline(String src){
        if (src == null)
            return "";
        return StringEscapeUtils.escapeHtml4(src).replaceAll("\n", "\n<br>\n");
    }

    public static String localized(String key){
        return LocalizedStrings.html(key);
    }

    public static String format(String src, String... params) {
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
            String s = params[i];
            if (s.startsWith("_")){
                s = localized(s);
            }
            sb.append(s);
            p1 = p2 + placeholder.length();
        }
        sb.append(src.substring(p1));
        return sb.toString();
    }

}
