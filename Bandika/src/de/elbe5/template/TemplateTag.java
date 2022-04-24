package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.ArrayList;
import java.util.List;

public class TemplateTag implements TemplateNode{

    String type;
    TagAttributes attributes = new TagAttributes();
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

    public void addChildNode(TemplateNode node){
        childNodes.add(node);
    }

    @Override
    public void appendHtml(StringBuilder sb, RequestData rdata){
        sb.append("<xxx:").append(type);
        for (String key : attributes.keySet()){
            sb.append(" ").append(key).append("=\"").append(attributes.get(key)).append("\"");
        }
        if (childNodes.isEmpty()){
            sb. append(" />");
        }
        else {
            sb.append(">");
            appendChildHtml(sb, rdata);
            sb.append("</xxx:").append(type).append(">");
        }
    }

    public void appendChildHtml(StringBuilder sb, RequestData rdata){
        for (TemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }



}
