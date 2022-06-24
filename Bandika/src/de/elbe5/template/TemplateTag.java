package de.elbe5.template;

import de.elbe5.request.RequestData;

import java.util.*;

public abstract class TemplateTag implements ITemplateNode {

    public static final String TAG_PREFIX = "tpl";

    protected String type = "";
    protected Map<String,String> attributes = new HashMap<>();

    protected List<ITemplateNode> childNodes = new ArrayList<>();

    protected TemplateTag(){
    }

    public String getType() {
        return type;
    }

    public void addChildNode(ITemplateNode node){
        childNodes.add(node);
    }

    public void setAttributes(Map<String,String> attributes) {
        this.attributes = attributes;
    }

    public void appendTagStart(StringBuilder sb, RequestData rdata){
    }

    public void appendTagEnd(StringBuilder sb, RequestData rdata){
    }

    public void appendHtml(StringBuilder sb, RequestData rdata){
        appendTagStart(sb, rdata);
        if (!childNodes.isEmpty()) {
            appendInner(sb, rdata);
        }
        appendTagEnd(sb, rdata);
    }

    public String getStringAttribute(String key, String def){
        String result = attributes.get(key);
        if (result==null){
            return def;
        }
        return result;
    }

    public int getIntAttribute(String key, int def){
        try{
            String result = attributes.get(key);
            if (result==null){
                return def;
            }
            return Integer.parseInt(result);
        }
        catch (Exception e){
            return def;
        }
    }
    public boolean getBooleanAttribute(String key, boolean def){
        try{
            String result = attributes.get(key);
            if (result==null){
                return def;
            }
            return Boolean.parseBoolean(result);
        }
        catch (Exception e){
            return def;
        }
    }

    public void appendInner(StringBuilder sb, RequestData rdata){
        for (ITemplateNode node : childNodes) {
            node.appendHtml(sb, rdata);
        }
    }

}
