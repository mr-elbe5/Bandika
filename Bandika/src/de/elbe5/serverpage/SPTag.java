package de.elbe5.serverpage;

import de.elbe5.base.StringMap;
import de.elbe5.request.RequestData;

import java.util.*;

public class SPTag implements SPNode {

    String type;
    SPTagAttributes attributes = new SPTagAttributes();

    List<SPNode> childNodes = new ArrayList<>();

    public SPTag(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public SPTagAttributes getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, String value){
        attributes.put(key, value);
    }

    public void addChildNode(SPNode node){
        childNodes.add(node);
    }

    @Override
    public void appendHtml(StringBuilder sb, StringMap params){
        appendChildHtml(sb, params);
    }

    public void appendChildHtml(StringBuilder sb, StringMap params){
        for (SPNode node : childNodes) {
            node.appendHtml(sb, params);
        }
    }

    String getAttribute(String key, RequestData rdata){
        String value = attributes.get(key);
        return value;
    }

    @Override
    public void appendCode(StringBuilder sb, String prefix){
        sb.append("<").append(prefix).append(":").append(type);
        for (String key : attributes.keySet()){
            sb.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
        }
        if (childNodes.isEmpty()){
            sb. append(" />");
        }
        else {
            sb.append(">");
            appendChildCode(sb, prefix);
            sb.append("</").append(prefix).append(":").append(type).append(">");
        }
    }

    public void appendChildCode(StringBuilder sb, String prefix){
        for (SPNode node : childNodes) {
            node.appendCode(sb, prefix);
        }
    }

}
