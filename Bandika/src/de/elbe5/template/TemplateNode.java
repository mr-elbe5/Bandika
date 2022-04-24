package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.Set;

public interface TemplateNode {

    void appendHtml(StringBuilder sb, RequestData rdata);

    void appendCode(StringBuilder sb, String prefix);

    default void addParams(String src, Set<String> set){
        int start, end = 0;
        while (true){
            start = src.indexOf("${", end);
            if (start == -1){
                return;
            }
            end = src.indexOf("}", start + 2);
            if (end==-1){
                return;
            }
            System.out.println(src.substring(start+2, end));
            set.add(src.substring(start+2, end));
        }
    }
}
