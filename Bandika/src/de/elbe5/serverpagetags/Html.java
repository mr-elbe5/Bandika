package de.elbe5.serverpagetags;

import de.elbe5.request.RequestData;
import de.elbe5.serverpage.SPTag;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public class Html {

    protected final StringBuilder sb = new StringBuilder();

    public Html add(String html){
        sb.append(html);
        return this;
    }

    public Html add(String html, Object... params){
        sb.append(format(html, params));
        return this;
    }

    public Html add(SPTag tag, RequestData rdata){
        tag.appendHtml(sb, rdata);
        return this;
    }

    public String toString(){
        return sb.toString();
    }

    public String toString(Object... params){
        return format(sb.toString(), params);
    }

    public String toPrettyString(){
        String html = toString();
        Document doc = Jsoup.parse(html);
        return doc.toString();
    }

    public String toPrettyString(Object... params){
        String html = toString(params);
        Document doc = Jsoup.parse(html);
        return doc.toString();
    }

    protected String format(String src, Object... params) {
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
            sb.append(params[i].toString());
            p1 = p2 + placeholder.length();
        }
        sb.append(src.substring(p1));
        return sb.toString();
    }

}
