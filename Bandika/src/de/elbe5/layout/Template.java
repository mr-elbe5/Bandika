package de.elbe5.layout;

import de.elbe5.request.RequestData;

import java.util.ArrayList;
import java.util.List;

public class Template implements ITemplateNode {

    final String type;
    final String name;

    List<ITemplateNode> childNodes = new ArrayList<>();

    public Template(String type, String name) {
        this.type = type;
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public String getKey() {
        return "template." + name;
    }

    public List<ITemplateNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(ITemplateNode node){
        childNodes.add(node);
    }

    public String getHtml(RequestData rdata){
        StringBuilder sb = new StringBuilder();
        appendHtml(sb, rdata);
        return sb.toString();
    }

    public String getCode(){
        StringBuilder sb = new StringBuilder();
        appendCode(sb);
        return sb.toString();
    }

    public void appendHtml(StringBuilder sb, RequestData rdata) {
        for (ITemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

    @Override
    public void appendCode(StringBuilder sb) {
        for (ITemplateNode node : childNodes) {
            node.appendCode(sb);
        }
    }
}
