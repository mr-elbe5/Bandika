package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.*;

public class TemplateTag implements TemplateNode{

    String type;
    TagAttributes attributes = new TagAttributes();

    Map<String, Set<String>> params = new HashMap<>();
    List<TemplateNode> childNodes = new ArrayList<>();

    public TemplateTag(String type){
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public TagAttributes getAttributes() {
        return attributes;
    }

    public void addAttribute(String key, String value){
        attributes.put(key, value);
        Set<String> set = new HashSet<>();
        addParams(value, set);
        params.put(key, set);
    }

    public void addChildNode(TemplateNode node){
        childNodes.add(node);
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendChildHtml(sb, rdata);
    }

    public void appendChildHtml(StringBuilder sb, RequestData rdata){
        for (TemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    String getAttribute(String key, RequestData rdata){
        String value = attributes.get(key);
        for (String s: params.get(key)){
            value = value.replaceAll("${" + key + "}", rdata.getString(s));
        }
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
        for (TemplateNode node : childNodes) {
            node.appendCode(sb, prefix);
        }
    }

}
